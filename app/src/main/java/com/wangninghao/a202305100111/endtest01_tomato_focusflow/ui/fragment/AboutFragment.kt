package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentAboutBinding

/**
 * 关于 Fragment
 * 主要功能：显示应用 Logo 和名称、版本号、开发者信息、GitHub 链接
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 设置版本信息
        binding.versionText.text = getVersionName()
        
        // 设置开发者信息（已经在XML中通过字符串资源设置了，这里可以动态设置）
        binding.developerNameText.text = getString(R.string.developer_name)
        binding.studentIdText.text = getString(R.string.student_id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 获取应用版本名
     * @return 版本名字符串，格式为 "v1.0"
     */
    private fun getVersionName(): String {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            )
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
            "v${packageInfo.versionName} ($versionCode)"
        } catch (e: PackageManager.NameNotFoundException) {
            "v1.0"
        }
    }
}