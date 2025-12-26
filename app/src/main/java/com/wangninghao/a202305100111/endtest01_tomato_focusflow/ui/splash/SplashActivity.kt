package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.ActivitySplashBinding
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.main.MainActivity

/**
 * 启动页面
 * 展示开发者信息：姓名、学号、头像
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        startAnimations()
    }

    private fun startAnimations() {
        // 欢迎文本动画
        binding.tvWelcome.alpha = 0f
        binding.tvWelcome.translationY = -30f
        binding.tvWelcome.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .start()

        // 开发者头像卡片动画
        binding.cardDeveloperAvatar.alpha = 0f
        binding.cardDeveloperAvatar.scaleX = 0.5f
        binding.cardDeveloperAvatar.scaleY = 0.5f
        binding.cardDeveloperAvatar.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        // 开发者姓名动画
        binding.tvDeveloperName.alpha = 0f
        binding.tvDeveloperName.translationY = 50f
        binding.tvDeveloperName.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(400)
            .start()

        // 学号动画
        binding.tvStudentId.alpha = 0f
        binding.tvStudentId.translationY = 50f
        binding.tvStudentId.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(500)
            .start()

        // 应用信息动画
        binding.tvAppInfo.alpha = 0f
        binding.tvAppInfo.animate()
            .alpha(1f)
            .setDuration(400)
            .setStartDelay(600)
            .start()

        // 按钮动画
        binding.btnEnter.alpha = 0f
        binding.btnEnter.scaleX = 0.8f
        binding.btnEnter.scaleY = 0.8f
        binding.btnEnter.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setStartDelay(700)
            .start()
    }

    private fun setupListeners() {
        // 点击进入按钮，跳转到主界面
        binding.btnEnter.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
