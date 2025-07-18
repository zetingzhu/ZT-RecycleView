package com.example.zt_rvhor_marquee.vers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
class VerSingleAct : AppCompatActivity() {
    companion object {
        private const val TAG = "MarqueeActivity"
        private const val SCROLL_DELAY_MS = 2000L // 每次滚动之间的停顿时间
        private const val SCROLL_ANIMATION_DURATION_MS = 500L // 滚动动画的时长

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, VerSingleAct::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var recyclerViewMarquee: RecyclerView
    private lateinit var marqueeAdapter: MarqueeAdapter
    private val marqueeData = mutableListOf<String>()
    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null

    private var currentPosition = 0
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_single)

        recyclerViewMarquee = findViewById(R.id.recyclerView_marquee)

        marqueeData.addAll(
            listOf(
                "向上滚动消息 1：这是第一条消息，它会向上滚动。",
                "向上滚动消息 2：第二条消息也来了。",
                "向上滚动消息 3：内容可以有很多很多很多。",
                "向上滚动消息 4：RecyclerView 实现跑马灯。",
                "向上滚动消息 5：最后一条示例消息。"
            )
        )

        marqueeAdapter = MarqueeAdapter(marqueeData)
        val layoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return super.canScrollVertically()
            }
        }
        recyclerViewMarquee.layoutManager = layoutManager
        recyclerViewMarquee.adapter = marqueeAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewMarquee)

        recyclerViewMarquee.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isUserScrolling = true
                        stopAutoScroll()
                    }

                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (isUserScrolling) {
                            isUserScrolling = false
                            scrollHandler.postDelayed({
                                if (!isUserScrolling) startAutoScroll()
                            }, SCROLL_DELAY_MS * 2)
                        }
                    }
                }
            }
        })

        recyclerViewMarquee.post { startAutoScroll() }
    }

    private fun startAutoScroll() {
        if (marqueeAdapter.itemCount == 0) {
            recyclerViewMarquee.postDelayed({ startAutoScroll() }, 100)
            return
        }

        scrollRunnable?.let { scrollHandler.removeCallbacks(it) }

        scrollRunnable = Runnable {
            if (isUserScrolling || marqueeAdapter.itemCount == 0) {
                return@Runnable
            }

            currentPosition++
            recyclerViewMarquee.smoothScrollToPosition(currentPosition)

            scrollRunnable?.let {
                scrollHandler.postDelayed(it, SCROLL_ANIMATION_DURATION_MS + SCROLL_DELAY_MS)
            }
        }.also {
            scrollHandler.postDelayed(it, SCROLL_DELAY_MS) // 首次启动
        }
    }

    private fun stopAutoScroll() {
        scrollRunnable?.let { scrollHandler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        if (!isUserScrolling) {
            if (::marqueeAdapter.isInitialized) {
                startAutoScroll()
            } else {
                recyclerViewMarquee.post { startAutoScroll() }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        scrollRunnable = null // 避免内存泄漏
    }
}

