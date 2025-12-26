package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentStatisticsBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 数据统计Fragment
 * 展示专注数据统计和图表
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        observeViewModel()
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

        // 最长记录卡片动画
        binding.cardLongest.alpha = 0f
        binding.cardLongest.scaleX = 0.9f
        binding.cardLongest.scaleY = 0.9f
        binding.cardLongest.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay(100)
            .start()

        // 统计网格动画
        binding.layoutStatsGrid.alpha = 0f
        binding.layoutStatsGrid.translationX = -50f
        binding.layoutStatsGrid.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(400)
            .setStartDelay(200)
            .start()

        // 图表标题动画
        binding.tvChartTitle.alpha = 0f
        binding.tvChartTitle.translationX = -50f
        binding.tvChartTitle.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(400)
            .setStartDelay(300)
            .start()

        // 图表动画
        binding.chartFocusHistory.alpha = 0f
        binding.chartFocusHistory.translationY = 50f
        binding.chartFocusHistory.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(400)
            .start()
    }

    private fun setupChart() {
        binding.chartFocusHistory.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setPinchZoom(false)
            setScaleEnabled(false)

            // X轴配置
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 10f
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
            }

            // 左Y轴配置
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textSize = 10f
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                axisMinimum = 0f
            }

            // 右Y轴禁用
            axisRight.isEnabled = false

            // 图例
            legend.isEnabled = false

            // 动画
            animateY(1000)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察最长记录
                launch {
                    viewModel.longestSession.collect { session ->
                        try {
                            if (session != null) {
                                binding.tvLongestDuration.text = TimeFormatter.formatDuration(session.duration)
                            } else {
                                binding.tvLongestDuration.text = "--"
                            }
                        } catch (e: Exception) {
                            binding.tvLongestDuration.text = "--"
                            e.printStackTrace()
                        }
                    }
                }

                // 观察总时长
                launch {
                    viewModel.totalDuration.collect { duration ->
                        try {
                            if (duration > 0) {
                                binding.tvTotalDuration.text = formatTotalDuration(duration)
                            } else {
                                binding.tvTotalDuration.text = "--"
                            }
                        } catch (e: Exception) {
                            binding.tvTotalDuration.text = "--"
                            e.printStackTrace()
                        }
                    }
                }

                // 观察总次数
                launch {
                    viewModel.totalCount.collect { count ->
                        try {
                            if (count > 0) {
                                binding.tvTotalCount.text = "${count}次"
                            } else {
                                binding.tvTotalCount.text = "--"
                            }
                        } catch (e: Exception) {
                            binding.tvTotalCount.text = "--"
                            e.printStackTrace()
                        }
                    }
                }

                // 观察已完成记录并更新图表
                launch {
                    viewModel.completedSessions.collect { sessions ->
                        try {
                            updateChart(sessions)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding.chartFocusHistory.clear()
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新图表数据
     */
    private fun updateChart(sessions: List<FocusSessionEntity>) {
        if (sessions.isEmpty()) {
            binding.chartFocusHistory.clear()
            return
        }

        // 取最近10条记录
        val recentSessions = sessions.takeLast(10)
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        recentSessions.forEachIndexed { index, session ->
            // 将毫秒转换为分钟
            val minutes = (session.duration / 1000 / 60).toFloat()
            entries.add(BarEntry(index.toFloat(), minutes))
            labels.add(dateFormat.format(Date(session.endTime)))
        }

        // 创建数据集
        val dataSet = BarDataSet(entries, "专注时长").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}m"
                }
            }
        }

        // 设置数据
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        binding.chartFocusHistory.apply {
            data = barData
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels.getOrNull(value.toInt()) ?: ""
                }
            }
            xAxis.labelCount = labels.size
            invalidate()
        }
    }

    /**
     * 格式化总时长
     */
    private fun formatTotalDuration(milliseconds: Long): String {
        val hours = milliseconds / 1000 / 60 / 60
        val minutes = (milliseconds / 1000 / 60) % 60

        return when {
            hours > 0 -> "${hours}小时${minutes}分"
            minutes > 0 -> "${minutes}分钟"
            else -> "少于1分钟"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
