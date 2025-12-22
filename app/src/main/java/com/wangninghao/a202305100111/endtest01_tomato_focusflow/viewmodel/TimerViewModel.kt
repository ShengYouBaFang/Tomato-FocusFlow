package com.wangninghao.a202305100111.endtest01_tomato_focusflow.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.FocusRecordRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.PreferencesRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.service.ServiceLifecycleManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants
import kotlinx.coroutines.launch

/**
 * 番茄钟 ViewModel
 * 负责管理倒计时状态和与 Service 的通信
 */
class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val serviceManager = ServiceLifecycleManager(application)
    private val preferencesRepository = PreferencesRepository(application)
    private val focusRecordRepository: FocusRecordRepository
    
    // 倒计时状态
    private val _remainingTime = MutableLiveData<Long>()
    val remainingTime: LiveData<Long> = _remainingTime
    
    private val _totalTime = MutableLiveData<Long>()
    val totalTime: LiveData<Long> = _totalTime
    
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning
    
    // 默认时长
    val defaultDuration = preferencesRepository.defaultDuration
    
    init {
        // 初始化数据库
        val database = com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.AppDatabase.getInstance(application)
        focusRecordRepository = FocusRecordRepository(database.focusRecordDao())
        
        // 设置初始值
        _totalTime.value = Constants.TIMER_25_MIN
        _remainingTime.value = Constants.TIMER_25_MIN
        _isRunning.value = false
        
        // 注册广播接收器
        registerBroadcastReceiver()
    }
    
    /**
     * 注册广播接收器，接收来自 Service 的状态更新
     */
    private fun registerBroadcastReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_TIMER_UPDATE -> {
                        _remainingTime.value = intent.getLongExtra(Constants.EXTRA_REMAINING_TIME, _totalTime.value ?: Constants.TIMER_25_MIN)
                        _totalTime.value = intent.getLongExtra(Constants.EXTRA_DURATION, _totalTime.value ?: Constants.TIMER_25_MIN)
                        _isRunning.value = intent.getBooleanExtra("isRunning", false)
                    }
                    Constants.BROADCAST_TIMER_FINISH -> {
                        _isRunning.value = false
                        _remainingTime.value = 0L
                        // 保存记录到数据库
                        saveFocusRecord()
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Constants.BROADCAST_TIMER_UPDATE)
            addAction(Constants.BROADCAST_TIMER_FINISH)
        }
        
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(receiver, filter)
    }
    
    /**
     * 开始倒计时
     */
    fun startTimer(duration: Long) {
        serviceManager.startTimerService(duration)
        _totalTime.value = duration
        _remainingTime.value = duration
        _isRunning.value = true
    }
    
    /**
     * 暂停倒计时
     */
    fun pauseTimer() {
        serviceManager.pauseTimerService()
    }
    
    /**
     * 继续倒计时
     */
    fun resumeTimer() {
        serviceManager.resumeTimerService()
    }
    
    /**
     * 停止倒计时
     */
    fun stopTimer() {
        serviceManager.stopTimerService()
        _isRunning.value = false
        _remainingTime.value = _totalTime.value ?: Constants.TIMER_25_MIN
    }
    
    /**
     * 保存专注记录到数据库
     */
    private fun saveFocusRecord() {
        viewModelScope.launch {
            val record = FocusRecord(
                duration = ((_totalTime.value ?: 0L) / 60 / 1000).toInt(),
                actualDuration = (((_totalTime.value ?: 0L) - (_remainingTime.value ?: 0L)) / 1000).toInt(),
                startTime = System.currentTimeMillis() - (_totalTime.value ?: 0L),
                endTime = System.currentTimeMillis(),
                isCompleted = _remainingTime.value == 0L,
                whiteNoise = null // 这里可以根据实际情况设置使用的白噪音类型
            )
            
            focusRecordRepository.insertRecord(record)
        }
    }
    
    /**
     * 获取默认时长
     */
    fun getDefaultDuration() {
        // 不需要实现，因为 defaultDuration 是 Flow 类型
    }
    
    /**
     * 设置默认时长
     */
    fun setDefaultDuration(duration: Int) {
        viewModelScope.launch {
            preferencesRepository.setDefaultDuration(duration)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        serviceManager.destroy()
    }
}