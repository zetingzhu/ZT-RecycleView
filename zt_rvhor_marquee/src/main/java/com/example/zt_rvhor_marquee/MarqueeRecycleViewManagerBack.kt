package com.example.zt_rvhor_marquee

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Timer
import kotlin.concurrent.timerTask

/**
 * @author: zeting
 * @date: 2025/7/9
 * 通过 RecyclerView 实现 单行 跑马灯管理工具类
 */
class MarqueeRecycleViewManagerBack {
    private var autoScrollTimer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTouched = false // 用于触摸时停止滚动
    private val scrollSpeedMs: Long = 20 // 每次滚动的时间间隔，可以调整
    private val scrollBy: Int = 1        // 每次滚动的像素距离 (垂直方向)
    var rView: RecyclerView? = null
    var orientation: Int? = null

    fun addRvManager(rv: RecyclerView?, ori: Int) {
        this.rView = rv
        rView?.let {
            orientation = ori
            it.layoutManager =
                LinearLayoutManager(it.context, ori, false)

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
            if (isTouched) {
                // 如果正在触摸，则不启动
                return
            }
            autoScrollTimer = Timer()
            autoScrollTimer?.schedule(timerTask {
                handler.post {
                    if (orientation == LinearLayoutManager.VERTICAL) {
                        rView?.scrollBy(0, scrollBy)
                    } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                        rView?.scrollBy(scrollBy, 0)
                    }
                }
            }, 0, scrollSpeedMs)
        }
    }

}