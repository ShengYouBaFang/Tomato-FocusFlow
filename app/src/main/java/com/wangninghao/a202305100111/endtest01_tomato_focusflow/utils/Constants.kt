package com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils

/**
 * 全局常量定义
 */
object Constants {
    
    // 倒计时时长（毫秒）
    const val TIMER_25_MIN = 25 * 60 * 1000L
    const val TIMER_45_MIN = 45 * 60 * 1000L
    const val TIMER_60_MIN = 60 * 60 * 1000L
    
    // Service Action
    const val ACTION_START = "ACTION_START"
    const val ACTION_PAUSE = "ACTION_PAUSE"
    const val ACTION_RESUME = "ACTION_RESUME"
    const val ACTION_STOP = "ACTION_STOP"
    
    // Intent Extra
    const val EXTRA_DURATION = "EXTRA_DURATION"
    const val EXTRA_REMAINING_TIME = "EXTRA_REMAINING_TIME"
    
    // Broadcast Action
    const val BROADCAST_TIMER_UPDATE = "BROADCAST_TIMER_UPDATE"
    const val BROADCAST_TIMER_FINISH = "BROADCAST_TIMER_FINISH"
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "timer_channel"
    const val NOTIFICATION_CHANNEL_NAME = "倒计时通知"
    const val NOTIFICATION_ID = 1001
    
    // Database
    const val DATABASE_NAME = "focus_flow_database"
    
    // DataStore
    const val PREFERENCES_NAME = "focus_flow_preferences"
    const val KEY_DEFAULT_DURATION = "default_duration"
    const val KEY_VOLUME = "volume"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    
    // White Noise Types
    const val NOISE_RAIN = "rain"
    const val NOISE_FOREST = "forest"
    const val NOISE_OCEAN = "ocean"
    const val NOISE_CAFE = "cafe"
    const val NOISE_FIRE = "fire"
}
