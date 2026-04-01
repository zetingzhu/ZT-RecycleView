package com.example.zzt.recycleview.entity

import java.io.Serializable

/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
data class ItemData(
    var id: Int = 0,
    var title: String = "",
    var msg: String = "",
    var bgColor: Int = 0,
    var sort: Long = 0,// 用来排序v
    var header: Int = 0,// 1 列表头
    var headerIndex: Int = 0,// 列表头序号
    var expanded: Boolean = true,// header 展开/收起状态
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemData

        if (id != other.id) return false
        if (bgColor != other.bgColor) return false
        if (sort != other.sort) return false
        if (header != other.header) return false
        if (headerIndex != other.headerIndex) return false
        if (expanded != other.expanded) return false
        if (title != other.title) return false
        if (msg != other.msg) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + bgColor
        result = 31 * result + sort.hashCode()
        result = 31 * result + header
        result = 31 * result + headerIndex
        result = 31 * result + expanded.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + msg.hashCode()
        return result
    }
}
