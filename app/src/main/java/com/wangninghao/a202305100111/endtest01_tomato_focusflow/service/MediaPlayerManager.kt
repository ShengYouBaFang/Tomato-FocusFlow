package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

/**
 * 媒体播放管理器
 * 管理白噪音音频的播放、暂停、切换等操作
 */
class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResourceId: Int = 0
    private var isPlaying: Boolean = false

    /**
     * 播放音频
     * @param resourceId 音频资源ID
     */
    fun play(@RawRes resourceId: Int) {
        // 如果已有播放器且资源ID相同，直接恢复播放
        if (currentResourceId == resourceId && mediaPlayer != null) {
            resume()
            return
        }

        // 释放旧的播放器
        release()

        // 创建新的播放器
        try {
            mediaPlayer = MediaPlayer.create(context, resourceId).apply {
                isLooping = true // 循环播放
                setOnErrorListener { _, what, extra ->
                    // 错误处理
                    false
                }
            }
            currentResourceId = resourceId
            mediaPlayer?.start()
            isPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
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
            }
        }
    }

    /**
     * 恢复播放
     */
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
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
            isPlaying = false
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentResourceId = 0
        isPlaying = false
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    /**
     * 设置音量
     * @param volume 0.0 - 1.0
     */
    fun setVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(vol, vol)
    }
}
