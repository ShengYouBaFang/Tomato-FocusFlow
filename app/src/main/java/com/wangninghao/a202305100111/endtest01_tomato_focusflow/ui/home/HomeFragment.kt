package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHomeBinding

/**
 * 主界面Fragment
 * 包含倒计时器和白噪音播放控制
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // TODO: 后续实现倒计时和白噪音播放功能
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
