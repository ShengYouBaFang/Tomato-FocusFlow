package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants

/**
 * 倒计时服务
 * 负责在后台运行倒计时，并发送广播更新UI
 */
class TimerService : Service() {
    
    companion object {
        private const val TAG = "TimerService"
    }
    
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = 0
    private var totalTime: Long = 0
    private var isRunning = false
    private var wakeLock: PowerManager.WakeLock? = null
    
    override fun onCreate() {
        super.onCreate()
        // 创建通知渠道
        NotificationHelper.createNotificationChannel(this)
        
        // 获取唤醒锁
        acquireWakeLock()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START -> {
                val duration = intent.getLongExtra(Constants.EXTRA_DURATION, Constants.TIMER_25_MIN)
                startTimer(duration)
            }
            Constants.ACTION_PAUSE -> pauseTimer()
            Constants.ACTION_RESUME -> resumeTimer()
            Constants.ACTION_STOP -> stopTimer()
        }
        
        // 服务被杀死后重新创建
        return START_STICKY
    }
    
    /**
     * 开始倒计时
     */
    private fun startTimer(duration: Long) {
        Log.d(TAG, "开始倒计时: ${duration}ms")
        
        // 如果已有计时器在运行，先停止
        countDownTimer?.cancel()
        
        totalTime = duration
        remainingTime = duration
        isRunning = true
        
        // 发送开始广播
        sendTimerUpdateBroadcast()
        
        // 启动前台服务
        startForegroundService()
        
        // 创建新的倒计时器
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                Log.d(TAG, "倒计时更新: ${millisUntilFinished}ms")
                sendTimerUpdateBroadcast()
                updateNotification()
            }
            
            override fun onFinish() {
                Log.d(TAG, "倒计时完成")
                isRunning = false
                remainingTime = 0
                sendTimerFinishBroadcast()
                showFinishNotification()
            }
        }.start()
    }
    
    /**
     * 启动前台服务
     */
    private fun startForegroundService() {
        val notification = NotificationHelper.createTimerNotification(
            this, remainingTime, totalTime, isRunning
        )
        startForeground(Constants.NOTIFICATION_ID, notification)
    }
    
    /**
     * 更新通知
     */
    private fun updateNotification() {
        val notification = NotificationHelper.createTimerNotification(
            this, remainingTime, totalTime, isRunning
        )
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }
    
    /**
     * 获取唤醒锁，防止 CPU 休眠
     */
    private fun acquireWakeLock() {
        if (wakeLock?.isHeld != true) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FocusFlow::TimerWakeLock"
            ).apply {
                setReferenceCounted(false)
                acquire(60 * 60 * 1000L) // 最长持有1小时
            }
        }
    }
    
    /**
     * 释放唤醒锁
     */
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }
    
    /**
     * 暂停倒计时
     */
    private fun pauseTimer() {
        if (isRunning) {
            Log.d(TAG, "暂停倒计时")
            countDownTimer?.cancel()
            isRunning = false
            sendTimerUpdateBroadcast()
            updateNotification()
        }
    }
    
    /**
     * 继续倒计时
     */
    private fun resumeTimer() {
        if (!isRunning && remainingTime > 0) {
            Log.d(TAG, "继续倒计时: ${remainingTime}ms")
            isRunning = true
            startTimer(remainingTime)
        }
    }
    
    /**
     * 停止倒计时
     */
    private fun stopTimer() {
        Log.d(TAG, "停止倒计时")
        countDownTimer?.cancel()
        isRunning = false
        remainingTime = 0
        sendTimerUpdateBroadcast()
        updateNotification()
        
        // 停止前台服务
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    /**
     * 发送倒计时更新广播
     */
    private fun sendTimerUpdateBroadcast() {
        val intent = Intent(Constants.BROADCAST_TIMER_UPDATE).apply {
            putExtra(Constants.EXTRA_REMAINING_TIME, remainingTime)
            putExtra(Constants.EXTRA_DURATION, totalTime)
            putExtra("isRunning", isRunning)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    
    /**
     * 发送倒计时完成广播
     */
    private fun sendTimerFinishBroadcast() {
        val intent = Intent(Constants.BROADCAST_TIMER_FINISH)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    
    /**
     * 显示完成通知
     */
    private fun showFinishNotification() {
        val notification = NotificationHelper.createFinishNotification(this)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(Constants.NOTIFICATION_ID + 1, notification)
        
        // 停止前台服务
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "服务销毁")
        countDownTimer?.cancel()
        releaseWakeLock()
    }
}