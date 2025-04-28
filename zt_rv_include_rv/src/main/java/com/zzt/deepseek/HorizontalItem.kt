package com.zzt.deepseek

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */

data class HorizontalItem(
    val id: String,
    val name: String,
    val imageResId: Int // 使用本地资源代替网络图片方便演示
)

data class VerticalItem(
    val title: String,
    val horizontalItems: List<HorizontalItem>
)