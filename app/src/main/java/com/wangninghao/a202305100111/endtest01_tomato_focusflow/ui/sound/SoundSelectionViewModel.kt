package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.sound

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.WhiteNoiseRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.PreferenceHelper

/**
 * 白噪音选择ViewModel
 */
class SoundSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceHelper = PreferenceHelper(application)
    private val whiteNoiseRepository = WhiteNoiseRepository()

    // 所有白噪音列表
    private val _whiteNoiseList = MutableLiveData<List<WhiteNoise>>()
    val whiteNoiseList: LiveData<List<WhiteNoise>> = _whiteNoiseList

    // 当前选中的白噪音
    private val _selectedWhiteNoise = MutableLiveData<WhiteNoise>()
    val selectedWhiteNoise: LiveData<WhiteNoise> = _selectedWhiteNoise

    init {
        loadWhiteNoiseList()
        loadSelectedWhiteNoise()
    }

    /**
     * 加载白噪音列表
     */
    private fun loadWhiteNoiseList() {
        _whiteNoiseList.value = whiteNoiseRepository.getAllWhiteNoises()
    }

    /**
     * 加载当前选中的白噪音
     */
    private fun loadSelectedWhiteNoise() {
        val selectedId = preferenceHelper.getSelectedWhiteNoiseId()
        val noise = whiteNoiseRepository.getWhiteNoiseById(selectedId)
            ?: whiteNoiseRepository.getDefaultWhiteNoise()
        _selectedWhiteNoise.value = noise
    }

    /**
     * 选择白噪音
     */
    fun selectWhiteNoise(whiteNoise: WhiteNoise) {
        _selectedWhiteNoise.value = whiteNoise
        preferenceHelper.setSelectedWhiteNoiseId(whiteNoise.id)
    }
}
