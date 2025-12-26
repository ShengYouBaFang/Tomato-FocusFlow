package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.AppDatabase
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.WhiteNoiseRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.Constants
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.IOException
import android.database.sqlite.SQLiteException

/**
 * 倒计时前台服务
 * 管理倒计时逻辑，保持后台运行
 */
class TimerService : Service() {

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val whiteNoiseRepository = WhiteNoiseRepository()

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

    companion object {
        private const val TAG = "TimerService"
        private const val DB_TIMEOUT_MS = 5000L // 数据库操作超时时间
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        try {
            mediaPlayerManager = MediaPlayerManager(this)
            Log.d(TAG, "TimerService 创建成功")
        } catch (e: Exception) {
            Log.e(TAG, "创建 TimerService 时发生错误", e)
            ErrorHandler.handleServiceError("服务初始化失败", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            intent?.let { handleIntent(it) }
        } catch (e: Exception) {
            Log.e(TAG, "处理 Intent 时发生错误", e)
            ErrorHandler.handleServiceError("处理命令失败", e)
        }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "处理 action 时发生错误: ${intent.action}", e)
            ErrorHandler.handleTimerError("执行操作失败", e)
        }
    }

    /**
     * 开始倒计时
     */
    fun startTimer(duration: Long, noiseId: String) {
        try {
            totalDuration = duration
            remainingTime = duration
            startTime = System.currentTimeMillis()
            whiteNoiseId = noiseId

            // 根据noiseId查询白噪音名称
            val whiteNoise = whiteNoiseRepository.getWhiteNoiseById(noiseId)
            whiteNoiseName = whiteNoise?.name ?: "雨声"

            startForeground(Constants.NOTIFICATION_ID,
                NotificationHelper.createNotification(this, remainingTime, true))

            countDownTimer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
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
                    } catch (e: Exception) {
                        Log.e(TAG, "倒计时更新失败", e)
                        ErrorHandler.handleTimerError("倒计时更新失败", e)
                    }
                }

                override fun onFinish() {
                    try {
                        isRunning = false
                        onTimerComplete()
                        _timerState.postValue(TimerState.Completed)
                        Log.d(TAG, "倒计时完成")
                    } catch (e: Exception) {
                        Log.e(TAG, "倒计时完成处理失败", e)
                        ErrorHandler.handleTimerError("倒计时完成处理失败", e)
                    }
                }
            }.start()

            isRunning = true
            Log.d(TAG, "倒计时已开始: duration=${duration}ms, noiseId=$noiseId")

        } catch (e: IllegalStateException) {
            Log.e(TAG, "启动倒计时时状态异常", e)
            ErrorHandler.handleTimerError("启动倒计时失败", e)
            _timerState.postValue(TimerState.Idle)
        } catch (e: Exception) {
            Log.e(TAG, "启动倒计时时发生未知错误", e)
            ErrorHandler.handleTimerError("启动倒计时失败", e)
            _timerState.postValue(TimerState.Idle)
        }
    }

    /**
     * 暂停倒计时
     */
    fun pauseTimer() {
        try {
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
            Log.d(TAG, "倒计时已暂停")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "暂停倒计时时状态异常", e)
            ErrorHandler.handleTimerError("暂停倒计时失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "暂停倒计时时发生错误", e)
            ErrorHandler.handleTimerError("暂停倒计时失败", e)
        }
    }

    /**
     * 恢复倒计时
     */
    fun resumeTimer() {
        try {
            countDownTimer = object : CountDownTimer(remainingTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
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
                    } catch (e: Exception) {
                        Log.e(TAG, "倒计时更新失败", e)
                        ErrorHandler.handleTimerError("倒计时更新失败", e)
                    }
                }

                override fun onFinish() {
                    try {
                        isRunning = false
                        onTimerComplete()
                        _timerState.postValue(TimerState.Completed)
                        Log.d(TAG, "倒计时完成")
                    } catch (e: Exception) {
                        Log.e(TAG, "倒计时完成处理失败", e)
                        ErrorHandler.handleTimerError("倒计时完成处理失败", e)
                    }
                }
            }.start()

            isRunning = true
            Log.d(TAG, "倒计时已恢复")

        } catch (e: IllegalStateException) {
            Log.e(TAG, "恢复倒计时时状态异常", e)
            ErrorHandler.handleTimerError("恢复倒计时失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "恢复倒计时时发生错误", e)
            ErrorHandler.handleTimerError("恢复倒计时失败", e)
        }
    }

    /**
     * 停止倒计时
     */
    fun stopTimer() {
        try {
            countDownTimer?.cancel()
            isRunning = false
            _timerState.postValue(TimerState.Idle)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            Log.d(TAG, "倒计时已停止")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "停止倒计时时状态异常", e)
            ErrorHandler.handleTimerError("停止倒计时失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "停止倒计时时发生错误", e)
            ErrorHandler.handleTimerError("停止倒计时失败", e)
        }
    }

    /**
     * 重置倒计时
     */
    fun resetTimer() {
        try {
            countDownTimer?.cancel()
            isRunning = false
            mediaPlayerManager?.stop()
            _timerState.postValue(TimerState.Idle)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            Log.d(TAG, "倒计时已重置")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "重置倒计时时状态异常", e)
            ErrorHandler.handleTimerError("重置倒计时失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "重置倒计时时发生错误", e)
            ErrorHandler.handleTimerError("重置倒计时失败", e)
        }
    }

    /**
     * 倒计时完成
     */
    private fun onTimerComplete() {
        try {
            // 保存完成记录到数据库
            saveSession(true)
            stopForeground(STOP_FOREGROUND_REMOVE)
            Log.d(TAG, "倒计时完成回调执行成功")
        } catch (e: Exception) {
            Log.e(TAG, "倒计时完成回调执行失败", e)
            ErrorHandler.handleTimerError("倒计时完成处理失败", e)
        }
    }

    /**
     * 保存专注记录
     */
    private fun saveSession(isCompleted: Boolean) {
        serviceScope.launch(Dispatchers.IO) {
            try {
                // 使用 withTimeout 避免数据库操作hang住
                withTimeout(DB_TIMEOUT_MS) {
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

                    Log.d(TAG, "专注记录保存成功: duration=${totalDuration}ms, completed=$isCompleted")
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                Log.e(TAG, "保存专注记录超时", e)
                ErrorHandler.handleDatabaseError("保存记录超时，数据库可能繁忙", e)
            } catch (e: SQLiteException) {
                Log.e(TAG, "数据库错误：保存专注记录失败", e)
                ErrorHandler.handleDatabaseError("数据库写入失败：${e.message}", e)
            } catch (e: IOException) {
                Log.e(TAG, "IO错误：保存专注记录失败", e)
                ErrorHandler.handleDatabaseError("存储空间不足或访问被拒绝", e)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "状态异常：保存专注记录失败", e)
                ErrorHandler.handleDatabaseError("数据库状态异常", e)
            } catch (e: Exception) {
                Log.e(TAG, "未知错误：保存专注记录失败", e)
                ErrorHandler.handleDatabaseError("保存记录失败：${e.message}", e)
            }
        }
    }

    /**
     * 更新通知
     */
    private fun updateNotification() {
        try {
            val notification = NotificationHelper.createNotification(this, remainingTime, isRunning)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.notify(Constants.NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "更新通知时权限不足", e)
            ErrorHandler.handleServiceError("更新通知失败：权限不足", e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "更新通知时状态异常", e)
            ErrorHandler.handleServiceError("更新通知失败：状态异常", e)
        } catch (e: Exception) {
            Log.e(TAG, "更新通知时发生未知错误", e)
            ErrorHandler.handleServiceError("更新通知失败", e)
        }
    }

    /**
     * 播放白噪音
     */
    fun playWhiteNoise(resourceId: Int) {
        try {
            mediaPlayerManager?.play(resourceId)
            Log.d(TAG, "开始播放白噪音: resourceId=$resourceId")
        } catch (e: Exception) {
            Log.e(TAG, "播放白噪音时发生错误", e)
            ErrorHandler.handleAudioError("播放白噪音失败", e)
        }
    }

    /**
     * 暂停白噪音
     */
    fun pauseWhiteNoise() {
        try {
            mediaPlayerManager?.pause()
            Log.d(TAG, "白噪音已暂停")
        } catch (e: Exception) {
            Log.e(TAG, "暂停白噪音时发生错误", e)
            ErrorHandler.handleAudioError("暂停白噪音失败", e)
        }
    }

    /**
     * 恢复白噪音
     */
    fun resumeWhiteNoise() {
        try {
            mediaPlayerManager?.resume()
            Log.d(TAG, "白噪音已恢复")
        } catch (e: Exception) {
            Log.e(TAG, "恢复白噪音时发生错误", e)
            ErrorHandler.handleAudioError("恢复白噪音失败", e)
        }
    }

    /**
     * 停止白噪音
     */
    fun stopWhiteNoise() {
        try {
            mediaPlayerManager?.stop()
            Log.d(TAG, "白噪音已停止")
        } catch (e: Exception) {
            Log.e(TAG, "停止白噪音时发生错误", e)
            ErrorHandler.handleAudioError("停止白噪音失败", e)
        }
    }

    override fun onDestroy() {
        try {
            countDownTimer?.cancel()
            mediaPlayerManager?.release()
            Log.d(TAG, "TimerService 已销毁")
        } catch (e: Exception) {
            Log.e(TAG, "销毁 TimerService 时发生错误", e)
            ErrorHandler.handleServiceError("服务销毁失败", e)
        } finally {
            super.onDestroy()
        }
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
