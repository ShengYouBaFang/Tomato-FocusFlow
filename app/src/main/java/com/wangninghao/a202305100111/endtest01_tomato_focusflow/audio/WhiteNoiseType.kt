package com.wangninghao.a202305100111.endtest01_tomato_focusflow.audio

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R

/**
 * 白噪音类型枚举
 */
enum class WhiteNoiseType(
    @StringRes val titleRes: Int,
    @RawRes val audioRes: Int,
    @DrawableRes val iconRes: Int
) {
    RAIN(
        R.string.noise_rain,
        R.raw.rain,  // 需要添加实际的音频文件
        R.drawable.ic_water
    ),
    FOREST(
        R.string.noise_forest,
        R.raw.forest,  // 需要添加实际的音频文件
        R.drawable.ic_forest
    ),
    OCEAN(
        R.string.noise_ocean,
        R.raw.ocean,  // 需要添加实际的音频文件
        R.drawable.ic_waves
    ),
    CAFE(
        R.string.noise_cafe,
        R.raw.cafe,  // 需要添加实际的音频文件
        R.drawable.ic_cafe
    ),
    FIRE(
        R.string.noise_fire,
        R.raw.fire,  // 需要添加实际的音频文件
        R.drawable.ic_fire
    );

    companion object {
        /**
         * 根据类型名称获取白噪音类型
         */
        fun fromTypeName(typeName: String?): WhiteNoiseType? {
            return typeName?.let {
                values().find { type -> type.name.equals(it, ignoreCase = true) }
            }
        }
        
        /**
         * 获取所有白噪音类型列表
         */
        fun getAllTypes(): List<WhiteNoiseType> {
            return values().toList()
        }
    }
}