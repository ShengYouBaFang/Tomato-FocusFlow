package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.FocusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 历史记录ViewModel
 * 查询和管理专注历史记录
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FocusRepository(application.applicationContext)

    private val _allSessions = MutableStateFlow<List<FocusSessionEntity>>(emptyList())
    val allSessions: StateFlow<List<FocusSessionEntity>> = _allSessions.asStateFlow()

    init {
        loadSessions()
    }

    /**
     * 加载所有历史记录
     */
    private fun loadSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllSessions().collect { sessions ->
                _allSessions.value = sessions
            }
        }
    }

    /**
     * 删除指定记录
     */
    fun deleteSession(session: FocusSessionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSession(session)
        }
    }

    /**
     * 清空所有记录
     */
    fun clearAllSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllSessions()
        }
    }
}
