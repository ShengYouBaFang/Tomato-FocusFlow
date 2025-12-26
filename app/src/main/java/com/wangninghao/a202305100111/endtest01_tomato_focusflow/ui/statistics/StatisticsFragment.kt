package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentStatisticsBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.util.TimeFormatter
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
        // 观察最长记录
        viewModel.longestSession.observe(viewLifecycleOwner) { session ->
            if (session != null) {
                binding.tvLongestDuration.text = TimeFormatter.formatTime(session.duration)
            } else {
                binding.tvLongestDuration.text = "--"
            }
        }

        // 观察总时长
        viewModel.totalDuration.observe(viewLifecycleOwner) { duration ->
            if (duration != null && duration > 0) {
                binding.tvTotalDuration.text = formatTotalDuration(duration)
            } else {
                binding.tvTotalDuration.text = "--"
            }
        }

        // 观察总次数
        viewModel.totalCount.observe(viewLifecycleOwner) { count ->
            if (count != null && count > 0) {
                binding.tvTotalCount.text = "${count}次"
            } else {
                binding.tvTotalCount.text = "--"
            }
        }

        // 观察已完成记录并更新图表
        viewModel.completedSessions.observe(viewLifecycleOwner) { sessions ->
            updateChart(sessions)
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
