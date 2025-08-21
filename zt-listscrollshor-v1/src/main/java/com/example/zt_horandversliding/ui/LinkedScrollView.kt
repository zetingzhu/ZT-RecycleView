package com.example.zt_horandversliding.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * 自定义的横向滚动视图，用于实现多个滚动视图的联动
 */
class LinkedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    // 关联的滚动视图列表
    private val linkedScrollViews = mutableListOf<LinkedScrollView>()

    // 是否正在处理滚动事件
    private var isScrolling = false

    /**
     * 添加关联的滚动视图
     */
    fun addLinkedScrollView(scrollView: LinkedScrollView) {
        if (!linkedScrollViews.contains(scrollView)) {
            linkedScrollViews.add(scrollView)
        }
    }

    /**
     * 移除关联的滚动视图
     */
    fun removeLinkedScrollView(scrollView: LinkedScrollView) {
        linkedScrollViews.remove(scrollView)
    }

    /**
     * 清除所有关联的滚动视图
     */
    fun clearLinkedScrollViews() {
        linkedScrollViews.clear()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        // 如果不是正在滚动中，则同步其他关联的滚动视图
        if (!isScrolling) {
            isScrolling = true
            // 同步所有关联的滚动视图
            linkedScrollViews.forEach { it.scrollTo(l, t) }
            isScrolling = false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 当触摸事件结束时，重置滚动状态
        if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
            isScrolling = false
        }
        return super.onTouchEvent(ev)
    }
}