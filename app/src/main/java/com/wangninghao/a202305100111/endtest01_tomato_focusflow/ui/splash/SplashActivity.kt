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
    }

    private fun setupListeners() {
        // 点击进入按钮，跳转到主界面
        binding.btnEnter.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
