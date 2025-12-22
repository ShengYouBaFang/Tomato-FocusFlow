package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio.WhiteNoiseType

/**
 * 白噪音列表适配器
 */
class WhiteNoiseAdapter(
    private val noiseList: List<WhiteNoiseType>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WhiteNoiseAdapter.ViewHolder>() {
    
    interface OnItemClickListener {
        fun onItemClick(noiseType: WhiteNoiseType, position: Int)
    }
    
    private var playingPosition = -1
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_white_noise, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noiseType = noiseList[position]
        
        // 设置图标和名称
        holder.iconImageView.setImageResource(noiseType.iconRes)
        holder.nameTextView.setText(noiseType.titleRes)
        
        // 设置播放状态
        if (position == playingPosition) {
            holder.statusTextView.setText(R.string.timer_pause)
            holder.statusTextView.setTextColor(
                holder.itemView.context.getColor(R.color.status_success)
            )
        } else {
            holder.statusTextView.setText(R.string.timer_play)
            holder.statusTextView.setTextColor(
                holder.itemView.context.getColor(R.color.status_warning)
            )
        }
        
        // 设置点击事件
        holder.itemView.setOnClickListener {
            val previousPlayingPosition = playingPosition
            playingPosition = position
            onItemClickListener.onItemClick(noiseType, position)
            
            // 更新UI
            notifyItemChanged(previousPlayingPosition)
            notifyItemChanged(playingPosition)
        }
    }
    
    override fun getItemCount(): Int = noiseList.size
    
    /**
     * 设置正在播放的位置
     */
    fun setPlayingPosition(position: Int) {
        val previousPlayingPosition = playingPosition
        playingPosition = position
        notifyItemChanged(previousPlayingPosition)
        notifyItemChanged(playingPosition)
    }
    
    /**
     * 停止播放
     */
    fun stopPlaying() {
        val previousPlayingPosition = playingPosition
        playingPosition = -1
        notifyItemChanged(previousPlayingPosition)
    }
}