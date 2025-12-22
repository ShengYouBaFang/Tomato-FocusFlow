package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHistoryBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter.HistoryGroupAdapter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter.HistoryRecordAdapter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 历史记录Fragment
 * 主要功能：显示专注历史、统计数据、删除记录
 */
class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: HistoryGroupAdapter
    private val groupedRecords = mutableMapOf<String, List<FocusRecord>>()
    private val dateHeaders = mutableListOf<String>()
    
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
        
        // 初始化RecyclerView
        setupRecyclerView()
        
        // 模拟数据用于测试
        loadMockData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = HistoryGroupAdapter(groupedRecords, dateHeaders, object : HistoryRecordAdapter.OnDeleteClickListener {
            override fun onDeleteClick(record: FocusRecord) {
                // TODO: 实现删除记录功能
            }
        })
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }
    
    /**
     * 加载模拟数据用于测试
     */
    private fun loadMockData() {
        // 创建模拟数据
        val today = System.currentTimeMillis()
        val yesterday = today - 24 * 60 * 60 * 1000
        val twoDaysAgo = today - 2 * 24 * 60 * 60 * 1000
        
        val mockRecords = listOf(
            FocusRecord(1, 25, 1500, today, today + 25 * 60 * 1000, true),
            FocusRecord(2, 45, 2700, today - 2 * 60 * 60 * 1000, today - 2 * 60 * 60 * 1000 + 45 * 60 * 1000, true),
            FocusRecord(3, 60, 3600, yesterday, yesterday + 60 * 60 * 1000, true),
            FocusRecord(4, 25, 1500, twoDaysAgo, twoDaysAgo + 25 * 60 * 1000, true)
        )
        
        // 按日期分组
        val grouped = mockRecords.groupBy { record ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(record.startTime))
        }
        
        // 设置日期标题
        val headers = grouped.keys.toList()
        
        // 更新数据
        groupedRecords.clear()
        groupedRecords.putAll(grouped)
        dateHeaders.clear()
        dateHeaders.addAll(headers)
        
        // 更新统计信息
        updateStats(mockRecords)
        
        // 通知适配器数据已更改
        adapter.notifyDataSetChanged()
        
        // 控制空状态显示
        binding.emptyStateLayout.visibility = if (mockRecords.isEmpty()) View.VISIBLE else View.GONE
    }
    
    /**
     * 更新统计信息
     */
    private fun updateStats(records: List<FocusRecord>) {
        val totalSeconds = records.sumOf { it.actualDuration }
        val totalTimeText = TimeFormatter.formatDuration(totalSeconds)
        binding.totalTimeText.text = totalTimeText
        
        val totalCount = records.size
        binding.totalCountText.text = "$totalCount" + getString(R.string.history_total_count)
    }
}
