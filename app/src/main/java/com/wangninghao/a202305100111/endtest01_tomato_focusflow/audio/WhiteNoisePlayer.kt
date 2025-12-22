package com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RawRes
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants

/**
 * 白噪音播放器
 * 负责播放各种白噪音音频文件
 */
class WhiteNoisePlayer(private val context: Context) {
    
    companion object {
        private const val TAG = "WhiteNoisePlayer"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var currentAudioRes: Int = -1
    private var currentVolume: Float = 0.5f
    private var isPlaying = false
    
    init {
        // 初始化音频焦点请求（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusListener)
                .build()
        }
    }
    
    /**
     * 播放音频
     * @param audioRes 音频资源ID
     * @param volume 音量（0.0 - 1.0）
     */
    fun play(@RawRes audioRes: Int, volume: Float = 0.5f) {
        if (currentAudioRes == audioRes && isPlaying) {
            // 如果正在播放相同的音频，只调整音量
            setVolume(volume)
            return
        }
        
        // 停止当前播放
        stop()
        
        currentAudioRes = audioRes
        currentVolume = volume
        
        try {
            // 请求音频焦点
            if (requestAudioFocus()) {
                // 创建MediaPlayer
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    
                    // 设置音频源
                    setDataSource(context, Uri.parse("android.resource://${context.packageName}/$audioRes"))
                    
                    // 设置循环播放
                    isLooping = true
                    
                    // 设置音量
                    setVolume(volume, volume)
                    
                    // 准备并开始播放
                    prepareAsync()
                    
                    setOnPreparedListener {
                        start()
                        isPlaying = true
                        Log.d(TAG, "开始播放音频: $audioRes")
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "播放音频出错: what=$what, extra=$extra")
                        isPlaying = false
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "播放音频失败", e)
            isPlaying = false
        }
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                Log.d(TAG, "暂停播放")
            }
        }
    }
    
    /**
     * 恢复播放
     */
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                // 恢复音频焦点
                if (requestAudioFocus()) {
                    it.start()
                    isPlaying = true
                    Log.d(TAG, "恢复播放")
                }
            }
        }
    }
    
    /**
     * 停止播放
     */
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            isPlaying = false
            Log.d(TAG, "停止播放")
        }
        mediaPlayer = null
        
        // 放弃音频焦点
        abandonAudioFocus()
    }
    
    /**
     * 设置音量
     */
    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        mediaPlayer?.setVolume(currentVolume, currentVolume)
        Log.d(TAG, "设置音量: $volume")
    }
    
    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return isPlaying
    }
    
    /**
     * 获取当前音量
     */
    fun getCurrentVolume(): Float {
        return currentVolume
    }
    
    /**
     * 请求音频焦点
     */
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                val result = audioManager.requestAudioFocus(it)
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                audioFocusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    /**
     * 放弃音频焦点
     */
    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusListener)
        }
    }
    
    /**
     * 音频焦点变化监听器
     */
    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // 长时间失去音频焦点，停止播放
                stop()
                Log.d(TAG, "长时间失去音频焦点，停止播放")
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // 短暂失去音频焦点，暂停播放
                pause()
                Log.d(TAG, "短暂失去音频焦点，暂停播放")
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // 短暂失去音频焦点但可以降低音量
                setVolume(currentVolume * 0.3f)
                Log.d(TAG, "短暂失去音频焦点但可以降低音量")
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // 重新获得音频焦点
                setVolume(currentVolume)
                if (!isPlaying) {
                    resume()
                }
                Log.d(TAG, "重新获得音频焦点")
            }
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        stop()
        Log.d(TAG, "释放音频播放器资源")
    }
}