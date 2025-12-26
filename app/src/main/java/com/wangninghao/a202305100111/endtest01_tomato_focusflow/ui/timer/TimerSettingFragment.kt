package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentTimerSettingBinding

/**
 * 倒计时设置Fragment
 * 设置倒计时时长
 */
class TimerSettingFragment : Fragment() {

    private var _binding: FragmentTimerSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerSettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // 配置NumberPicker
        binding.npHours.apply {
            minValue = 0
            maxValue = 2
            value = 0
        }

        binding.npMinutes.apply {
            minValue = 0
            maxValue = 59
            value = 25
        }

        // 快捷按钮
        binding.btn10min.setOnClickListener {
            viewModel.saveQuickTimer(10)
            findNavController().navigateUp()
        }

        binding.btn15min.setOnClickListener {
            viewModel.saveQuickTimer(15)
            findNavController().navigateUp()
        }

        binding.btn30min.setOnClickListener {
            viewModel.saveQuickTimer(30)
            findNavController().navigateUp()
        }

        // 确认按钮
        binding.btnConfirm.setOnClickListener {
            val hours = binding.npHours.value
            val minutes = binding.npMinutes.value
            viewModel.saveTimerDuration(hours, minutes)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
