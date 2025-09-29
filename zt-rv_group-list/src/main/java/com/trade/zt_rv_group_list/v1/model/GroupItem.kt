package com.trade.zt_rv_group_list.v1.model

/**
 * 组项数据模型
 */
data class GroupItem(
    val id: Int,
    val title: String,
    val children: MutableList<ChildItem> = mutableListOf(),
    var isExpanded: Boolean = false
)

/**
 * 子项数据模型
 */
data class ChildItem(
    val id: Int,
    val title: String,
    val description: String = ""
)

/**
 * 列表项类型枚举
 */
enum class ItemType {
    GROUP,
    CHILD
}

/**
 * 展示用的列表项基类
 */
sealed class DisplayItem(val type: ItemType) {
    data class GroupDisplayItem(
        val groupItem: GroupItem
    ) : DisplayItem(ItemType.GROUP)
    
    data class ChildDisplayItem(
        val childItem: ChildItem,
        val parentGroupId: Int
    ) : DisplayItem(ItemType.CHILD)
}