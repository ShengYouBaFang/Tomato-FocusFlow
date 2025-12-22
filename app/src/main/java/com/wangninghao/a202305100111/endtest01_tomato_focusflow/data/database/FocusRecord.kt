package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 专注记录实体类
 */
@Entity(tableName = "focus_records")
data class FocusRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** 设定时长（分钟） */
    val duration: Int,
    
    /** 实际完成时长（秒） */
    val actualDuration: Int,
    
    /** 开始时间戳 */
    val startTime: Long,
    
    /** 结束时间戳 */
    val endTime: Long,
    
    /** 是否完整完成 */
    val isCompleted: Boolean,
    
    /** 使用的白噪音类型（可选） */
    val whiteNoise: String? = null
)
