package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentHomeBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.service.TimerService
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter

/**
 * 主界面Fragment
 * 包含倒计时器和白噪音播放控制
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var rotationAnimator: ObjectAnimator? = null

    // 标志位，防止开关状态循环触发
    private var isUpdatingSwitch = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        viewModel.bindService(requireContext())
        animateViews()
    }

    private fun animateViews() {
        // 标题淡入
        binding.tvTitle.alpha = 0f
        binding.tvTitle.animate()
            .alpha(1f)
            .setDuration(400)
            .start()

        // 白噪音卡片从上滑入
        binding.cardWhiteNoise.alpha = 0f
        binding.cardWhiteNoise.translationY = -50f
        binding.cardWhiteNoise.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(100)
            .start()

        // 圆形进度条缩放淡入
        binding.circularProgress.alpha = 0f
        binding.circularProgress.scaleX = 0.8f
        binding.circularProgress.scaleY = 0.8f
        binding.circularProgress.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setStartDelay(200)
            .start()

        // 时间文本淡入
        binding.tvTime.alpha = 0f
        binding.tvTime.animate()
            .alpha(1f)
            .setDuration(400)
            .setStartDelay(400)
            .start()

        // 控制按钮从下滑入
        binding.layoutButtons.alpha = 0f
        binding.layoutButtons.translationY = 50f
        binding.layoutButtons.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(500)
            .start()
    }

    private fun setupUI() {
        // 播放/暂停按钮
        binding.btnPlayPause.setOnClickListener {
            handlePlayPauseClick()
        }

        // 重置按钮
        binding.btnReset.setOnClickListener {
            viewModel.resetTimer(requireContext())
        }

        // 设置按钮
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_timer_setting)
        }

        // 白噪音开关
        binding.switchWhiteNoise.setOnCheckedChangeListener { _, isChecked ->
            // 只有在非程序更新时才响应用户操作
            if (!isUpdatingSwitch) {
                viewModel.setWhiteNoiseEnabled(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        // 观察白噪音
        viewModel.selectedWhiteNoise.observe(viewLifecycleOwner) { whiteNoise ->
            binding.tvWhiteNoiseName.text = whiteNoise.name
            binding.ivWhiteNoiseIcon.setImageResource(whiteNoise.iconRes)
        }

        // 观察白噪音开关状态
        viewModel.whiteNoiseEnabled.observe(viewLifecycleOwner) { enabled ->
            // 设置标志位，防止触发监听器
            isUpdatingSwitch = true
            binding.switchWhiteNoise.isChecked = enabled
            isUpdatingSwitch = false

            // 根据开关状态和倒计时状态控制旋转动画
            updateRotationAnimation()
        }

        // 观察倒计时时长
        viewModel.timerDuration.observe(viewLifecycleOwner) { duration ->
            if (viewModel.timerState.value is TimerService.TimerState.Idle) {
                binding.tvTime.text = TimeFormatter.formatTime(duration)
                binding.circularProgress.setProgress(1f)
            }
        }

        // 观察倒计时状态
        viewModel.timerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TimerService.TimerState.Idle -> {
                    binding.btnPlayPause.text = "开始"
                    binding.btnPlayPause.isEnabled = true
                    binding.btnReset.isEnabled = false
                    // 重置后显示设置的倒计时时长
                    val duration = viewModel.timerDuration.value ?: 25 * 60 * 1000L
                    binding.tvTime.text = TimeFormatter.formatTime(duration)
                    binding.circularProgress.setProgress(1f)
                    stopRotationAnimation()
                    // 重置图标角度
                    binding.ivWhiteNoiseIcon.rotation = 0f
                }
                is TimerService.TimerState.Running -> {
                    binding.btnPlayPause.text = "暂停"
                    binding.btnPlayPause.isEnabled = true
                    binding.btnReset.isEnabled = true
                    binding.tvTime.text = TimeFormatter.formatTime(state.remainingTime)
                    binding.circularProgress.setProgress(1f - state.progress)
                    // 根据开关状态控制旋转动画
                    updateRotationAnimation()
                }
                is TimerService.TimerState.Paused -> {
                    binding.btnPlayPause.text = "继续"
                    binding.btnPlayPause.isEnabled = true
                    binding.btnReset.isEnabled = true
                    binding.tvTime.text = TimeFormatter.formatTime(state.remainingTime)
                    binding.circularProgress.setProgress(1f - state.progress)
                    stopRotationAnimation()
                }
                is TimerService.TimerState.Completed -> {
                    binding.btnPlayPause.text = "开始"
                    binding.btnPlayPause.isEnabled = true
                    binding.btnReset.isEnabled = true
                    binding.tvTime.text = "00:00"
                    binding.circularProgress.setProgress(0f)
                    stopRotationAnimation()
                    // 重置图标角度
                    binding.ivWhiteNoiseIcon.rotation = 0f
                }
            }
        }
    }

    private fun handlePlayPauseClick() {
        when (viewModel.timerState.value) {
            is TimerService.TimerState.Idle -> {
                viewModel.startTimer(requireContext())
            }
            is TimerService.TimerState.Running -> {
                viewModel.pauseTimer()
            }
            is TimerService.TimerState.Paused -> {
                viewModel.resumeTimer()
            }
            is TimerService.TimerState.Completed -> {
                viewModel.startTimer(requireContext())
            }
            else -> {}
        }
    }

    /**
     * 开始旋转动画
     */
    private fun startRotationAnimation() {
        // 如果已经在运行，直接返回
        if (rotationAnimator?.isRunning == true) return

        // 从当前角度开始旋转
        val currentRotation = binding.ivWhiteNoiseIcon.rotation
        rotationAnimator = ObjectAnimator.ofFloat(
            binding.ivWhiteNoiseIcon,
            "rotation",
            currentRotation,
            currentRotation + 360f
        ).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = LinearInterpolator()
            start()
        }
    }

    /**
     * 停止旋转动画（保持当前角度）
     */
    private fun stopRotationAnimation() {
        rotationAnimator?.cancel()
        rotationAnimator = null
    }

    /**
     * 根据倒计时状态和白噪音开关状态更新旋转动画
     */
    private fun updateRotationAnimation() {
        val isRunning = viewModel.timerState.value is TimerService.TimerState.Running
        val isWhiteNoiseEnabled = viewModel.whiteNoiseEnabled.value == true

        if (isRunning && isWhiteNoiseEnabled) {
            startRotationAnimation()
        } else {
            stopRotationAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        // 刷新设置，以防用户从设置页面返回
        viewModel.refreshSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRotationAnimation()
        viewModel.unbindService(requireContext())
        _binding = null
    }
}
