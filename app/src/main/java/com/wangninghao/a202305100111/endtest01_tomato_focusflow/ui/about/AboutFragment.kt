package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentAboutBinding

/**
 * 关于页面Fragment
 * 展示应用信息和开发者信息
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.tvVersion.text = "版本 1.0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
