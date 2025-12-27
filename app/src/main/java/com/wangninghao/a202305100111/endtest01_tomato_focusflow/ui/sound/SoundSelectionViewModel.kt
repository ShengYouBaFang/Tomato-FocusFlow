package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.sound

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
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.PreferenceHelper

/**
 * 白噪音选择ViewModel
 */
class SoundSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelper(application)
    private val whiteNoiseRepository = WhiteNoiseRepository()

    private var timerService: TimerService? = null
    private var isBound = false

    companion object {
        private const val TAG = "SoundSelectionViewModel"
    }

    // 所有白噪音列表
    private val _whiteNoiseList = MutableLiveData<List<WhiteNoise>>()
    val whiteNoiseList: LiveData<List<WhiteNoise>> = _whiteNoiseList

    // 当前选中的白噪音
    private val _selectedWhiteNoise = MutableLiveData<WhiteNoise>()
    val selectedWhiteNoise: LiveData<WhiteNoise> = _selectedWhiteNoise

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                val binder = service as TimerService.TimerBinder
                timerService = binder.getService()
                isBound = true
                Log.d(TAG, "TimerService 绑定成功")
            } catch (e: Exception) {
                Log.e(TAG, "绑定 TimerService 失败", e)
                timerService = null
                isBound = false
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
            Log.w(TAG, "TimerService 连接断开")
        }
    }

    init {
        loadWhiteNoiseList()
        loadSelectedWhiteNoise()
    }

    /**
     * 绑定TimerService
     */
    fun bindService(context: Context) {
        if (!isBound) {
            val intent = Intent(context, TimerService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * 解绑TimerService
     */
    fun unbindService(context: Context) {
        if (isBound) {
            try {
                context.unbindService(serviceConnection)
                isBound = false
            } catch (e: Exception) {
                Log.e(TAG, "解绑服务失败", e)
                isBound = false
            }
        }
    }

    /**
     * 加载白噪音列表
     */
    private fun loadWhiteNoiseList() {
        _whiteNoiseList.value = whiteNoiseRepository.getAllWhiteNoises()
    }

    /**
     * 加载当前选中的白噪音
     */
    private fun loadSelectedWhiteNoise() {
        val selectedId = preferenceHelper.getSelectedWhiteNoiseId()
        val noise = whiteNoiseRepository.getWhiteNoiseById(selectedId)
            ?: whiteNoiseRepository.getDefaultWhiteNoise()
        _selectedWhiteNoise.value = noise
    }

    /**
     * 选择白噪音（支持即时切换）
     */
    fun selectWhiteNoise(whiteNoise: WhiteNoise) {
        _selectedWhiteNoise.value = whiteNoise
        preferenceHelper.setSelectedWhiteNoiseId(whiteNoise.id)

        // 检查是否需要即时切换播放
        val isWhiteNoiseEnabled = preferenceHelper.isWhiteNoiseEnabled()
        val isTimerRunning = timerService?.timerState?.value is TimerService.TimerState.Running

        if (isTimerRunning && isWhiteNoiseEnabled) {
            // 立即切换播放新的白噪音
            timerService?.playWhiteNoise(whiteNoise.audioRes)
            Log.d(TAG, "即时切换白噪音: ${whiteNoise.name}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerService = null
    }
}
