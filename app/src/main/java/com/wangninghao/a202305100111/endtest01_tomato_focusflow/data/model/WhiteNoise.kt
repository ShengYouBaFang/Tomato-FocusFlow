package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

/**
 * 白噪音数据模型
 * 包含白噪音的显示信息和资源引用
 */
data class WhiteNoise(
    /** 唯一标识ID */
    val id: String,

    /** 显示名称 */
    val name: String,

    /** 描述 */
    val description: String,

    /** 图标资源ID */
    @DrawableRes
    val iconRes: Int,

    /** 音频资源ID */
    @RawRes
    val audioRes: Int
)
