package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio.WhiteNoisePlayer
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio.WhiteNoiseType
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentWhiteNoiseBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment.adapter.WhiteNoiseAdapter

/**
 * 白噪音Fragment
 * 主要功能：音频列表、播放控制、音量调节
 */
class WhiteNoiseFragment : Fragment() {
    
    private var _binding: FragmentWhiteNoiseBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var whiteNoisePlayer: WhiteNoisePlayer
    private lateinit var adapter: WhiteNoiseAdapter
    private var currentPlayingPosition = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whiteNoisePlayer = WhiteNoisePlayer(requireContext())
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhiteNoiseBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化RecyclerView
        setupRecyclerView()
        
        // 初始化音量控制
        setupVolumeControl()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        whiteNoisePlayer.release()
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        val noiseList = WhiteNoiseType.getAllTypes()
        adapter = WhiteNoiseAdapter(noiseList, object : WhiteNoiseAdapter.OnItemClickListener {
            override fun onItemClick(noiseType: WhiteNoiseType, position: Int) {
                handleNoiseItemClick(noiseType, position)
            }
        })
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WhiteNoiseFragment.adapter
        }
    }
    
    /**
     * 处理白噪音项点击事件
     */
    private fun handleNoiseItemClick(noiseType: WhiteNoiseType, position: Int) {
        if (currentPlayingPosition == position) {
            // 点击正在播放的项，暂停播放
            whiteNoisePlayer.pause()
            adapter.stopPlaying()
            currentPlayingPosition = -1
        } else {
            // 点击其他项，播放新的音频
            val volume = binding.volumeSeekBar.progress / 100.0f
            whiteNoisePlayer.play(noiseType.audioRes, volume)
            adapter.setPlayingPosition(position)
            currentPlayingPosition = position
        }
    }
    
    /**
     * 设置音量控制
     */
    private fun setupVolumeControl() {
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val volume = progress / 100.0f
                    whiteNoisePlayer.setVolume(volume)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }
}
