package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.home

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.WhiteNoiseRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.service.TimerService
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.Constants
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

    // 当前选中的白噪音
    private val _selectedWhiteNoise = MutableLiveData<WhiteNoise>()
    val selectedWhiteNoise: LiveData<WhiteNoise> = _selectedWhiteNoise

    // 倒计时时长
    private val _timerDuration = MutableLiveData<Long>()
    val timerDuration: LiveData<Long> = _timerDuration

    // 倒计时状态
    private val _timerState = MutableLiveData<TimerService.TimerState>(TimerService.TimerState.Idle)
    val timerState: LiveData<TimerService.TimerState> = _timerState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true

            // 观察Service的timer状态并同步到ViewModel
            timerService?.timerState?.observeForever { state ->
                _timerState.value = state
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
            _timerState.value = TimerService.TimerState.Idle
        }
    }

    init {
        loadSettings()
    }

    /**
     * 加载设置
     */
    private fun loadSettings() {
        val noiseId = preferenceHelper.getSelectedWhiteNoiseId()
        val noise = whiteNoiseRepository.getWhiteNoiseById(noiseId)
            ?: whiteNoiseRepository.getDefaultWhiteNoise()
        _selectedWhiteNoise.value = noise

        val duration = preferenceHelper.getDefaultTimerDuration()
        _timerDuration.value = duration
    }

    /**
     * 刷新设置（从SharedPreferences重新加载）
     */
    fun refreshSettings() {
        loadSettings()
    }

    /**
     * 绑定Service
     */
    fun bindService(context: Context) {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 解绑Service
     */
    fun unbindService(context: Context) {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    /**
     * 开始倒计时
     */
    fun startTimer(context: Context) {
        val duration = _timerDuration.value ?: Constants.QUICK_TIMER_10_MIN
        val noiseId = _selectedWhiteNoise.value?.id ?: "rain"

        val intent = Intent(context, TimerService::class.java).apply {
            action = Constants.ACTION_START_TIMER
            putExtra(Constants.EXTRA_TIMER_DURATION, duration)
            putExtra(Constants.EXTRA_WHITE_NOISE_ID, noiseId)
        }

        context.startForegroundService(intent)

        // 播放白噪音
        _selectedWhiteNoise.value?.let {
            timerService?.playWhiteNoise(it.audioRes)
        }
    }

    /**
     * 暂停倒计时
     */
    fun pauseTimer() {
        timerService?.pauseTimer()
        timerService?.pauseWhiteNoise()
    }

    /**
     * 恢复倒计时
     */
    fun resumeTimer() {
        timerService?.resumeTimer()
        timerService?.resumeWhiteNoise()
    }

    /**
     * 停止倒计时
     */
    fun stopTimer() {
        timerService?.stopTimer()
    }

    /**
     * 重置倒计时
     */
    fun resetTimer(context: Context) {
        timerService?.resetTimer()
        _timerState.value = TimerService.TimerState.Idle
    }

    /**
     * 设置倒计时时长
     */
    fun setTimerDuration(duration: Long) {
        _timerDuration.value = duration
        preferenceHelper.setDefaultTimerDuration(duration)
    }

    /**
     * 设置选中的白噪音
     */
    fun setSelectedWhiteNoise(whiteNoise: WhiteNoise) {
        _selectedWhiteNoise.value = whiteNoise
        preferenceHelper.setSelectedWhiteNoiseId(whiteNoise.id)
    }

    override fun onCleared() {
        super.onCleared()
        timerService = null
    }
}
