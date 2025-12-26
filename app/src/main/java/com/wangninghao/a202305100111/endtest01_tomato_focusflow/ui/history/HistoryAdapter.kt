package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository.WhiteNoiseRepository
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.ItemHistoryBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 历史记录列表适配器
 */
class HistoryAdapter(
    private val onItemClick: (FocusSessionEntity) -> Unit = {}
) : ListAdapter<FocusSessionEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val whiteNoiseRepository = WhiteNoiseRepository()
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        fun bind(session: FocusSessionEntity) {
            try {
                binding.apply {
                    // 显示时长 - 使用formatDuration
                    tvDuration.text = TimeFormatter.formatDuration(session.duration)

                    // 显示白噪音信息
                    try {
                        val whiteNoise = whiteNoiseRepository.getWhiteNoiseById(session.whiteNoiseIcon)
                        if (whiteNoise != null) {
                            tvWhiteNoiseName.text = whiteNoise.name
                            ivWhiteNoiseIcon.setImageResource(whiteNoise.iconRes)
                        } else {
                            // 后备方案：使用数据库存储的名称
                            tvWhiteNoiseName.text = session.whiteNoiseName
                            ivWhiteNoiseIcon.setImageResource(R.drawable.rain)
                        }
                    } catch (e: Exception) {
                        // 异常处理：使用默认值
                        tvWhiteNoiseName.text = session.whiteNoiseName
                        ivWhiteNoiseIcon.setImageResource(R.drawable.rain)
                    }

                    // 显示时间信息
                    try {
                        val startTime = dateFormat.format(Date(session.startTime))
                        val endTime = dateFormat.format(Date(session.endTime))
                        tvTimeInfo.text = "$startTime - ${endTime.substring(11)}"
                    } catch (e: Exception) {
                        tvTimeInfo.text = "时间信息不可用"
                    }

                    // 显示完成标识
                    ivCompleted.visibility = if (session.isCompleted) View.VISIBLE else View.GONE

                    // 点击事件
                    root.setOnClickListener {
                        onItemClick(session)
                    }
                }
            } catch (e: Exception) {
                // 整体异常处理，避免崩溃
                e.printStackTrace()
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<FocusSessionEntity>() {
        override fun areItemsTheSame(
            oldItem: FocusSessionEntity,
            newItem: FocusSessionEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FocusSessionEntity,
            newItem: FocusSessionEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
