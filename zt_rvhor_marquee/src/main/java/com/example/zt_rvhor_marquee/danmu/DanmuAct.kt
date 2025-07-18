package com.example.zt_rvhor_marquee.danmu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.zt_rvhor_marquee.R
import java.util.Random

class DanmuAct : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarqueeStaggeredAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val itemsList = mutableListOf<MarqueeStaggeredItem>()
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()

    private val SCROLL_SPEED_PIXELS = 2 // 每次滚动的像素，可以调整
    private val SCROLL_DELAY_MS = 30L // 滚动间隔，可以调整 (~33fps)
    private val MAX_ITEMS_ON_SCREEN_APPROX = 50 // 大致屏幕上保留的item数量，用于清理

    private var isUserScrolling = false

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, DanmuAct::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_danmu)

        recyclerView = findViewById(R.id.rv_staggered_marquee)
        val btnAddItem: Button = findViewById(R.id.btn_add_staggered_item)

        setupRecyclerView()

        btnAddItem.setOnClickListener {
            addNewRandomItem()
            if (itemsList.size == 1 || !isAutoScrolling) {
                startAutoScroll()
            }
        }

        // 初始加载一些数据
        repeat(10) {
            addNewRandomItem(false) // 初始不立即滚动到0
        }
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(0) // 确保从最右边开始
        startAutoScroll()

        // 监听用户触摸，停止自动滚动
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(
                rv: RecyclerView,
                e: android.view.MotionEvent
            ): Boolean {
                if (e.action == android.view.MotionEvent.ACTION_DOWN) {
                    isUserScrolling = true
                    stopAutoScroll()
                } else if (e.action == android.view.MotionEvent.ACTION_UP || e.action == android.view.MotionEvent.ACTION_CANCEL) {
                    // 用户停止触摸后，可以延迟一小段时间再重新开始自动滚动
                    handler.postDelayed({
                        isUserScrolling = false
                        startAutoScroll()
                    }, 3000) // 3秒后重启
                }
                return super.onInterceptTouchEvent(rv, e)
            }
        })
    }

    private fun setupRecyclerView() {
        layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        recyclerView.layoutManager = layoutManager
        adapter = MarqueeStaggeredAdapter(itemsList)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null // 禁用动画，让滚动更像跑马灯
    }

    private fun addNewRandomItem(scrollToStart: Boolean = true) {
        val texts = listOf(
            "Staggered!",
            "横向多行",
            "跑马灯效果",
            "RecyclerView",
            "错落有致",
            "Kotlin",
            "Android Dev",
            "这是一条比较长的测试文本看看效果如何",
        )
        val newItem = MarqueeStaggeredItem(
            text = texts[random.nextInt(texts.size)] + " (${itemsList.size})",
        )

        adapter.addItem(newItem)

        if (scrollToStart) {
            recyclerView.scrollToPosition(0) // 新项目加在最前面（最右边），滚动到那里
        }

        // 清理旧项目
        if (itemsList.size > MAX_ITEMS_ON_SCREEN_APPROX + 10) { // 保留一些缓冲区
            val itemsToRemove = itemsList.size - MAX_ITEMS_ON_SCREEN_APPROX
            adapter.removeItemsFromEnd(itemsToRemove)
        }
    }

    private var isAutoScrolling = false
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (!isUserScrolling && itemsList.isNotEmpty()) {
                recyclerView.scrollBy(SCROLL_SPEED_PIXELS, 0)
                handler.postDelayed(this, SCROLL_DELAY_MS)
            } else {
                isAutoScrolling = false
            }
        }
    }

    private fun startAutoScroll() {
        if (!isAutoScrolling && itemsList.isNotEmpty()) {
            isAutoScrolling = true

            if (layoutManager.reverseLayout) {
                recyclerView.scrollBy(SCROLL_SPEED_PIXELS, 0) // 内容向左移动
            }
            handler.postDelayed(autoScrollRunnable, SCROLL_DELAY_MS)
        }
    }

    private val scrollActualRunnable = object : Runnable {
        override fun run() {
            if (!isUserScrolling && itemsList.isNotEmpty()) {
                if (layoutManager.reverseLayout) {
                    recyclerView.scrollBy(SCROLL_SPEED_PIXELS, 0)
                } else {
                    recyclerView.scrollBy(SCROLL_SPEED_PIXELS, 0)
                }
                handler.postDelayed(this, SCROLL_DELAY_MS)
            } else {
                isAutoScrolling = false
            }
        }
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        handler.removeCallbacks(scrollActualRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
    }
}

