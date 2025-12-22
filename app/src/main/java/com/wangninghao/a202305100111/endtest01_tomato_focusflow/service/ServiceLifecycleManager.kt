package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants

/**
 * Service 生命周期管理器
 * 负责管理 TimerService 的生命周期和保活机制
 */
class ServiceLifecycleManager(private val context: Context) {
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var screenOffReceiver: BroadcastReceiver? = null
    
    /**
     * 获取唤醒锁，防止 CPU 休眠
     */
    fun acquireWakeLock() {
        if (wakeLock?.isHeld != true) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
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
    fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }
    
    /**
     * 注册屏幕关闭接收器
     * 当屏幕关闭时重新获取唤醒锁
     */
    fun registerScreenOffReceiver() {
        screenOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                    acquireWakeLock()
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        
        context.registerReceiver(screenOffReceiver, filter)
    }
    
    /**
     * 注销屏幕关闭接收器
     */
    fun unregisterScreenOffReceiver() {
        screenOffReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // 接收器未注册，忽略异常
            }
        }
        screenOffReceiver = null
    }
    
    /**
     * 启动 TimerService
     */
    fun startTimerService(duration: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = Constants.ACTION_START
            putExtra(Constants.EXTRA_DURATION, duration)
        }
        context.startService(intent)
    }
    
    /**
     * 暂停 TimerService
     */
    fun pauseTimerService() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = Constants.ACTION_PAUSE
        }
        context.startService(intent)
    }
    
    /**
     * 继续 TimerService
     */
    fun resumeTimerService() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = Constants.ACTION_RESUME
        }
        context.startService(intent)
    }
    
    /**
     * 停止 TimerService
     */
    fun stopTimerService() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = Constants.ACTION_STOP
        }
        context.startService(intent)
    }
    
    /**
     * 销毁资源
     */
    fun destroy() {
        releaseWakeLock()
        unregisterScreenOffReceiver()
    }
}