package com.example.zt_rvhor_marquee.pubu

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
data class Poem(
    val title: String,
    val author: String,
    val contentLines: List<String>, // 将诗句按行存储
    val backgroundColor: Int? = null // 可选：为每个卡片设置不同的背景色
)
