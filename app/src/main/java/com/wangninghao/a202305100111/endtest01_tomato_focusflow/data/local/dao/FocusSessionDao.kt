package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 专注记录数据访问对象（DAO）
 * 提供专注记录的数据库操作方法
 */
@Dao
interface FocusSessionDao {

    /**
     * 插入新的专注记录
     */
    @Insert
    suspend fun insert(session: FocusSessionEntity): Long

    /**
     * 获取所有专注记录（按开始时间倒序）
     */
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    /**
     * 获取所有已完成的专注记录（按结束时间倒序）
     */
    @Query("SELECT * FROM focus_sessions WHERE isCompleted = 1 ORDER BY endTime DESC")
    fun getCompletedSessions(): Flow<List<FocusSessionEntity>>

    /**
     * 获取最长的专注记录（Flow版本）
     */
    @Query("SELECT * FROM focus_sessions WHERE isCompleted = 1 ORDER BY duration DESC LIMIT 1")
    fun getLongestSession(): Flow<FocusSessionEntity?>

    /**
     * 获取总专注时长（毫秒，Flow版本）
     */
    @Query("SELECT SUM(duration) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalDuration(): Flow<Long?>

    /**
     * 获取总完成次数（Flow版本）
     */
    @Query("SELECT COUNT(*) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalCount(): Flow<Int?>

    /**
     * 删除指定记录
     */
    @Delete
    suspend fun delete(session: FocusSessionEntity)

    /**
     * 删除所有记录
     */
    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAll()
}
