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
    var sort: Long = 0 // 用来排序
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemData) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (msg != other.msg) return false
        if (bgColor != other.bgColor) return false
        if (sort != other.sort) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + msg.hashCode()
        result = 31 * result + bgColor
        result = 31 * result + sort.hashCode()
        return result
    }
}
