package com.wangninghao.a202305100111.endtest01_tomato_focusflow.util

import java.util.concurrent.TimeUnit

/**
 * 时间格式化工具类
 */
object TimeFormatter {

    /**
     * 格式化毫秒为 MM:SS 格式
     * @param millis 毫秒数
     * @return 格式化后的字符串，例如 "25:00"
     */
    fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * 格式化毫秒为 HH:MM:SS 格式
     * @param millis 毫秒数
     * @return 格式化后的字符串，例如 "01:25:30"
     */
    fun formatTimeWithHours(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * 格式化毫秒为友好的时长描述
     * @param millis 毫秒数
     * @return 格式化后的字符串，例如 "25分钟" 或 "1小时30分钟"
     */
    fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟"
            hours > 0 -> "${hours}小时"
            minutes > 0 -> "${minutes}分钟"
            else -> "小于1分钟"
        }
    }

    /**
     * 将分钟转换为毫秒
     */
    fun minutesToMillis(minutes: Int): Long {
        return TimeUnit.MINUTES.toMillis(minutes.toLong())
    }

    /**
     * 将小时和分钟转换为毫秒
     */
    fun hoursAndMinutesToMillis(hours: Int, minutes: Int): Long {
        return TimeUnit.HOURS.toMillis(hours.toLong()) + TimeUnit.MINUTES.toMillis(minutes.toLong())
    }
}
