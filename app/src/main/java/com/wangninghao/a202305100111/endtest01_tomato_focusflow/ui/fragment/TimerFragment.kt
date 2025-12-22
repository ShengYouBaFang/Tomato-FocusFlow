package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentTimerBinding

/**
 * 番茄钟Fragment
 * 主要功能：倒计时、自定义View、开始/暂停/停止控制
 */
class TimerFragment : Fragment() {
    
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: 后续实现倒计时逻辑
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
