package com.zzt.gemin

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */
class DataObj {
}
// 基类，用于表示 RecyclerView 中的不同类型的项
sealed class MainListItem {
    data class TitleItem(val title: String) : MainListItem() // 标题项
    data class HorizontalListItem(val items: List<HorizontalItem>) : MainListItem() // 水平列表项
    data class ProductItem(val product: Product) : MainListItem() // 单个商品项
}

// 用于水平 RecyclerView 中的数据项
data class HorizontalItem(val id: Int, val name: String, val imageUrl: String)

// 商品数据类
data class Product(val id: Int, val name: String, val price: Double, val description: String)