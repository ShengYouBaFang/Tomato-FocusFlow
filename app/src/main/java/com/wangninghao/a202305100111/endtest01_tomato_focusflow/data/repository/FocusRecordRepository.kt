package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository

import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecordDao
import kotlinx.coroutines.flow.Flow

/**
 * 专注记录数据仓库
 */
class FocusRecordRepository(private val focusRecordDao: FocusRecordDao) {
    
    /**
     * 获取所有记录
     */
    fun getAllRecords(): Flow<List<FocusRecord>> {
        return focusRecordDao.getAllRecords()
    }
    
    /**
     * 获取指定日期的记录
     */
    fun getRecordsByDay(dayStart: Long, dayEnd: Long): Flow<List<FocusRecord>> {
        return focusRecordDao.getRecordsByDay(dayStart, dayEnd)
    }
    
    /**
     * 插入一条记录
     */
    suspend fun insertRecord(record: FocusRecord): Long {
        return focusRecordDao.insert(record)
    }
    
    /**
     * 删除一条记录
     */
    suspend fun deleteRecord(record: FocusRecord) {
        focusRecordDao.delete(record)
    }
    
    /**
     * 获取总专注时长（秒）
     */
    suspend fun getTotalFocusTime(): Int {
        return focusRecordDao.getTotalFocusTime() ?: 0
    }
    
    /**
     * 获取总完成次数
     */
    suspend fun getTotalCompletedCount(): Int {
        return focusRecordDao.getTotalCompletedCount()
    }
    
    /**
     * 删除所有记录
     */
    suspend fun deleteAllRecords() {
        focusRecordDao.deleteAll()
    }
}
