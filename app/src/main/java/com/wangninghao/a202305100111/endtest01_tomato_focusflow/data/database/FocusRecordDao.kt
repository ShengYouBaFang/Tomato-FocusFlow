package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 专注记录 DAO
 */
@Dao
interface FocusRecordDao {
    
    /**
     * 插入一条专注记录
     */
    @Insert
    suspend fun insert(record: FocusRecord): Long
    
    /**
     * 删除一条记录
     */
    @Delete
    suspend fun delete(record: FocusRecord)
    
    /**
     * 获取所有记录（按开始时间倒序）
     */
    @Query("SELECT * FROM focus_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<FocusRecord>>
    
    /**
     * 获取指定日期的记录
     */
    @Query("SELECT * FROM focus_records WHERE startTime >= :dayStart AND startTime < :dayEnd ORDER BY startTime DESC")
    fun getRecordsByDay(dayStart: Long, dayEnd: Long): Flow<List<FocusRecord>>
    
    /**
     * 统计总专注时长（秒）
     */
    @Query("SELECT SUM(actualDuration) FROM focus_records WHERE isCompleted = 1")
    suspend fun getTotalFocusTime(): Int?
    
    /**
     * 统计总完成次数
     */
    @Query("SELECT COUNT(*) FROM focus_records WHERE isCompleted = 1")
    suspend fun getTotalCompletedCount(): Int
    
    /**
     * 删除所有记录
     */
    @Query("DELETE FROM focus_records")
    suspend fun deleteAll()
}
