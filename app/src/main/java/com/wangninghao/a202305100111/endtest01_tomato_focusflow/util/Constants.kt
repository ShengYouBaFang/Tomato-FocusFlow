package com.wangninghao.a202305100111.endtest01_tomato_focusflow.util

/**
 * 常量定义
 */
object Constants {

    // 倒计时相关
    const val DEFAULT_TIMER_MINUTES = 25
    const val QUICK_TIMER_10_MIN = 10 * 60 * 1000L
    const val QUICK_TIMER_15_MIN = 15 * 60 * 1000L
    const val QUICK_TIMER_25_MIN = 25 * 60 * 1000L
    const val QUICK_TIMER_30_MIN = 30 * 60 * 1000L

    // Service相关
    const val ACTION_START_TIMER = "ACTION_START_TIMER"
    const val ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER"
    const val ACTION_RESUME_TIMER = "ACTION_RESUME_TIMER"
    const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"

    const val EXTRA_TIMER_DURATION = "EXTRA_TIMER_DURATION"
    const val EXTRA_WHITE_NOISE_ID = "EXTRA_WHITE_NOISE_ID"

    // Notification相关
    const val NOTIFICATION_CHANNEL_ID = "focus_timer_channel"
    const val NOTIFICATION_CHANNEL_NAME = "专注倒计时"
    const val NOTIFICATION_ID = 1001

    // 时间格式
    const val TIME_FORMAT_MM_SS = "mm:ss"
    const val TIME_FORMAT_HH_MM_SS = "HH:mm:ss"
}
