package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentWhiteNoiseBinding

/**
 * 白噪音Fragment
 * 主要功能：音频列表、播放控制、音量调节
 */
class WhiteNoiseFragment : Fragment() {
    
    private var _binding: FragmentWhiteNoiseBinding? = null
    private val binding get() = _binding!!
    
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
        // TODO: 后续实现白噪音播放逻辑
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
