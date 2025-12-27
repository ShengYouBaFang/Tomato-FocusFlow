package com.wangninghao.a202305100111.endtest01_tomato_focusflow.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences工具类
 * 管理应用的配置和偏好设置
 */
class PreferenceHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "focus_flow_prefs"

        // Keys
        private const val KEY_SELECTED_WHITE_NOISE_ID = "selected_white_noise_id"
        private const val KEY_DEFAULT_TIMER_DURATION = "default_timer_duration"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_WHITE_NOISE_ENABLED = "white_noise_enabled"

        // 默认值
        private const val DEFAULT_TIMER_DURATION = 25 * 60 * 1000L // 25分钟（毫秒）
        private const val DEFAULT_WHITE_NOISE_ID = "rain" // 默认雨声
    }

    /**
     * 获取选中的白噪音ID
     */
    fun getSelectedWhiteNoiseId(): String {
        return prefs.getString(KEY_SELECTED_WHITE_NOISE_ID, DEFAULT_WHITE_NOISE_ID)
            ?: DEFAULT_WHITE_NOISE_ID
    }

    /**
     * 设置选中的白噪音ID
     */
    fun setSelectedWhiteNoiseId(id: String) {
        prefs.edit().putString(KEY_SELECTED_WHITE_NOISE_ID, id).apply()
    }

    /**
     * 获取默认倒计时时长（毫秒）
     */
    fun getDefaultTimerDuration(): Long {
        return prefs.getLong(KEY_DEFAULT_TIMER_DURATION, DEFAULT_TIMER_DURATION)
    }

    /**
     * 设置默认倒计时时长（毫秒）
     */
    fun setDefaultTimerDuration(duration: Long) {
        prefs.edit().putLong(KEY_DEFAULT_TIMER_DURATION, duration).apply()
    }

    /**
     * 是否是第一次启动
     */
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    /**
     * 设置已经不是第一次启动
     */
    fun setNotFirstLaunch() {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }

    /**
     * 获取白噪音是否开启
     */
    fun isWhiteNoiseEnabled(): Boolean {
        return prefs.getBoolean(KEY_WHITE_NOISE_ENABLED, true)
    }

    /**
     * 设置白噪音是否开启
     */
    fun setWhiteNoiseEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WHITE_NOISE_ENABLED, enabled).apply()
    }

    /**
     * 清除所有设置
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
