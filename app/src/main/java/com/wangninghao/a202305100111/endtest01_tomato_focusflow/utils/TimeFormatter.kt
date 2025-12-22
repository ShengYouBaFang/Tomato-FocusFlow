package com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils

/**
 * 时间格式化工具类
 */
object TimeFormatter {
    
    /**
     * 将毫秒转换为 "MM:SS" 格式
     * @param milliseconds 毫秒数
     * @return 格式化后的时间字符串，例如 "25:00"
     */
    fun formatTime(milliseconds: Long): String {
        val totalSeconds = (milliseconds / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * 将秒数转换为 "HH小时MM分钟" 格式
     * @param totalSeconds 总秒数
     * @return 格式化后的时间字符串
     */
    fun formatDuration(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟"
            hours > 0 -> "${hours}小时"
            minutes > 0 -> "${minutes}分钟"
            else -> "0分钟"
        }
    }
    
    /**
     * 将分钟转换为毫秒
     */
    fun minutesToMillis(minutes: Int): Long {
        return minutes * 60 * 1000L
    }
}
