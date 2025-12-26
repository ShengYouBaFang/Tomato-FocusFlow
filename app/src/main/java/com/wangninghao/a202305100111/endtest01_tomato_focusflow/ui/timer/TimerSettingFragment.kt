package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentTimerSettingBinding

/**
 * 倒计时设置Fragment
 * 设置倒计时时长
 */
class TimerSettingFragment : Fragment() {

    private var _binding: FragmentTimerSettingBinding? = null
    private val binding get() = _binding!!

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
        // TODO: 后续实现倒计时设置功能
        binding.btnConfirm.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
