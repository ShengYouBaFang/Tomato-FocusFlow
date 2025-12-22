package com.wangninghao.a202305100111.endtest01_tomato_focusflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.AppDatabase
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.FocusRecordRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 历史记录 ViewModel
 * 负责获取和管理专注历史数据
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val focusRecordRepository: FocusRecordRepository
    
    // 所有记录
    private val _allRecords = MutableLiveData<List<FocusRecord>>()
    val allRecords: LiveData<List<FocusRecord>> = _allRecords
    
    // 按日期分组的记录
    private val _groupedRecords = MutableLiveData<Map<String, List<FocusRecord>>>()
    val groupedRecords: LiveData<Map<String, List<FocusRecord>>> = _groupedRecords
    
    // 总专注时长
    private val _totalFocusTime = MutableLiveData<Int>()
    val totalFocusTime: LiveData<Int> = _totalFocusTime
    
    // 总完成次数
    private val _totalCompletedCount = MutableLiveData<Int>()
    val totalCompletedCount: LiveData<Int> = _totalCompletedCount
    
    init {
        // 初始化数据库和仓库
        val database = AppDatabase.getInstance(application)
        focusRecordRepository = FocusRecordRepository(database.focusRecordDao())
        
        // 加载数据
        loadData()
    }
    
    /**
     * 加载数据
     */
    private fun loadData() {
        // 获取所有记录
        viewModelScope.launch {
            focusRecordRepository.getAllRecords().collect { records ->
                _allRecords.value = records
                
                // 按日期分组
                val grouped = records.groupBy { record ->
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(record.startTime))
                }
                _groupedRecords.value = grouped
            }
        }
        
        // 获取总专注时长
        viewModelScope.launch {
            val total = focusRecordRepository.getTotalFocusTime()
            _totalFocusTime.value = total
        }
        
        // 获取总完成次数
        viewModelScope.launch {
            val count = focusRecordRepository.getTotalCompletedCount()
            _totalCompletedCount.value = count
        }
    }
    
    /**
     * 删除一条记录
     */
    fun deleteRecord(record: FocusRecord) {
        viewModelScope.launch {
            focusRecordRepository.deleteRecord(record)
            // 重新加载数据
            loadData()
        }
    }
    
    /**
     * 删除所有记录
     */
    fun deleteAllRecords() {
        viewModelScope.launch {
            focusRecordRepository.deleteAllRecords()
            // 重新加载数据
            loadData()
        }
    }
}