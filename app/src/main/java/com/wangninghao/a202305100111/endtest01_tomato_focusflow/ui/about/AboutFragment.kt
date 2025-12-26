package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
        setupAnimations()
    }

    private fun setupUI() {
        // 获取版本信息
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            )
            val versionName = packageInfo.versionName
            binding.tvVersion.text = "版本 $versionName"
        } catch (e: Exception) {
            binding.tvVersion.text = "版本 1.0.0"
        }
    }

    private fun setupAnimations() {
        // 为卡片添加淡入动画 - 修复闪烁问题
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in).apply {
            duration = 400  // 缩短动画时长
        }

        // 设置初始透明度为0，避免闪烁
        binding.cardDescription.alpha = 0f
        binding.cardDeveloper.alpha = 0f
        binding.cardTech.alpha = 0f

        // 延迟启动动画，使用View的animate API替代AnimationUtils
        binding.cardDescription.postDelayed({
            binding.cardDescription.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 50)

        binding.cardDeveloper.postDelayed({
            binding.cardDeveloper.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 100)

        binding.cardTech.postDelayed({
            binding.cardTech.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
