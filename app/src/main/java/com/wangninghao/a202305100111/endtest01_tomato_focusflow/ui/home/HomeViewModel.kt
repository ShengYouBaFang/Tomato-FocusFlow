package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.home

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.WhiteNoiseRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.service.TimerService
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.Constants
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.ErrorHandler
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.PreferenceHelper

/**
 * 主界面ViewModel
 * 管理倒计时状态和白噪音播放
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelper(application)
    private val whiteNoiseRepository = WhiteNoiseRepository()

    private var timerService: TimerService? = null
    private var isBound = false
    private var bindAttempts = 0
    private val maxBindAttempts = 3

    companion object {
        private const val TAG = "HomeViewModel"
    }

    // 当前选中的白噪音
    private val _selectedWhiteNoise = MutableLiveData<WhiteNoise>()
    val selectedWhiteNoise: LiveData<WhiteNoise> = _selectedWhiteNoise

    // 白噪音是否开启
    private val _whiteNoiseEnabled = MutableLiveData<Boolean>()
    val whiteNoiseEnabled: LiveData<Boolean> = _whiteNoiseEnabled

    // 倒计时时长
    private val _timerDuration = MutableLiveData<Long>()
    val timerDuration: LiveData<Long> = _timerDuration

    // 倒计时状态
    private val _timerState = MutableLiveData<TimerService.TimerState>(TimerService.TimerState.Idle)
    val timerState: LiveData<TimerService.TimerState> = _timerState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                val binder = service as TimerService.TimerBinder
                timerService = binder.getService()
                isBound = true
                bindAttempts = 0

                // 观察Service的timer状态并同步到ViewModel
                timerService?.timerState?.observeForever { state ->
                    _timerState.value = state
                }

                Log.d(TAG, "服务绑定成功")
            } catch (e: ClassCastException) {
                Log.e(TAG, "服务绑定失败：Binder类型错误", e)
                ErrorHandler.handleServiceError("服务绑定失败", e)
                timerService = null
                isBound = false
            } catch (e: Exception) {
                Log.e(TAG, "服务绑定失败：未知错误", e)
                ErrorHandler.handleServiceError("服务绑定失败", e)
                timerService = null
                isBound = false
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                Log.w(TAG, "服务意外断开连接")
                timerService = null
                isBound = false
                _timerState.value = TimerService.TimerState.Idle

                // 如果不是主动解绑，尝试重新绑定
                if (bindAttempts < maxBindAttempts) {
                    bindAttempts++
                    Log.d(TAG, "尝试重新绑定服务 ($bindAttempts/$maxBindAttempts)")
                    // 延迟重试避免过于频繁
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        bindService(getApplication())
                    }, 1000)
                } else {
                    ErrorHandler.handleServiceError("服务连接失败，已达最大重试次数", null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理服务断开时发生错误", e)
                ErrorHandler.handleServiceError("处理服务断开失败", e)
            }
        }
    }

    init {
        try {
            loadSettings()
        } catch (e: Exception) {
            Log.e(TAG, "初始化ViewModel时发生错误", e)
            ErrorHandler.handleUnknownError("初始化失败", e)
        }
    }

    /**
     * 加载设置
     */
    private fun loadSettings() {
        try {
            val noiseId = preferenceHelper.getSelectedWhiteNoiseId()
            val noise = whiteNoiseRepository.getWhiteNoiseById(noiseId)
                ?: whiteNoiseRepository.getDefaultWhiteNoise()
            _selectedWhiteNoise.value = noise

            val duration = preferenceHelper.getDefaultTimerDuration()
            _timerDuration.value = duration

            val whiteNoiseEnabled = preferenceHelper.isWhiteNoiseEnabled()
            _whiteNoiseEnabled.value = whiteNoiseEnabled

            Log.d(TAG, "设置加载成功: noiseId=$noiseId, duration=$duration, whiteNoiseEnabled=$whiteNoiseEnabled")
        } catch (e: Exception) {
            Log.e(TAG, "加载设置失败", e)
            ErrorHandler.handleUnknownError("加载设置失败", e)

            // 使用默认值
            _selectedWhiteNoise.value = whiteNoiseRepository.getDefaultWhiteNoise()
            _timerDuration.value = Constants.QUICK_TIMER_25_MIN
            _whiteNoiseEnabled.value = true
        }
    }

    /**
     * 刷新设置（从SharedPreferences重新加载）
     */
    fun refreshSettings() {
        try {
            // 重新加载设置以更新UI显示
            loadSettings()
            Log.d(TAG, "设置已刷新")
        } catch (e: Exception) {
            Log.e(TAG, "刷新设置失败", e)
            ErrorHandler.handleUnknownError("刷新设置失败", e)
        }
    }

    /**
     * 绑定Service
     */
    fun bindService(context: Context) {
        try {
            if (isBound) {
                Log.d(TAG, "服务已绑定，跳过")
                return
            }

            val intent = Intent(context, TimerService::class.java)
            val bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

            if (!bindResult) {
                Log.e(TAG, "bindService返回false")
                ErrorHandler.handleServiceError("服务绑定失败", null)
            } else {
                Log.d(TAG, "开始绑定服务")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "绑定服务失败：权限不足", e)
            ErrorHandler.handleServiceError("绑定服务失败：权限不足", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "绑定服务失败：参数错误", e)
            ErrorHandler.handleServiceError("绑定服务失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "绑定服务失败：未知错误", e)
            ErrorHandler.handleServiceError("绑定服务失败", e)
        }
    }

    /**
     * 解绑Service
     */
    fun unbindService(context: Context) {
        try {
            if (isBound) {
                context.unbindService(serviceConnection)
                isBound = false
                bindAttempts = 0
                Log.d(TAG, "服务已解绑")
            }
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "解绑服务失败：服务未绑定", e)
            // 解绑失败不是致命错误，只记录日志
            isBound = false
        } catch (e: Exception) {
            Log.e(TAG, "解绑服务失败：未知错误", e)
            ErrorHandler.handleServiceError("解绑服务失败", e)
            isBound = false
        }
    }

    /**
     * 开始倒计时
     */
    fun startTimer(context: Context) {
        try {
            val duration = _timerDuration.value ?: Constants.QUICK_TIMER_25_MIN
            val noiseId = _selectedWhiteNoise.value?.id ?: "rain"
            val whiteNoiseEnabled = _whiteNoiseEnabled.value ?: true

            val intent = Intent(context, TimerService::class.java).apply {
                action = Constants.ACTION_START_TIMER
                putExtra(Constants.EXTRA_TIMER_DURATION, duration)
                putExtra(Constants.EXTRA_WHITE_NOISE_ID, noiseId)
                putExtra(Constants.EXTRA_WHITE_NOISE_ENABLED, whiteNoiseEnabled)
            }

            context.startForegroundService(intent)

            // 只有开关打开时才播放白噪音
            if (whiteNoiseEnabled) {
                _selectedWhiteNoise.value?.let {
                    timerService?.playWhiteNoise(it.audioRes)
                }
            }

            Log.d(TAG, "开始计时器: duration=$duration, noiseId=$noiseId, whiteNoiseEnabled=$whiteNoiseEnabled")
        } catch (e: SecurityException) {
            Log.e(TAG, "启动前台服务失败：权限不足", e)
            ErrorHandler.handleServiceError("启动计时器失败：权限不足", e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "启动前台服务失败：状态异常", e)
            ErrorHandler.handleServiceError("启动计时器失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "启动计时器失败：未知错误", e)
            ErrorHandler.handleTimerError("启动计时器失败", e)
        }
    }

    /**
     * 暂停倒计时
     */
    fun pauseTimer() {
        try {
            timerService?.pauseTimer()
            // 暂停时总是暂停白噪音
            timerService?.pauseWhiteNoise()
            Log.d(TAG, "计时器已暂停")
        } catch (e: Exception) {
            Log.e(TAG, "暂停计时器失败", e)
            ErrorHandler.handleTimerError("暂停计时器失败", e)
        }
    }

    /**
     * 恢复倒计时
     */
    fun resumeTimer() {
        try {
            timerService?.resumeTimer()
            // 只有当白噪音开关打开时才播放
            if (_whiteNoiseEnabled.value == true) {
                // 播放当前选择的白噪音（而不是简单恢复），以支持暂停期间切换白噪音
                _selectedWhiteNoise.value?.let { whiteNoise ->
                    timerService?.playWhiteNoise(whiteNoise.audioRes)
                }
            }
            Log.d(TAG, "计时器已恢复，白噪音开关状态：${_whiteNoiseEnabled.value}")
        } catch (e: Exception) {
            Log.e(TAG, "恢复计时器失败", e)
            ErrorHandler.handleTimerError("恢复计时器失败", e)
        }
    }

    /**
     * 停止倒计时
     */
    fun stopTimer() {
        try {
            timerService?.stopTimer()
            Log.d(TAG, "计时器已停止")
        } catch (e: Exception) {
            Log.e(TAG, "停止计时器失败", e)
            ErrorHandler.handleTimerError("停止计时器失败", e)
        }
    }

    /**
     * 重置倒计时
     */
    fun resetTimer(context: Context) {
        try {
            timerService?.resetTimer()
            _timerState.value = TimerService.TimerState.Idle
            Log.d(TAG, "计时器已重置")
        } catch (e: Exception) {
            Log.e(TAG, "重置计时器失败", e)
            ErrorHandler.handleTimerError("重置计时器失败", e)
            // 即使失败也设置为Idle状态
            _timerState.value = TimerService.TimerState.Idle
        }
    }

    /**
     * 设置倒计时时长
     */
    fun setTimerDuration(duration: Long) {
        try {
            _timerDuration.value = duration
            preferenceHelper.setDefaultTimerDuration(duration)
            Log.d(TAG, "计时器时长已设置: $duration")
        } catch (e: Exception) {
            Log.e(TAG, "设置计时器时长失败", e)
            ErrorHandler.handleUnknownError("设置计时器时长失败", e)
        }
    }

    /**
     * 设置选中的白噪音
     */
    fun setSelectedWhiteNoise(whiteNoise: WhiteNoise) {
        try {
            _selectedWhiteNoise.value = whiteNoise
            preferenceHelper.setSelectedWhiteNoiseId(whiteNoise.id)
            Log.d(TAG, "白噪音已设置: ${whiteNoise.name}")
        } catch (e: Exception) {
            Log.e(TAG, "设置白噪音失败", e)
            ErrorHandler.handleUnknownError("设置白噪音失败", e)
        }
    }

    /**
     * 设置白噪音是否开启
     */
    fun setWhiteNoiseEnabled(enabled: Boolean) {
        try {
            _whiteNoiseEnabled.value = enabled
            preferenceHelper.setWhiteNoiseEnabled(enabled)

            // 只在倒计时运行状态下立即控制白噪音播放
            // 暂停状态下不操作，等继续时根据开关状态决定
            if (_timerState.value is TimerService.TimerState.Running) {
                if (enabled) {
                    _selectedWhiteNoise.value?.let {
                        timerService?.playWhiteNoise(it.audioRes)
                    }
                } else {
                    timerService?.stopWhiteNoise()
                }
            }

            Log.d(TAG, "白噪音开关已设置: $enabled，当前状态: ${_timerState.value}")
        } catch (e: Exception) {
            Log.e(TAG, "设置白噪音开关失败", e)
            ErrorHandler.handleUnknownError("设置白噪音开关失败", e)
        }
    }

    override fun onCleared() {
        try {
            super.onCleared()
            timerService = null
            Log.d(TAG, "ViewModel已清理")
        } catch (e: Exception) {
            Log.e(TAG, "清理ViewModel时发生错误", e)
        }
    }
}
