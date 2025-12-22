package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.TimeFormatter
import kotlin.math.atan2
import kotlin.math.min

/**
 * 圆形计时器视图
 * 显示倒计时进度，颜色随时间变化，支持触摸调整时长
 */
class CircularTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    // 画笔
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = ContextCompat.getColor(context, R.color.divider_light)
    }
    
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 80f
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_primary_light)
        isFakeBoldText = true
    }
    
    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_secondary_light)
    }
    
    // 矩形边界
    private val rectF = RectF()
    
    // 状态变量
    private var totalTime: Long = 0
    private var remainingTime: Long = 0
    private var isRunning = false
    
    // 触摸相关
    private var isDragging = false
    private var dragStartTime: Long = 0
    private var dragStartAngle: Float = 0f
    private var onTimeSetListener: OnTimeSetListener? = null
    
    /**
     * 设置总时间和剩余时间
     */
    fun setTime(totalTime: Long, remainingTime: Long) {
        this.totalTime = totalTime
        this.remainingTime = remainingTime
        invalidate()
    }
    
    /**
     * 设置总时间
     */
    fun setTotalTime(totalTime: Long) {
        this.totalTime = totalTime
        invalidate()
    }
    
    /**
     * 设置剩余时间
     */
    fun setRemainingTime(remainingTime: Long) {
        this.remainingTime = remainingTime
        invalidate()
    }
    
    /**
     * 设置运行状态
     */
    fun setRunning(isRunning: Boolean) {
        this.isRunning = isRunning
        invalidate()
    }
    
    /**
     * 设置时间设置监听器
     */
    fun setOnTimeSetListener(listener: OnTimeSetListener) {
        this.onTimeSetListener = listener
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val strokeWidth = backgroundPaint.strokeWidth
        rectF.set(
            strokeWidth / 2,
            strokeWidth / 2,
            w - strokeWidth / 2,
            h - strokeWidth / 2
        )
        
        // 调整文字大小以适应视图
        val textSize = min(w, h) * 0.2f
        textPaint.textSize = textSize
        smallTextPaint.textSize = textSize * 0.4f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        
        // 绘制背景圆环
        canvas.drawCircle(centerX, centerY, rectF.width() / 2, backgroundPaint)
        
        // 绘制进度圆环
        if (totalTime > 0) {
            val progress = (totalTime - remainingTime).toFloat() / totalTime
            val sweepAngle = 360f * progress
            
            // 根据进度更新颜色
            progressPaint.color = getColorForProgress(progress)
            
            canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)
        }
        
        // 绘制中心时间文本
        val timeText = TimeFormatter.formatTime(remainingTime)
        val textBounds = android.graphics.Rect()
        textPaint.getTextBounds(timeText, 0, timeText.length, textBounds)
        
        canvas.drawText(timeText, centerX, centerY + textBounds.height() / 2f, textPaint)
        
        // 绘制状态文本
        val statusText = if (isRunning) "进行中" else "已暂停"
        canvas.drawText(statusText, centerX, centerY + textBounds.height() + 80f, smallTextPaint)
    }
    
    /**
     * 根据进度返回相应的颜色
     * 绿色 → 黄色 → 红色
     */
    private fun getColorForProgress(progress: Float): Int {
        return when {
            progress <= 0.5f -> {
                // 绿色到黄色过渡 (0% - 50%)
                val ratio = progress / 0.5f
                blendColors(
                    ContextCompat.getColor(context, R.color.progress_green),
                    ContextCompat.getColor(context, R.color.progress_yellow),
                    ratio
                )
            }
            else -> {
                // 黄色到红色过渡 (50% - 100%)
                val ratio = (progress - 0.5f) / 0.5f
                blendColors(
                    ContextCompat.getColor(context, R.color.progress_yellow),
                    ContextCompat.getColor(context, R.color.progress_red),
                    ratio
                )
            }
        }
    }
    
    /**
     * 混合两种颜色
     */
    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        
        val r = (r1 + (r2 - r1) * ratio).toInt()
        val g = (g1 + (g2 - g1) * ratio).toInt()
        val b = (b1 + (b2 - b1) * ratio).toInt()
        
        return Color.rgb(r, g, b)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val centerX = width / 2f
        val centerY = height / 2f
        val x = event.x - centerX
        val y = event.y - centerY
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 计算触摸点与中心点的角度
                val touchRadius = kotlin.math.sqrt(x * x + y * y)
                val circleRadius = rectF.width() / 2
                
                // 只有在圆环附近触摸才响应
                if (touchRadius in (circleRadius - 50)..(circleRadius + 50)) {
                    isDragging = true
                    dragStartTime = System.currentTimeMillis()
                    dragStartAngle = atan2(y, x) * 180f / Math.PI.toFloat()
                    return true
                }
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (isDragging && !isRunning) {
                    val currentAngle = atan2(y, x) * 180f / Math.PI.toFloat()
                    val angleDiff = currentAngle - dragStartAngle
                    
                    // 根据角度差调整时间
                    val timeAdjustment = (angleDiff / 360f * 60 * 60 * 1000).toLong()
                    val newTotalTime = (totalTime - timeAdjustment).coerceIn(
                        1 * 60 * 1000L, // 最小1分钟
                        120 * 60 * 1000L // 最大120分钟
                    )
                    
                    if (newTotalTime != totalTime) {
                        totalTime = newTotalTime
                        remainingTime = newTotalTime
                        dragStartAngle = currentAngle
                        invalidate()
                    }
                    return true
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    // 触摸结束时回调新的时间值
                    onTimeSetListener?.onTimeSet(totalTime)
                    return true
                }
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    /**
     * 时间设置监听器
     */
    interface OnTimeSetListener {
        fun onTimeSet(newTotalTime: Long)
    }
}