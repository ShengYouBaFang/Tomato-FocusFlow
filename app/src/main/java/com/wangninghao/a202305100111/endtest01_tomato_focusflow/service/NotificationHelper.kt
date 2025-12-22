package com.wangninghao.a202305100111.endtest01_tomato_focusflow.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.activity.MainActivity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter

/**
 * 通知帮助类
 * 负责创建和管理倒计时通知
 */
object NotificationHelper {
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_name)
                setShowBadge(false)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 创建倒计时通知
     */
    fun createTimerNotification(
        context: Context,
        remainingTime: Long,
        totalTime: Long,
        isRunning: Boolean
    ): Notification {
        // 创建点击通知后打开的意图
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 创建操作按钮意图
        val pauseIntent = createActionIntent(context, Constants.ACTION_PAUSE)
        val stopIntent = createActionIntent(context, Constants.ACTION_STOP)
        
        // 格式化时间显示
        val timeText = TimeFormatter.formatTime(remainingTime)
        val progress = if (totalTime > 0) {
            ((totalTime - remainingTime).toFloat() / totalTime * 100).toInt()
        } else 0
        
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer) // 需要添加图标资源
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(timeText)
            .setProgress(100, progress, false)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 使通知不可滑动删除
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
        
        // 添加操作按钮
        if (isRunning) {
            builder.addAction(
                R.drawable.ic_pause, // 需要添加图标资源
                context.getString(R.string.timer_pause),
                pauseIntent
            )
        } else {
            val resumeIntent = createActionIntent(context, Constants.ACTION_RESUME)
            builder.addAction(
                R.drawable.ic_play, // 需要添加图标资源
                context.getString(R.string.timer_resume),
                resumeIntent
            )
        }
        
        builder.addAction(
            R.drawable.ic_stop, // 需要添加图标资源
            context.getString(R.string.timer_stop),
            stopIntent
        )
        
        return builder.build()
    }
    
    /**
     * 创建完成通知
     */
    fun createFinishNotification(context: Context): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check) // 需要添加图标资源
            .setContentTitle(context.getString(R.string.notification_finish_title))
            .setContentText(context.getString(R.string.notification_finish_desc))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }
    
    /**
     * 创建操作意图
     */
    private fun createActionIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, TimerService::class.java).apply {
            this.action = action
        }
        
        // 使用不同的 requestCode 区分不同的操作
        val requestCode = when (action) {
            Constants.ACTION_PAUSE -> 1
            Constants.ACTION_RESUME -> 2
            Constants.ACTION_STOP -> 3
            else -> 0
        }
        
        return PendingIntent.getService(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}