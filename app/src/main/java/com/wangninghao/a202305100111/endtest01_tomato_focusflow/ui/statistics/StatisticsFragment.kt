package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentStatisticsBinding

/**
 * 数据统计Fragment
 * 展示专注数据统计和图表
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // TODO: 后续实现统计图表功能
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
