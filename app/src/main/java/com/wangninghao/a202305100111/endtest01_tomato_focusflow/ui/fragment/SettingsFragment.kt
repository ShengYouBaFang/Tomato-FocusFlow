package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentSettingsBinding

/**
 * 设置Fragment
 * 主要功能：通知开关、默认时长、主题切换、清除数据
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 添加设置Fragment
        if (savedInstanceState == null) {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.settingsContainer, SettingsPreferenceFragment())
                .commit()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * 设置PreferenceFragment
     */
    class SettingsPreferenceFragment : PreferenceFragmentCompat() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            // 设置清除历史记录点击事件
            val clearHistoryPreference = findPreference<Preference>("clear_history")
            clearHistoryPreference?.setOnPreferenceClickListener {
                showClearHistoryDialog()
                true
            }
        }
        
        /**
         * 显示清除历史记录确认对话框
         */
        private fun showClearHistoryDialog() {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.history_delete_all)
                .setMessage(R.string.history_delete_confirm)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    // TODO: 实现清除历史记录功能
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}