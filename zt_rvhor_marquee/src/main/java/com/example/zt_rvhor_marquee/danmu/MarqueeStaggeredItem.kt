package com.example.zt_rvhor_marquee.danmu

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
import android.graphics.Color
import java.util.UUID

data class MarqueeStaggeredItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val textSizeSp: Float = (14 + random.nextInt(8)).toFloat(), // 随机文字大小 14-21sp
) {
    companion object {
        private val random = java.util.Random()
    }
}