package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentTimerSettingBinding

/**
 * 倒计时设置Fragment
 * 设置倒计时时长
 */
class TimerSettingFragment : Fragment() {

    private var _binding: FragmentTimerSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerSettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        animateViews()
    }

    private fun animateViews() {
        // 标题动画
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = -50f
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .start()

        // 快捷按钮组动画
        binding.layoutQuickButtons.alpha = 0f
        binding.layoutQuickButtons.translationX = -100f
        binding.layoutQuickButtons.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(400)
            .setStartDelay(100)
            .start()

        // 自定义时间卡片动画
        binding.cardCustomTime.alpha = 0f
        binding.cardCustomTime.scaleX = 0.9f
        binding.cardCustomTime.scaleY = 0.9f
        binding.cardCustomTime.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay(200)
            .start()

        // 确认按钮动画
        binding.btnConfirm.alpha = 0f
        binding.btnConfirm.translationY = 50f
        binding.btnConfirm.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(300)
            .start()
    }

    private fun setupUI() {
        // 配置NumberPicker - 延长时间限制到23小时59分
        binding.npHours.apply {
            minValue = 0
            maxValue = 23  // 最多23小时
            value = 0
            wrapSelectorWheel = true
        }

        binding.npMinutes.apply {
            minValue = 0
            maxValue = 59  // 最多59分钟
            value = 25
            wrapSelectorWheel = true
        }

        // 快捷按钮
        binding.btn10min.setOnClickListener {
            viewModel.saveQuickTimer(10)
            showToast("已设置为10分钟")
            findNavController().navigateUp()
        }

        binding.btn15min.setOnClickListener {
            viewModel.saveQuickTimer(15)
            showToast("已设置为15分钟")
            findNavController().navigateUp()
        }

        binding.btn30min.setOnClickListener {
            viewModel.saveQuickTimer(30)
            showToast("已设置为30分钟")
            findNavController().navigateUp()
        }

        binding.btn60min.setOnClickListener {
            viewModel.saveQuickTimer(60)
            showToast("已设置为60分钟")
            findNavController().navigateUp()
        }

        // 确认按钮 - 应用自定义时间
        binding.btnConfirm.setOnClickListener {
            val hours = binding.npHours.value
            val minutes = binding.npMinutes.value

            // 验证：至少要有1分钟
            if (hours == 0 && minutes == 0) {
                showToast("请至少设置1分钟")
                return@setOnClickListener
            }

            viewModel.saveTimerDuration(hours, minutes)

            // 显示设置的时间
            val timeText = when {
                hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟"
                hours > 0 -> "${hours}小时"
                else -> "${minutes}分钟"
            }
            showToast("已设置为$timeText")

            findNavController().navigateUp()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
