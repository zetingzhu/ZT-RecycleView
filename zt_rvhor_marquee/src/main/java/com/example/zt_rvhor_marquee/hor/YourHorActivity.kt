package com.example.zt_rvhor_marquee.hor

import android.content.Context
import android.content.Intent


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R
import java.util.Timer
import kotlin.concurrent.timerTask

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 */
class YourHorActivity : AppCompatActivity() { // 或者 Fragment

    private lateinit var recyclerViewMarquee: RecyclerView
    private lateinit var marqueeAdapter: MarqueeAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val marqueeItems = mutableListOf<MarqueeItem>()

    private val scrollSpeedMs: Long = 20 // 每次滚动的时间间隔，越小越快
    private val scrollDx: Int = 1        // 每次滚动的像素距离，控制平滑度

    private var autoScrollTimer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, YourHorActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your) // 你的 Activity 布局，其中包含 RecyclerView

        recyclerViewMarquee = findViewById(R.id.recyclerView_marquee) // 你的 RecyclerView ID

        // 1. 设置 LayoutManager
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewMarquee.layoutManager = layoutManager

        // 2. 准备数据
        prepareMarqueeData() // 加载你的跑马灯数据

        // 3. 设置 Adapter
        marqueeAdapter = MarqueeAdapter(marqueeItems)
        recyclerViewMarquee.adapter = marqueeAdapter

        // 4. 如果是模拟无限滚动，可以初始滚动到一个中间位置
        if (marqueeAdapter.itemCount > 0 && marqueeAdapter.itemCount / marqueeAdapter.REPEAT_COUNT > 1) { // 确保有足够多的 item
            val initialPosition = marqueeAdapter.itemCount / 2
            recyclerViewMarquee.scrollToPosition(initialPosition)
        }
    }

    private fun prepareMarqueeData() {
        // 示例数据
        marqueeItems.add(MarqueeItem("消息1: 欢迎使用跑马灯功能!"))
        marqueeItems.add(MarqueeItem("消息2: RecyclerView 横向滚动。"))
        marqueeItems.add(MarqueeItem("消息3: 这是一个示例消息。"))
        marqueeItems.add(MarqueeItem("消息4: 可以显示很多内容。"))
    }

    // 5. 实现自动滚动
    private fun startAutoScroll() {
        stopAutoScroll() // 先停止之前的，防止重复启动
        if (marqueeAdapter.itemCount == 0) return

        autoScrollTimer = Timer()
        autoScrollTimer?.schedule(timerTask {
            handler.post {
                recyclerViewMarquee.scrollBy(scrollDx, 0)
            }
        }, 0, scrollSpeedMs)
    }

    private fun stopAutoScroll() {
        autoScrollTimer?.cancel()
        autoScrollTimer = null
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll() // Activity 可见时开始滚动
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll() // Activity 不可见时停止滚动，节省资源
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll() // 确保销毁时停止
    }
}
