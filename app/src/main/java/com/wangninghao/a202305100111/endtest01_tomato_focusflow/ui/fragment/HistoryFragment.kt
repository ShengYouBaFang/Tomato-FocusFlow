package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHistoryBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter.HistoryGroupAdapter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter.HistoryRecordAdapter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.viewmodel.HistoryViewModel
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
    private lateinit var viewModel: HistoryViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
    }
    
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
        
        // 观察 ViewModel 数据
        observeViewModel()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        val groupedRecords = mutableMapOf<String, List<FocusRecord>>()
        val dateHeaders = mutableListOf<String>()
        
        adapter = HistoryGroupAdapter(groupedRecords, dateHeaders, object : HistoryRecordAdapter.OnDeleteClickListener {
            override fun onDeleteClick(record: FocusRecord) {
                viewModel.deleteRecord(record)
            }
        })
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }
    
    /**
     * 观察 ViewModel 数据
     */
    private fun observeViewModel() {
        // 观察所有记录
        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            // 按日期分组
            val grouped = records.groupBy { record ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(record.startTime))
            }
            
            // 设置日期标题
            val headers = grouped.keys.toList()
            
            // 更新适配器数据
            (adapter as HistoryGroupAdapter).updateData(grouped, headers)
            
            // 控制空状态显示
            binding.emptyStateLayout.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // 观察总专注时长
        viewModel.totalFocusTime.observe(viewLifecycleOwner) { totalSeconds ->
            val totalTimeText = TimeFormatter.formatDuration(totalSeconds)
            binding.totalTimeText.text = totalTimeText
        }
        
        // 观察总完成次数
        viewModel.totalCompletedCount.observe(viewLifecycleOwner) { totalCount ->
            binding.totalCountText.text = "$totalCount" + getString(R.string.history_total_count)
        }
    }
    
    /**
     * 更新统计信息
     */
    private fun updateStats(totalSeconds: Int, totalCount: Int) {
        val totalTimeText = TimeFormatter.formatDuration(totalSeconds)
        binding.totalTimeText.text = totalTimeText
        binding.totalCountText.text = "$totalCount" + getString(R.string.history_total_count)
    }
}
