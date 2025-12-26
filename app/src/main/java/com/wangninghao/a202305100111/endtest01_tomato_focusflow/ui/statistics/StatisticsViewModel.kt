package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.FocusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 统计数据ViewModel
 * 查询和计算专注统计数据
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FocusRepository(application.applicationContext)

    private val _completedSessions = MutableStateFlow<List<FocusSessionEntity>>(emptyList())
    val completedSessions: StateFlow<List<FocusSessionEntity>> = _completedSessions.asStateFlow()

    private val _longestSession = MutableStateFlow<FocusSessionEntity?>(null)
    val longestSession: StateFlow<FocusSessionEntity?> = _longestSession.asStateFlow()

    private val _totalDuration = MutableStateFlow<Long>(0L)
    val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    private val _totalCount = MutableStateFlow<Int>(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * 加载所有统计数据
     */
    private fun loadStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            // 加载已完成记录
            launch {
                repository.getCompletedSessions().collect { sessions ->
                    _completedSessions.value = sessions
                }
            }

            // 加载最长记录
            launch {
                repository.getLongestSession().collect { session ->
                    _longestSession.value = session
                }
            }

            // 加载总时长
            launch {
                repository.getTotalDuration().collect { duration ->
                    _totalDuration.value = duration ?: 0L
                }
            }

            // 加载总次数
            launch {
                repository.getTotalCount().collect { count ->
                    _totalCount.value = count ?: 0
                }
            }
        }
    }
}
