package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHistoryBinding

/**
 * 历史记录Fragment
 * 展示专注历史记录列表
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { session ->
            // 点击历史记录项（可扩展功能，如显示详情）
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun setupButtons() {
        // 导航到统计页面
        binding.btnStatistics.setOnClickListener {
            findNavController().navigate(R.id.action_history_to_statistics)
        }

        // 清空所有记录（可选功能）
        binding.btnClearAll.setOnClickListener {
            // TODO: 显示确认对话框后清空
            // viewModel.clearAllSessions()
        }
    }

    private fun observeViewModel() {
        // 观察历史记录列表
        viewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            adapter.submitList(sessions)

            // 显示/隐藏空状态
            if (sessions.isEmpty()) {
                binding.tvEmptyHint.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvEmptyHint.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
