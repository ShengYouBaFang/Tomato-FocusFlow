package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.home.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R

/**
 * 自定义圆形进度条View
 * 显示倒计时进度，支持颜色渐变动画
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 进度值（0.0 - 1.0）
    private var progress: Float = 1f

    // 画笔
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.text_hint)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }

    // 绘制区域
    private val rectF = RectF()

    // 颜色
    private val startColor = ContextCompat.getColor(context, R.color.progress_start)
    private val endColor = ContextCompat.getColor(context, R.color.progress_end)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 30f
        rectF.set(
            padding,
            padding,
            w.toFloat() - padding,
            h.toFloat() - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制背景圆环
        canvas.drawArc(rectF, 0f, 360f, false, backgroundPaint)

        // 根据进度计算颜色
        val color = interpolateColor(startColor, endColor, progress)
        progressPaint.color = color

        // 绘制进度圆弧（从顶部开始，顺时针）
        val sweepAngle = 360f * progress
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)
    }

    /**
     * 设置进度
     * @param value 0.0 - 1.0
     */
    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 1f)
        invalidate()
    }

    /**
     * 获取当前进度
     */
    fun getProgress(): Float = progress

    /**
     * 颜色插值
     */
    private fun interpolateColor(startColor: Int, endColor: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio

        val r = (android.graphics.Color.red(endColor) * ratio +
                android.graphics.Color.red(startColor) * inverseRatio).toInt()
        val g = (android.graphics.Color.green(endColor) * ratio +
                android.graphics.Color.green(startColor) * inverseRatio).toInt()
        val b = (android.graphics.Color.blue(endColor) * ratio +
                android.graphics.Color.blue(startColor) * inverseRatio).toInt()

        return android.graphics.Color.rgb(r, g, b)
    }
}
