package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.AppDatabase
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 倒计时前台服务
 * 管理倒计时逻辑，保持后台运行
 */
class TimerService : Service() {

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayerManager: MediaPlayerManager? = null

    // 倒计时状态
    private var totalDuration: Long = 0
    private var remainingTime: Long = 0
    private var startTime: Long = 0
    private var whiteNoiseId: String = "rain"
    private var whiteNoiseName: String = "雨声"
    private var isRunning: Boolean = false

    // LiveData
    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState> = _timerState

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        mediaPlayerManager = MediaPlayerManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Constants.ACTION_START_TIMER -> {
                val duration = intent.getLongExtra(Constants.EXTRA_TIMER_DURATION, 25 * 60 * 1000L)
                val noiseId = intent.getStringExtra(Constants.EXTRA_WHITE_NOISE_ID) ?: "rain"
                startTimer(duration, noiseId)
            }
            Constants.ACTION_PAUSE_TIMER -> pauseTimer()
            Constants.ACTION_RESUME_TIMER -> resumeTimer()
            Constants.ACTION_STOP_TIMER -> stopTimer()
        }
    }

    /**
     * 开始倒计时
     */
    fun startTimer(duration: Long, noiseId: String) {
        totalDuration = duration
        remainingTime = duration
        startTime = System.currentTimeMillis()
        whiteNoiseId = noiseId

        startForeground(Constants.NOTIFICATION_ID,
            NotificationHelper.createNotification(this, remainingTime, true))

        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                isRunning = true
                updateNotification()
                _timerState.postValue(
                    TimerState.Running(
                        remainingTime,
                        totalDuration,
                        (totalDuration - remainingTime).toFloat() / totalDuration
                    )
                )
            }

            override fun onFinish() {
                isRunning = false
                onTimerComplete()
                _timerState.postValue(TimerState.Completed)
            }
        }.start()

        isRunning = true
    }

    /**
     * 暂停倒计时
     */
    fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        updateNotification()
        _timerState.postValue(
            TimerState.Paused(
                remainingTime,
                totalDuration,
                (totalDuration - remainingTime).toFloat() / totalDuration
            )
        )
    }

    /**
     * 恢复倒计时
     */
    fun resumeTimer() {
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                isRunning = true
                updateNotification()
                _timerState.postValue(
                    TimerState.Running(
                        remainingTime,
                        totalDuration,
                        (totalDuration - remainingTime).toFloat() / totalDuration
                    )
                )
            }

            override fun onFinish() {
                isRunning = false
                onTimerComplete()
                _timerState.postValue(TimerState.Completed)
            }
        }.start()

        isRunning = true
    }

    /**
     * 停止倒计时
     */
    fun stopTimer() {
        countDownTimer?.cancel()
        isRunning = false
        _timerState.postValue(TimerState.Idle)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * 倒计时完成
     */
    private fun onTimerComplete() {
        // 保存完成记录到数据库
        saveSession(true)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    /**
     * 保存专注记录
     */
    private fun saveSession(isCompleted: Boolean) {
        serviceScope.launch(Dispatchers.IO) {
            val session = FocusSessionEntity(
                duration = totalDuration,
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                whiteNoiseName = whiteNoiseName,
                whiteNoiseIcon = whiteNoiseId,
                isCompleted = isCompleted
            )

            val database = AppDatabase.getDatabase(applicationContext)
            database.focusSessionDao().insert(session)
        }
    }

    /**
     * 更新通知
     */
    private fun updateNotification() {
        val notification = NotificationHelper.createNotification(this, remainingTime, isRunning)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

    /**
     * 播放白噪音
     */
    fun playWhiteNoise(resourceId: Int) {
        mediaPlayerManager?.play(resourceId)
    }

    /**
     * 暂停白噪音
     */
    fun pauseWhiteNoise() {
        mediaPlayerManager?.pause()
    }

    /**
     * 恢复白噪音
     */
    fun resumeWhiteNoise() {
        mediaPlayerManager?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayerManager?.release()
    }

    /**
     * 倒计时状态
     */
    sealed class TimerState {
        object Idle : TimerState()
        data class Running(
            val remainingTime: Long,
            val totalDuration: Long,
            val progress: Float
        ) : TimerState()
        data class Paused(
            val remainingTime: Long,
            val totalDuration: Long,
            val progress: Float
        ) : TimerState()
        object Completed : TimerState()
    }
}
