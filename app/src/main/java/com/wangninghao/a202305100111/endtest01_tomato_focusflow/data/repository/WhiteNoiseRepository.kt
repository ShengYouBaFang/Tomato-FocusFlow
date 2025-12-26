package com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.repository

import com.wangninghao.a202305100111.endtest01_tomato_focusflow.R
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise

/**
 * 白噪音仓库
 * 管理白噪音数据，提供可用的白噪音列表
 */
class WhiteNoiseRepository {

    /**
     * 获取所有可用的白噪音列表
     */
    fun getAllWhiteNoises(): List<WhiteNoise> {
        return listOf(
            WhiteNoise(
                id = "forest",
                name = "林涛",
                description = "树林中的风声，自然放松",
                iconRes = R.drawable.forest,
                audioRes = R.raw.forest
            ),
            WhiteNoise(
                id = "rain",
                name = "雨声",
                description = "细雨滴答，舒缓心情",
                iconRes = R.drawable.rain,
                audioRes = R.raw.rain
            ),
            WhiteNoise(
                id = "wave",
                name = "海浪",
                description = "海浪拍打，平静安宁",
                iconRes = R.drawable.wave,
                audioRes = R.raw.wave
            ),
            WhiteNoise(
                id = "night",
                name = "夏夜",
                description = "夏夜虫鸣，宁静美好",
                iconRes = R.drawable.night,
                audioRes = R.raw.night
            ),
            WhiteNoise(
                id = "fire",
                name = "篝火",
                description = "篝火噼啪，温暖舒适",
                iconRes = R.drawable.fire,
                audioRes = R.raw.fire
            )
        )
    }

    /**
     * 根据ID获取白噪音
     */
    fun getWhiteNoiseById(id: String): WhiteNoise? {
        return getAllWhiteNoises().find { it.id == id }
    }

    /**
     * 获取默认白噪音（雨声）
     */
    fun getDefaultWhiteNoise(): WhiteNoise {
        return getAllWhiteNoises()[1] // 默认返回雨声
    }
}
