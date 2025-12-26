package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch

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
        animateViews()
    }

    private fun animateViews() {
        // 标题动画
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = -50f
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .start()

        // 统计按钮动画
        binding.btnStatistics.alpha = 0f
        binding.btnStatistics.scaleX = 0.8f
        binding.btnStatistics.scaleY = 0.8f
        binding.btnStatistics.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay(100)
            .start()
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
        // 使用StateFlow观察数据
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allSessions.collect { sessions ->
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
