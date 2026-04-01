package com.example.zzt.recycleview.groupedadapter.sticky

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 分组列表通用基类。
 * 内置分组展开/收起逻辑 + IStickyHeaderProvider 吸顶支持。
 * 子类只需实现：
 *   - isHeader / isExpanded / setExpanded（数据层判断）
 *   - onCreateViewHolder / onBindViewHolder（UI 绑定）
 *   - onBindViewHolderStickyHead（吸顶头绑定）
 *
 * @param T  列表数据项类型
 * @param VH ViewHolder 类型
 */
abstract class BaseGroupListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback), IStickyHeaderProvider<VH> {

    // 完整的扁平数据（包含所有 header 和 content）
    private val allItems = mutableListOf<T>()

    // ==================== 子类必须实现的抽象方法 ====================

    /**
     * 判断该条数据是否为分组 Header
     */
    abstract fun isHeader(item: T): Boolean

    /**
     * 获取 Header 的展开状态
     */
    abstract fun isExpanded(item: T): Boolean

    /**
     * 设置 Header 的展开状态
     */
    abstract fun setExpanded(item: T, expanded: Boolean)

    // ==================== 数据操作 ====================

    /**
     * 获取完整数据列表（包括收起的子项）
     */
    fun getAllItems(): List<T> = allItems

    /**
     * 设置完整数据
     */
    fun setData(data: List<T>) {
        allItems.clear()
        allItems.addAll(data)
        submitVisibleList()
    }

    /**
     * 切换指定 header 的展开/收起
     * @param headerPosition header 在 allItems 中的位置
     */
    fun toggleGroup(headerPosition: Int) {
        if (headerPosition in allItems.indices && isHeader(allItems[headerPosition])) {
            val item = allItems[headerPosition]
            setExpanded(item, !isExpanded(item))
            submitVisibleList()
        }
    }

    /**
     * 全部展开
     */
    fun expandAll() {
        allItems.filter { isHeader(it) }.forEach { setExpanded(it, true) }
        submitVisibleList()
    }

    /**
     * 全部收起
     */
    fun collapseAll() {
        allItems.filter { isHeader(it) }.forEach { setExpanded(it, false) }
        submitVisibleList()
    }

    /**
     * 根据 header 的 expanded 状态，过滤出当前可见的 item 列表提交给 ListAdapter
     */
    private fun submitVisibleList() {
        val visibleItems = mutableListOf<T>()
        var currentExpanded = true

        for (item in allItems) {
            if (isHeader(item)) {
                visibleItems.add(item)
                currentExpanded = isExpanded(item)
            } else {
                if (currentExpanded) {
                    visibleItems.add(item)
                }
            }
        }
        submitList(visibleItems)
    }

    /**
     * 查找 item 在 allItems 中的位置（用于 toggleGroup）
     */
    fun findPositionInAll(item: T): Int = allItems.indexOf(item)

    // ==================== IStickyHeaderProvider 默认实现 ====================

    override fun getStickyHeaderPosition(pos: Int): Int {
        for (i in pos downTo 0) {
            if (i < itemCount && isHeader(getItem(i))) {
                return i
            }
        }
        return -1
    }

    override fun getNextStickyHeaderPosition(currentHeaderPos: Int): Int {
        for (i in (currentHeaderPos + 1) until itemCount) {
            if (isHeader(getItem(i))) {
                return i
            }
        }
        return -1
    }
}
