package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.dao.FocusSessionDao
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.local.entity.FocusSessionEntity

/**
 * Room数据库
 * 应用的主数据库，管理所有本地数据持久化
 */
@Database(
    entities = [FocusSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取专注记录DAO
     */
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库单例
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_flow_database"
                )
                    .fallbackToDestructiveMigration() // 简化开发，生产环境应使用Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
