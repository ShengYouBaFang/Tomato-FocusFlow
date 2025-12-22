package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.ActivityMainNewBinding

/**
 * 主Activity
 * 使用BottomNavigationView + Navigation组件管理Fragment导航
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainNewBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    /**
     * 配置导航
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // 将BottomNavigationView与NavController绑定
        binding.bottomNavigation.setupWithNavController(navController)
        
        // 监听导航变化，更新Toolbar标题
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = destination.label
        }
    }
}
