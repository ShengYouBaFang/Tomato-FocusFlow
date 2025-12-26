package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.FocusRepository

/**
 * 统计数据ViewModel
 * 查询和计算专注统计数据
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FocusRepository(application)

    /**
     * 所有已完成的记录（用于图表）
     */
    val completedSessions: LiveData<List<FocusSessionEntity>> =
        repository.getCompletedSessions().asLiveData()

    /**
     * 最长专注记录
     */
    val longestSession: LiveData<FocusSessionEntity?> =
        repository.getLongestSession().asLiveData()

    /**
     * 总专注时长（毫秒）
     */
    val totalDuration: LiveData<Long?> =
        repository.getTotalDuration().asLiveData()

    /**
     * 总专注次数
     */
    val totalCount: LiveData<Int?> =
        repository.getTotalCount().asLiveData()
}
