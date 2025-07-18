package com.example.zt_rvhor_marquee

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Timer
import kotlin.concurrent.timerTask

/**
 * @author: zeting
 * @date: 2025/7/9
 * 通过 RecyclerView 实现各种跑马灯管理工具类
 *
const val MARQUEE_TYPE_HORIZONTAL = 0 // 水平
const val MARQUEE_TYPE_VERTICAL = 1 // 垂直
const val MARQUEE_TYPE_HORIZONTAL_SINGLE = 2 // 水平单行
const val MARQUEE_TYPE_VERTICAL_SINGLE = 3 // 垂直单行
 */
class MarqueeRecycleViewManager {
    private var autoScrollTimer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTouched = false // 用于触摸时停止滚动
    private val scrollSpeedMs: Long = 20 // 每次滚动的时间间隔，可以调整
    private val scrollBy: Int = 1        // 每次滚动的像素距离 (垂直方向)
    private val scrollSingleMS = 2000L // 每次滚动之间的停顿时间
    var rView: RecyclerView? = null
    var mLayoutManager: LinearLayoutManager? = null
    var snapHelper: PagerSnapHelper? = null // 设置一次滚动一页工具
    var currentPosition: Int? = 0 // 当前滚动位置，

    @MarqueeType
    var mType: Int? = null // 保存当前滚动类型

    @IntDef(
        MarqueeType.MARQUEE_TYPE_HORIZONTAL,
        MarqueeType.MARQUEE_TYPE_VERTICAL,
        MarqueeType.MARQUEE_TYPE_VERTICAL_SINGLE,
        MarqueeType.MARQUEE_TYPE_HORIZONTAL_SINGLE
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class MarqueeType {
        companion object {
            const val MARQUEE_TYPE_HORIZONTAL = 0 // 水平
            const val MARQUEE_TYPE_VERTICAL = 1 // 垂直
            const val MARQUEE_TYPE_HORIZONTAL_SINGLE = 2 // 水平单行
            const val MARQUEE_TYPE_VERTICAL_SINGLE = 3 // 垂直单行
        }
    }

    fun addRvManager(rv: RecyclerView?, @MarqueeType ori: Int) {
        this.rView = rv
        rView?.let {
            mType = ori
            when (ori) {
                MarqueeType.MARQUEE_TYPE_HORIZONTAL -> {
                    mLayoutManager =
                        LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
                }

                MarqueeType.MARQUEE_TYPE_VERTICAL -> {
                    mLayoutManager =
                        LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
                }

                MarqueeType.MARQUEE_TYPE_HORIZONTAL_SINGLE -> {
                    mLayoutManager =
                        LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
                    // 设置每次都中间对齐
                    snapHelper = PagerSnapHelper()
                    snapHelper?.attachToRecyclerView(it)
                }

                MarqueeType.MARQUEE_TYPE_VERTICAL_SINGLE -> {
                    mLayoutManager =
                        LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
                    // 设置每次都中间对齐
                    snapHelper = PagerSnapHelper()
                    snapHelper?.attachToRecyclerView(it)
                }
            }

            it.layoutManager = mLayoutManager

            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            val snappedView = snapHelper?.findSnapView(mLayoutManager)
                            if (snappedView != null) {
                                val newPosition = mLayoutManager?.getPosition(snappedView)
                                if (currentPosition != newPosition) {
                                    currentPosition = newPosition
                                }
                                Log.d("TAG", "跑马灯 当前位置: $currentPosition")
                            }
                        }
                    }
                }
            })

            it.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isTouched = true
                            stopAutoScroll()
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            isTouched = false
                            startAutoScroll()
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                }
            })
            startAutoScroll()
        }
    }

    private fun stopAutoScroll() {
        autoScrollTimer?.cancel()
        autoScrollTimer = null

    }

    fun startAutoScroll() {
        // 延迟一点启动，避免界面未完全加载时就开始滚动
        handler.postDelayed({
            if (!isTouched) { // 如果用户没有正在触摸
                startAutoScrollReady()
            }
        }, 2000)
    }

    private fun startAutoScrollReady() {
        stopAutoScroll()
        rView?.let {
            when (mType) {
                MarqueeType.MARQUEE_TYPE_HORIZONTAL -> {
                    timeHandle(0, scrollSpeedMs) {
                        rView?.scrollBy(scrollBy, 0)
                    }
                }

                MarqueeType.MARQUEE_TYPE_VERTICAL -> {
                    timeHandle(0, scrollSpeedMs) {
                        rView?.scrollBy(0, scrollBy)
                    }
                }

                MarqueeType.MARQUEE_TYPE_HORIZONTAL_SINGLE, MarqueeType.MARQUEE_TYPE_VERTICAL_SINGLE -> {
                    timeHandle(scrollSingleMS, scrollSingleMS) {
                        currentPosition?.let {
                            rView?.smoothScrollToPosition(currentPosition!! + 1)
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun timeHandle(delayMs: Long, periodMs: Long, run: Runnable) {
        if (isTouched) {
            // 如果正在触摸，则不启动
            return
        }
        autoScrollTimer = Timer()
        autoScrollTimer?.schedule(timerTask {
            handler.post(run)
        }, delayMs, periodMs)
    }

}