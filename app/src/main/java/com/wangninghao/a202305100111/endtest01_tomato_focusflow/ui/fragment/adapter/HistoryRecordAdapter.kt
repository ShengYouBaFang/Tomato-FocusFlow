package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.database.FocusRecord
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 历史记录项适配器
 */
class HistoryRecordAdapter(
    private val recordList: List<FocusRecord>,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<HistoryRecordAdapter.ViewHolder>() {
    
    interface OnDeleteClickListener {
        fun onDeleteClick(record: FocusRecord)
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val durationText: TextView = itemView.findViewById(R.id.durationText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_record, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recordList[position]
        
        // 设置时长
        val durationText = "${record.duration}分钟"
        holder.durationText.text = durationText
        
        // 设置时间
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeText = timeFormat.format(Date(record.startTime))
        holder.timeText.text = timeText
        
        // 设置删除按钮点击事件
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener.onDeleteClick(record)
        }
    }
    
    override fun getItemCount(): Int = recordList.size
}