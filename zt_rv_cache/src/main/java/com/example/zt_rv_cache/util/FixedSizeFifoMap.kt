package com.example.zt_rv_cache.util

import java.util.LinkedHashMap
import java.util.Map

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 * 实现一个固定大小的 FIFO (先进先出) Map。
 * 当 Map 达到最大容量时，新添加的条目会挤掉最老的条目。
 *
 * @param K Map 的键类型
 * @param V Map 的值类型
 * @param capacity Map 的最大存储容量
 */
class FixedSizeFifoMap<K, V>(private val capacity: Int) :
    LinkedHashMap<K, V>(capacity, 0.75f, false) {

    // override the removeEldestEntry method to control the map's size
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        // 当 Map 的当前大小超过设定的容量时，返回 true，表示移除最老的条目
        return size > capacity
    }
}