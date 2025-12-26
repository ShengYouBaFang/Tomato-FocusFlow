package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository

import android.content.Context
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.AppDatabase
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 专注记录仓库
 * 管理专注记录的数据访问，提供统一的数据接口
 */
class FocusRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).focusSessionDao()

    /**
     * 获取所有专注记录（按时间倒序）
     */
    fun getAllSessions(): Flow<List<FocusSessionEntity>> {
        return dao.getAllSessions()
    }

    /**
     * 获取所有已完成的专注记录
     */
    fun getCompletedSessions(): Flow<List<FocusSessionEntity>> {
        return dao.getCompletedSessions()
    }

    /**
     * 获取最长的专注记录
     */
    fun getLongestSession(): Flow<FocusSessionEntity?> {
        return dao.getLongestSession()
    }

    /**
     * 获取总专注时长（毫秒）
     */
    fun getTotalDuration(): Flow<Long?> {
        return dao.getTotalDuration()
    }

    /**
     * 获取总完成次数
     */
    fun getTotalCount(): Flow<Int?> {
        return dao.getTotalCount()
    }

    /**
     * 插入新的专注记录
     */
    suspend fun insertSession(session: FocusSessionEntity): Long {
        return dao.insert(session)
    }

    /**
     * 删除指定记录
     */
    suspend fun deleteSession(session: FocusSessionEntity) {
        dao.delete(session)
    }

    /**
     * 删除所有记录
     */
    suspend fun deleteAllSessions() {
        dao.deleteAll()
    }
}
