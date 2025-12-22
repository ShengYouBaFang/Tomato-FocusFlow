package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 用户偏好设置仓库
 */
class PreferencesRepository(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = Constants.PREFERENCES_NAME
        )
        
        private val KEY_DEFAULT_DURATION = intPreferencesKey(Constants.KEY_DEFAULT_DURATION)
        private val KEY_VOLUME = floatPreferencesKey(Constants.KEY_VOLUME)
        private val KEY_THEME_MODE = intPreferencesKey(Constants.KEY_THEME_MODE)
        private val KEY_NOTIFICATION_ENABLED = booleanPreferencesKey(Constants.KEY_NOTIFICATION_ENABLED)
    }
    
    /**
     * 获取默认时长（分钟）
     */
    val defaultDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_DURATION] ?: 25
    }
    
    /**
     * 设置默认时长
     */
    suspend fun setDefaultDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_DURATION] = duration
        }
    }
    
    /**
     * 获取音量（0.0 - 1.0）
     */
    val volume: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_VOLUME] ?: 0.5f
    }
    
    /**
     * 设置音量
     */
    suspend fun setVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOLUME] = volume
        }
    }
    
    /**
     * 获取主题模式（0: 浅色, 1: 深色, 2: 跟随系统）
     */
    val themeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: 2
    }
    
    /**
     * 设置主题模式
     */
    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }
    
    /**
     * 获取通知是否启用
     */
    val notificationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATION_ENABLED] ?: true
    }
    
    /**
     * 设置通知启用状态
     */
    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_ENABLED] = enabled
        }
    }
}
