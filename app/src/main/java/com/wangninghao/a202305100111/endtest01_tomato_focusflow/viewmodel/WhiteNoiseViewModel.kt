package com.wangninghao.a202305100111.endtest01_tomato_focusflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio.WhiteNoisePlayer
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.PreferencesRepository
import kotlinx.coroutines.launch

/**
 * 白噪音 ViewModel
 * 负责管理音频播放状态和设置
 */
class WhiteNoiseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val whiteNoisePlayer = WhiteNoisePlayer(application)
    private val preferencesRepository = PreferencesRepository(application)
    
    // 播放状态
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying
    
    // 当前音量
    private val _currentVolume = MutableLiveData<Float>()
    val currentVolume: LiveData<Float> = _currentVolume
    
    // 当前播放的音频类型
    private val _currentNoiseType = MutableLiveData<String?>()
    val currentNoiseType: LiveData<String?> = _currentNoiseType
    
    // 音量设置
    val volumeSetting = preferencesRepository.volume
    
    init {
        _isPlaying.value = false
        _currentVolume.value = 0.5f
        _currentNoiseType.value = null
    }
    
    /**
     * 播放音频
     * @param noiseType 音频类型
     * @param audioRes 音频资源ID
     */
    fun playAudio(noiseType: String, audioRes: Int) {
        viewModelScope.launch {
            // 获取保存的音量设置
            preferencesRepository.volume.collect {
                whiteNoisePlayer.play(audioRes, it)
                _isPlaying.value = true
                _currentNoiseType.value = noiseType
                _currentVolume.value = it
            }
        }
    }
    
    /**
     * 暂停播放
     */
    fun pauseAudio() {
        whiteNoisePlayer.pause()
        _isPlaying.value = false
    }
    
    /**
     * 恢复播放
     */
    fun resumeAudio() {
        whiteNoisePlayer.resume()
        _isPlaying.value = true
    }
    
    /**
     * 停止播放
     */
    fun stopAudio() {
        whiteNoisePlayer.stop()
        _isPlaying.value = false
        _currentNoiseType.value = null
    }
    
    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        whiteNoisePlayer.setVolume(volume)
        _currentVolume.value = volume
        
        // 保存音量设置
        viewModelScope.launch {
            preferencesRepository.setVolume(volume)
        }
    }
    
    /**
     * 获取当前音量
     */
    fun getCurrentVolume(): Float {
        return whiteNoisePlayer.getCurrentVolume()
    }
    
    override fun onCleared() {
        super.onCleared()
        whiteNoisePlayer.release()
    }
}