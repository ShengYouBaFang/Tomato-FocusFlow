package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.sound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentSoundSelectionBinding

/**
 * 白噪音选择Fragment
 * 展示可选择的白噪音列表
 */
class SoundSelectionFragment : Fragment() {

    private var _binding: FragmentSoundSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // TODO: 后续实现白噪音列表和选择功能
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
