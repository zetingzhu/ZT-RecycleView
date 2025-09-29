package com.trade.zt_listscrollshor_v4.entiy

/**
 * 子项数据对象
 */
data class ChildObj(
    var title: String = "",
    var subItems: MutableList<SubItemObj> = mutableListOf()
)