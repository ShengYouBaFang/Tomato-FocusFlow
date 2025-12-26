package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository

import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.dao.FocusSessionDao
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 专注记录仓库
 * 管理专注记录的数据访问，提供统一的数据接口
 */
class FocusRepository(private val focusSessionDao: FocusSessionDao) {

    /**
     * 获取所有已完成的专注记录
     */
    fun getAllCompletedSessions(): Flow<List<FocusSessionEntity>> {
        return focusSessionDao.getAllCompletedSessions()
    }

    /**
     * 插入新的专注记录
     */
    suspend fun insertSession(session: FocusSessionEntity): Long {
        return focusSessionDao.insert(session)
    }

    /**
     * 获取最长的专注记录
     */
    suspend fun getLongestSession(): FocusSessionEntity? {
        return focusSessionDao.getLongestSession()
    }

    /**
     * 获取总专注时长（毫秒）
     */
    suspend fun getTotalDuration(): Long {
        return focusSessionDao.getTotalDuration() ?: 0L
    }

    /**
     * 获取总完成次数
     */
    suspend fun getTotalCount(): Int {
        return focusSessionDao.getTotalCount()
    }

    /**
     * 删除所有记录
     */
    suspend fun deleteAll() {
        focusSessionDao.deleteAll()
    }
}
