package com.wangninghao.a202305100111.endtest01_tomato_focusflow.util

import android.util.Log

/**
 * 全局错误处理器
 * 统一处理应用中的各类异常
 */
object ErrorHandler {

    private const val TAG = "ErrorHandler"

    /**
     * 错误类型
     */
    sealed class ErrorType {
        data class TimerError(val message: String, val cause: Throwable? = null) : ErrorType()
        data class AudioError(val message: String, val cause: Throwable? = null) : ErrorType()
        data class DatabaseError(val message: String, val cause: Throwable? = null) : ErrorType()
        data class ServiceError(val message: String, val cause: Throwable? = null) : ErrorType()
        data class UnknownError(val message: String, val cause: Throwable? = null) : ErrorType()
    }

    /**
     * 错误监听器
     */
    interface ErrorListener {
        fun onError(errorType: ErrorType)
    }

    private val errorListeners = mutableListOf<ErrorListener>()

    /**
     * 注册错误监听器
     */
    fun addErrorListener(listener: ErrorListener) {
        errorListeners.add(listener)
    }

    /**
     * 移除错误监听器
     */
    fun removeErrorListener(listener: ErrorListener) {
        errorListeners.remove(listener)
    }

    /**
     * 处理计时器错误
     */
    fun handleTimerError(message: String, throwable: Throwable? = null) {
        val error = ErrorType.TimerError(message, throwable)
        logError(error)
        notifyListeners(error)
    }

    /**
     * 处理音频播放错误
     */
    fun handleAudioError(message: String, throwable: Throwable? = null) {
        val error = ErrorType.AudioError(message, throwable)
        logError(error)
        notifyListeners(error)
    }

    /**
     * 处理数据库错误
     */
    fun handleDatabaseError(message: String, throwable: Throwable? = null) {
        val error = ErrorType.DatabaseError(message, throwable)
        logError(error)
        notifyListeners(error)
    }

    /**
     * 处理服务错误
     */
    fun handleServiceError(message: String, throwable: Throwable? = null) {
        val error = ErrorType.ServiceError(message, throwable)
        logError(error)
        notifyListeners(error)
    }

    /**
     * 处理未知错误
     */
    fun handleUnknownError(message: String, throwable: Throwable? = null) {
        val error = ErrorType.UnknownError(message, throwable)
        logError(error)
        notifyListeners(error)
    }

    /**
     * 记录错误日志
     */
    private fun logError(errorType: ErrorType) {
        when (errorType) {
            is ErrorType.TimerError -> {
                Log.e(TAG, "Timer Error: ${errorType.message}", errorType.cause)
            }
            is ErrorType.AudioError -> {
                Log.e(TAG, "Audio Error: ${errorType.message}", errorType.cause)
            }
            is ErrorType.DatabaseError -> {
                Log.e(TAG, "Database Error: ${errorType.message}", errorType.cause)
            }
            is ErrorType.ServiceError -> {
                Log.e(TAG, "Service Error: ${errorType.message}", errorType.cause)
            }
            is ErrorType.UnknownError -> {
                Log.e(TAG, "Unknown Error: ${errorType.message}", errorType.cause)
            }
        }
    }

    /**
     * 通知所有监听器
     */
    private fun notifyListeners(errorType: ErrorType) {
        errorListeners.forEach { listener ->
            try {
                listener.onError(errorType)
            } catch (e: Exception) {
                Log.e(TAG, "Error notifying listener", e)
            }
        }
    }

    /**
     * 获取用户友好的错误消息
     */
    fun getUserFriendlyMessage(errorType: ErrorType): String {
        return when (errorType) {
            is ErrorType.TimerError -> "计时器出现问题，请重试"
            is ErrorType.AudioError -> "白噪音播放失败，已静音继续"
            is ErrorType.DatabaseError -> "数据保存失败，但计时器仍在运行"
            is ErrorType.ServiceError -> "后台服务连接失败，请重启应用"
            is ErrorType.UnknownError -> "发生未知错误，请重试"
        }
    }
}
