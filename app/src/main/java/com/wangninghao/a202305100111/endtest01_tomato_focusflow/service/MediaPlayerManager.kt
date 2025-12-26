package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.ErrorHandler

/**
 * 媒体播放管理器
 * 管理白噪音音频的播放、暂停、切换等操作
 */
class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResourceId: Int = 0
    private var isPlaying: Boolean = false
    private var retryCount = 0
    private val maxRetries = 3

    companion object {
        private const val TAG = "MediaPlayerManager"
    }

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
            mediaPlayer = MediaPlayer.create(context, resourceId)

            if (mediaPlayer == null) {
                // MediaPlayer.create() 返回 null 表示资源加载失败
                handlePlaybackError("无法创建 MediaPlayer，音频资源可能不存在", null)
                return
            }

            mediaPlayer?.apply {
                isLooping = true // 循环播放

                setOnErrorListener { mp, what, extra ->
                    val errorMsg = "MediaPlayer 错误: what=$what, extra=$extra"
                    Log.e(TAG, errorMsg)
                    handlePlaybackError(errorMsg, null)

                    // 尝试自动恢复
                    if (retryCount < maxRetries) {
                        retryCount++
                        Log.d(TAG, "尝试重新播放 (${retryCount}/$maxRetries)")
                        tryRecover(resourceId)
                    }

                    true // 返回 true 表示错误已处理
                }

                setOnCompletionListener {
                    // 正常情况下不会触发（因为是循环播放）
                    // 但如果触发了，说明循环播放可能失败
                    Log.w(TAG, "MediaPlayer 完成回调被触发（异常情况）")
                    if (isLooping) {
                        tryRecover(resourceId)
                    }
                }
            }

            currentResourceId = resourceId
            mediaPlayer?.start()
            isPlaying = true
            retryCount = 0 // 成功播放后重置重试计数

            Log.d(TAG, "音频播放成功: resourceId=$resourceId")

        } catch (e: IllegalStateException) {
            handlePlaybackError("MediaPlayer 状态异常", e)
        } catch (e: IllegalArgumentException) {
            handlePlaybackError("无效的音频资源", e)
        } catch (e: SecurityException) {
            handlePlaybackError("没有访问音频资源的权限", e)
        } catch (e: Exception) {
            handlePlaybackError("播放音频时发生未知错误", e)
        }
    }

    /**
     * 尝试恢复播放
     */
    private fun tryRecover(resourceId: Int) {
        try {
            release()
            Thread.sleep(500) // 短暂延迟后重试
            play(resourceId)
        } catch (e: Exception) {
            Log.e(TAG, "恢复播放失败", e)
            ErrorHandler.handleAudioError("自动恢复播放失败", e)
        }
    }

    /**
     * 处理播放错误
     */
    private fun handlePlaybackError(message: String, throwable: Throwable?) {
        Log.e(TAG, message, throwable)
        ErrorHandler.handleAudioError(message, throwable)
        isPlaying = false
    }

    /**
     * 暂停播放
     */
    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    isPlaying = false
                    Log.d(TAG, "音频已暂停")
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "暂停播放时状态异常", e)
            ErrorHandler.handleAudioError("暂停播放失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "暂停播放时发生错误", e)
            ErrorHandler.handleAudioError("暂停播放失败", e)
        }
    }

    /**
     * 恢复播放
     */
    fun resume() {
        try {
            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    isPlaying = true
                    Log.d(TAG, "音频已恢复")
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "恢复播放时状态异常", e)
            ErrorHandler.handleAudioError("恢复播放失败", e)
            // 尝试重新加载
            if (currentResourceId != 0) {
                play(currentResourceId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "恢复播放时发生错误", e)
            ErrorHandler.handleAudioError("恢复播放失败", e)
        }
    }

    /**
     * 停止播放
     */
    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                    Log.d(TAG, "音频已停止")
                }
                isPlaying = false
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "停止播放时状态异常", e)
            // 停止失败时直接释放
            safeRelease()
        } catch (e: Exception) {
            Log.e(TAG, "停止播放时发生错误", e)
            safeRelease()
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        safeRelease()
    }

    /**
     * 安全地释放资源
     */
    private fun safeRelease() {
        try {
            mediaPlayer?.let {
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                } catch (e: IllegalStateException) {
                    // 停止失败也要继续释放
                    Log.w(TAG, "停止播放失败，继续释放资源", e)
                }

                try {
                    it.release()
                    Log.d(TAG, "MediaPlayer 资源已释放")
                } catch (e: Exception) {
                    Log.e(TAG, "释放 MediaPlayer 时发生错误", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "释放资源时发生未知错误", e)
        } finally {
            mediaPlayer = null
            currentResourceId = 0
            isPlaying = false
            retryCount = 0
        }
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: IllegalStateException) {
            Log.w(TAG, "检查播放状态时发生异常", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "检查播放状态时发生未知异常", e)
            false
        }
    }

    /**
     * 设置音量
     * @param volume 0.0 - 1.0
     */
    fun setVolume(volume: Float) {
        try {
            val vol = volume.coerceIn(0f, 1f)
            mediaPlayer?.setVolume(vol, vol)
            Log.d(TAG, "音量已设置: $vol")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "设置音量时状态异常", e)
            ErrorHandler.handleAudioError("设置音量失败", e)
        } catch (e: Exception) {
            Log.e(TAG, "设置音量时发生错误", e)
            ErrorHandler.handleAudioError("设置音量失败", e)
        }
    }
}
