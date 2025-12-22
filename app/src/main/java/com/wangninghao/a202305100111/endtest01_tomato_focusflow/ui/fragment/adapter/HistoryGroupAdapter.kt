package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord

/**
 * 历史记录组适配器
 */
class HistoryGroupAdapter(
    private val groupedRecords: Map<String, List<FocusRecord>>,
    private val dateHeaders: List<String>,
    private val onDeleteClickListener: HistoryRecordAdapter.OnDeleteClickListener
) : RecyclerView.Adapter<HistoryGroupAdapter.ViewHolder>() {
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateHeader: TextView = itemView.findViewById(R.id.dateHeader)
        val recordsRecyclerView: RecyclerView = itemView.findViewById(R.id.recordsRecyclerView)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_group, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = dateHeaders[position]
        val records = groupedRecords[date] ?: emptyList()
        
        // 设置日期标题
        holder.dateHeader.text = date
        
        // 设置记录列表
        val recordAdapter = HistoryRecordAdapter(records, onDeleteClickListener)
        holder.recordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recordAdapter
        }
    }
    
    override fun getItemCount(): Int = dateHeaders.size
}