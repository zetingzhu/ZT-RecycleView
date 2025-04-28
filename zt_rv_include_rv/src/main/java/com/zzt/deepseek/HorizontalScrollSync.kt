package com.zzt.deepseek

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */


class HorizontalScrollSync {
    private val recyclerViews = mutableListOf<RecyclerView>()
    private var isSyncing = false

    fun registerRecyclerView(recyclerView: RecyclerView) {
        if (recyclerViews.contains(recyclerView)) return

        recyclerViews.add(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isSyncing) return
                syncScroll(recyclerView)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    syncScroll(recyclerView)
                }
            }
        })
    }

    private fun syncScroll(source: RecyclerView) {
        isSyncing = true
        val sourceLayoutManager = source.layoutManager as? LinearLayoutManager ?: return
        var firstPos = sourceLayoutManager.findFirstVisibleItemPosition()
        val firstView = sourceLayoutManager.findViewByPosition(firstPos) ?: return
        val offset = sourceLayoutManager.getDecoratedRight(firstView)

        Log.w(
            "ZZT",
            "滑动：firstPos：" + firstPos + " offset:" + offset + " count:" + recyclerViews.size
        )
        recyclerViews.forEach { rv ->
            if (rv != source) {
                val layoutManager = rv.layoutManager as LinearLayoutManager?
                if (layoutManager != null) {
                    Log.d("ZZT", "滑动：firstPos：$firstPos offset:$offset")

                    //通过当前显示item的位置和偏移量的位置来置顶recycleview 也就是同步其它item的移动距离
                    layoutManager.scrollToPositionWithOffset(firstPos + 1, offset)
                }
            }
        }
        isSyncing = false
    }

    fun clear() {
        recyclerViews.clear()
    }
}