package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 专注记录数据库实体
 * 记录用户每次专注倒计时的详细信息
 */
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 专注时长（毫秒） */
    val duration: Long,

    /** 开始时间戳 */
    val startTime: Long,

    /** 结束时间戳 */
    val endTime: Long,

    /** 白噪音名称 */
    val whiteNoiseName: String,

    /** 白噪音图标资源名 */
    val whiteNoiseIcon: String,

    /** 是否完整完成（未中断） */
    val isCompleted: Boolean
)
