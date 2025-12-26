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
    }

    private fun observeViewModel() {
        // 观察白噪音
        viewModel.selectedWhiteNoise.observe(viewLifecycleOwner) { whiteNoise ->
            binding.tvWhiteNoiseName.text = whiteNoise.name
            binding.ivWhiteNoiseIcon.setImageResource(whiteNoise.iconRes)
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
                    stopRotationAnimation()
                }
                is TimerService.TimerState.Running -> {
                    binding.btnPlayPause.text = "暂停"
                    binding.btnPlayPause.isEnabled = true
                    binding.btnReset.isEnabled = true
                    binding.tvTime.text = TimeFormatter.formatTime(state.remainingTime)
                    binding.circularProgress.setProgress(1f - state.progress)
                    startRotationAnimation()
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
                    binding.btnReset.isEnabled = false
                    binding.tvTime.text = "00:00"
                    binding.circularProgress.setProgress(0f)
                    stopRotationAnimation()
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
        if (rotationAnimator?.isRunning == true) return

        rotationAnimator = ObjectAnimator.ofFloat(
            binding.ivWhiteNoiseIcon,
            "rotation",
            0f,
            360f
        ).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    /**
     * 停止旋转动画
     */
    private fun stopRotationAnimation() {
        rotationAnimator?.cancel()
        binding.ivWhiteNoiseIcon.rotation = 0f
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
