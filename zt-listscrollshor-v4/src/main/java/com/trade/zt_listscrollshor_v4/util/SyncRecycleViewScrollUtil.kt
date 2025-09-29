package com.trade.zt_listscrollshor_v4.util

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: zeting
 * @date: 2025/4/28
 * 多个RecyclerView横向滚动同步工具类
 */
class SyncRecycleViewScrollUtil {
    // 需要同步滚动的RecyclerView集合
    private val observerList = HashSet<RecyclerView>()

    // 当前第一个可见项的位置和偏移量
    private var firstPos = -1
    private var firstOffset = -1

    /**
     * 初始化RecyclerView并设置滚动同步
     * @param recyclerView 需要同步滚动的RecyclerView
     */
    fun initRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView == null) return

        // 固定RecyclerView大小以提高性能
        recyclerView.setHasFixedSize(true)

        // 如果已有滚动位置，同步到该位置
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        if (layoutManager != null && firstPos > 0 && firstOffset > 0) {
            layoutManager.scrollToPositionWithOffset(firstPos + 1, firstOffset)
        }

        // 添加到同步列表
        observerList.add(recyclerView)

        // 监听触摸事件，在开始触摸时停止所有滚动
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(
                rv: RecyclerView,
                e: MotionEvent
            ): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        observerList.forEach { it.stopScroll() }
                    }
                }
                return false
            }

            override fun onTouchEvent(
                rv: RecyclerView,
                e: MotionEvent
            ) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

        // 添加滚动监听器
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dx == 0) return  // 忽略垂直滚动

                // 获取当前滚动位置信息
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                linearLayoutManager?.let { llManager ->
                    val firstPos1 = llManager.findFirstVisibleItemPosition()
                    val firstVisibleItem = llManager.getChildAt(0)
                    if (firstVisibleItem != null) {
                        val firstRight = linearLayoutManager.getDecoratedRight(firstVisibleItem)
                        firstPos = firstPos1
                        firstOffset = firstRight

                        // 同步其他列表到相同位置
                        observerList.forEach { rv ->
                            if (rv != recyclerView) {
                                (rv.layoutManager as? LinearLayoutManager)?.let { manager ->
                                    manager.scrollToPositionWithOffset(firstPos1 + 1, firstRight)
                                }
                            }
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 在滚动停止时确保所有列表位置同步
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    firstPos >= 0 && firstOffset >= 0
                ) {
                    observerList.forEach { rv ->
                        if (rv != recyclerView) {
                            (rv.layoutManager as? LinearLayoutManager)?.let { manager ->
                                manager.scrollToPositionWithOffset(firstPos + 1, firstOffset)
                            }
                        }
                    }
                }
            }
        })
    }
}
