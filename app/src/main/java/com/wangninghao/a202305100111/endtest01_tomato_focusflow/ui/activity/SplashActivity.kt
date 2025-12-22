package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.ActivitySplashBinding

/**
 * 启动页 Activity
 * 展示应用Logo和开发者信息，延迟后跳转到主界面
 */
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashDelay = 2000L // 2秒延迟
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置开发者信息
        val developerInfo = getString(
            R.string.developer_info,
            getString(R.string.developer_name),
            getString(R.string.student_id)
        )
        binding.developerInfoText.text = developerInfo
        
        // 延迟后跳转到主界面
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashDelay)
    }
}
