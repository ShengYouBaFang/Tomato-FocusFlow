package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.sound

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.ItemWhiteNoiseBinding

/**
 * 白噪音列表适配器
 */
class WhiteNoiseAdapter(
    private val onItemClick: (WhiteNoise) -> Unit,
    private val onPreviewClick: (WhiteNoise) -> Unit
) : ListAdapter<WhiteNoise, WhiteNoiseAdapter.WhiteNoiseViewHolder>(WhiteNoiseDiffCallback()) {

    private var selectedId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhiteNoiseViewHolder {
        val binding = ItemWhiteNoiseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WhiteNoiseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhiteNoiseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedId(id: String) {
        selectedId = id
        notifyDataSetChanged()
    }

    inner class WhiteNoiseViewHolder(
        private val binding: ItemWhiteNoiseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(whiteNoise: WhiteNoise) {
            binding.apply {
                tvNoiseName.text = whiteNoise.name
                ivNoiseIcon.setImageResource(whiteNoise.iconRes)

                // 设置选中状态
                val isSelected = whiteNoise.id == selectedId
                root.isSelected = isSelected

                // 根据选中状态更新选择按钮文本
                btnSelect.text = if (isSelected) "已选择" else "选择"
                btnSelect.isEnabled = !isSelected

                // 点击选择按钮
                btnSelect.setOnClickListener {
                    onItemClick(whiteNoise)
                }

                // 点击试听
                btnPreview.setOnClickListener {
                    onPreviewClick(whiteNoise)
                }
            }
        }
    }

    class WhiteNoiseDiffCallback : DiffUtil.ItemCallback<WhiteNoise>() {
        override fun areItemsTheSame(oldItem: WhiteNoise, newItem: WhiteNoise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WhiteNoise, newItem: WhiteNoise): Boolean {
            return oldItem == newItem
        }
    }
}
