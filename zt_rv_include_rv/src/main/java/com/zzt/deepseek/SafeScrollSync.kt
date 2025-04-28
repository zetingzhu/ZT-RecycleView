package com.zzt.deepseek

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */
class SafeScrollSync {
    private val recyclerViews = mutableListOf<WeakReference<RecyclerView>>()
    private var isSyncing = false
    private val handler = Handler(Looper.getMainLooper())
    private val syncRunnable = Runnable { performSync() }
    private var pendingSyncData: Pair<Int, Int>? = null // <position, offset>

    fun registerRecyclerView(recyclerView: RecyclerView) {
        if (recyclerViews.any { it.get() == recyclerView }) return

        recyclerViews.add(WeakReference(recyclerView))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isSyncing || recyclerView.isComputingLayout) return

                val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val firstPos = lm.findFirstVisibleItemPosition()
                val firstView = lm.findViewByPosition(firstPos) ?: return
                val offset = lm.getDecoratedRight(firstView)
                pendingSyncData = firstPos to offset

                handler.removeCallbacks(syncRunnable)
                handler.post(syncRunnable)
            }
        })
    }

    private fun performSync() {
        if (isSyncing || pendingSyncData == null) return
        val (pos, offset) = pendingSyncData ?: return

        isSyncing = true
        recyclerViews.forEach { ref ->
            val rv = ref.get()?.takeUnless { it.isComputingLayout } ?: return@forEach
            try {
                (rv.layoutManager as? LinearLayoutManager)?.apply {
                    if (findFirstVisibleItemPosition() != pos) {
                        scrollToPositionWithOffset(pos + 1, offset)
                    }
                }
            } catch (e: IllegalStateException) {
                // 忽略正在计算布局的异常
                e.printStackTrace()
            }
        }
        isSyncing = false
    }

    fun clear() {
        handler.removeCallbacks(syncRunnable)
        recyclerViews.clear()
    }
}