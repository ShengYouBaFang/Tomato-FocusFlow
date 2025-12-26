package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.main.MainActivity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.Constants
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter

/**
 * 通知辅助类
 * 创建和管理前台服务通知
 */
object NotificationHelper {

    /**
     * 创建通知渠道
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "专注倒计时通知"
                setShowBadge(false)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建前台服务通知
     */
    fun createNotification(
        context: Context,
        remainingTime: Long,
        isRunning: Boolean
    ): android.app.Notification {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val timeText = TimeFormatter.formatTime(remainingTime)
        val statusText = if (isRunning) "进行中" else "已暂停"

        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("专注倒计时 - $statusText")
            .setContentText("剩余时间：$timeText")
            .setSmallIcon(R.drawable.ic_home)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
