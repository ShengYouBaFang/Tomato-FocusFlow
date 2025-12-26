package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.PreferenceHelper
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter

/**
 * 倒计时设置ViewModel
 */
class TimerSettingViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelper(application)

    /**
     * 保存倒计时设置
     */
    fun saveTimerDuration(hours: Int, minutes: Int) {
        val duration = TimeFormatter.hoursAndMinutesToMillis(hours, minutes)
        preferenceHelper.setDefaultTimerDuration(duration)
    }

    /**
     * 保存快捷倒计时
     */
    fun saveQuickTimer(minutes: Int) {
        val duration = TimeFormatter.minutesToMillis(minutes)
        preferenceHelper.setDefaultTimerDuration(duration)
    }
}
