package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentTimerBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.service.ServiceLifecycleManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.customview.CircularTimerView
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

/**
 * 番茄钟Fragment
 * 主要功能：倒计时、自定义View、开始/暂停/停止控制
 */
class TimerFragment : Fragment() {
    
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var serviceManager: ServiceLifecycleManager
    private lateinit var viewModel: TimerViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceManager = ServiceLifecycleManager(requireContext())
        viewModel = ViewModelProvider(this)[TimerViewModel::class.java]
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化UI
        setupUI()
        
        // 观察 ViewModel 状态
        observeViewModel()
        
        // 设置初始时间
        binding.circularTimerView.setTime(Constants.TIMER_25_MIN, Constants.TIMER_25_MIN)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceManager.destroy()
    }
    
    /**
     * 设置UI控件
     */
    private fun setupUI() {
        // 设置圆形计时器
        binding.circularTimerView.setOnTimeSetListener(object : CircularTimerView.OnTimeSetListener {
            override fun onTimeSet(newTotalTime: Long) {
                binding.circularTimerView.setTime(newTotalTime, newTotalTime)
            }
        })
        
        // 设置时长选择按钮
        binding.btn25Min.setOnClickListener {
            setTimerDuration(Constants.TIMER_25_MIN)
        }
        
        binding.btn45Min.setOnClickListener {
            setTimerDuration(Constants.TIMER_45_MIN)
        }
        
        binding.btn60Min.setOnClickListener {
            setTimerDuration(Constants.TIMER_60_MIN)
        }
        
        // 设置控制按钮
        binding.btnStart.setOnClickListener {
            lifecycleScope.launch {
                viewModel.defaultDuration.collect { duration ->
                    val durationMillis = duration * 60 * 1000L
                    viewModel.startTimer(durationMillis)
                }
            }
        }
        
        binding.btnPause.setOnClickListener {
            viewModel.pauseTimer()
        }
        
        binding.btnStop.setOnClickListener {
            viewModel.stopTimer()
        }
    }
    
    /**
     * 观察 ViewModel 状态
     */
    private fun observeViewModel() {
        // 观察剩余时间
        viewModel.remainingTime.observe(viewLifecycleOwner) { remainingTime ->
            binding.circularTimerView.setRemainingTime(remainingTime)
        }
        
        // 观察总时间
        viewModel.totalTime.observe(viewLifecycleOwner) { totalTime ->
            binding.circularTimerView.setTotalTime(totalTime)
        }
        
        // 观察运行状态
        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.circularTimerView.setRunning(isRunning)
            updateControlButtons(isRunning)
        }
    }
    
    /**
     * 设置计时器时长
     */
    private fun setTimerDuration(duration: Long) {
        viewModel.isRunning.value?.let { isRunning ->
            if (!isRunning) {
                binding.circularTimerView.setTime(duration, duration)
            }
        }
    }
    
    /**
     * 更新控制按钮状态
     */
    private fun updateControlButtons(isRunning: Boolean) {
        if (isRunning) {
            binding.btnStart.visibility = View.GONE
            binding.btnPause.visibility = View.VISIBLE
            binding.btnStop.visibility = View.VISIBLE
        } else {
            binding.btnStart.visibility = View.VISIBLE
            binding.btnPause.visibility = View.GONE
            binding.btnStop.visibility = View.GONE
        }
    }
}