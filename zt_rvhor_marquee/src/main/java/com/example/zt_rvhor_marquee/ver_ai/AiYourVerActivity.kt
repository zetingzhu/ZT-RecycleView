package com.example.zt_rvhor_marquee.ver_ai

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R // 确保 R 文件路径正确
import com.example.zt_rvhor_marquee.hor.MarqueeItem
import java.util.Timer
import kotlin.concurrent.timerTask

class AiYourVerActivity : AppCompatActivity() {

    private lateinit var recyclerViewVerticalMarquee: RecyclerView
    private var verMarqueeAdapter: VerMarqueeAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private val marqueeItems = mutableListOf<MarqueeItem>()

    private val scrollSpeedMs: Long = 30 // 每次滚动的时间间隔，可以调整
    private val scrollDy: Int = 1        // 每次滚动的像素距离 (垂直方向)

    private var autoScrollTimer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTouched = false // 用于触摸时停止滚动

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AiYourVerActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_marquee) // 你的 Activity 布局

        recyclerViewVerticalMarquee = findViewById(R.id.recyclerView_vertical_marquee)

        // 1. 设置 LayoutManager 为垂直方向
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewVerticalMarquee.layoutManager = layoutManager
        // 2. 准备数据
        prepareMarqueeData()
        // 3. 设置 Adapter
        verMarqueeAdapter = VerMarqueeAdapter(marqueeItems)
        recyclerViewVerticalMarquee.adapter = verMarqueeAdapter

        // 4. 如果是模拟无限滚动，可以初始滚动到一个中间位置
        verMarqueeAdapter?.let { adapter ->
            if (adapter.itemCount > 0 && adapter.itemCount / adapter.REPEAT_COUNT > 1) {
                val initialPosition = adapter.itemCount / 2
                recyclerViewVerticalMarquee.scrollToPosition(initialPosition)
            }
        }

        // 5. 添加触摸监听以暂停/恢复滚动 (可选)
        recyclerViewVerticalMarquee.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isTouched = true
                        stopAutoScroll()
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isTouched = false
                        handler.postDelayed({
                            if (!isTouched) {
                                startAutoScroll()
                            }
                        }, 2000) // 延迟一段时间再启动
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun prepareMarqueeData() {
        marqueeItems.add(MarqueeItem("垂直消息1: 这是从上往下滚动的。"))
        marqueeItems.add(MarqueeItem("垂直消息2: 内容可以很多。"))
        marqueeItems.add(MarqueeItem("垂直消息3: RecyclerView 实现的跑马灯。"))
        marqueeItems.add(MarqueeItem("垂直消息4: 非常灵活！"))
    }

    private fun startAutoScroll() {
        stopAutoScroll()
        if (verMarqueeAdapter?.itemCount == 0 || isTouched) return // 如果正在触摸，则不启动

        autoScrollTimer = Timer()
        autoScrollTimer?.schedule(timerTask {
            handler.post {
                recyclerViewVerticalMarquee.scrollBy(0, scrollDy) // dx 为 0, dy 控制垂直滚动
            }
        }, 0, scrollSpeedMs)
    }

    private fun stopAutoScroll() {
        autoScrollTimer?.cancel()
        autoScrollTimer = null
    }

    override fun onResume() {
        super.onResume()
        // 延迟一点启动，避免界面未完全加载时就开始滚动
        handler.postDelayed({
            if (!isTouched) { // 如果用户没有正在触摸
                startAutoScroll()
            }
        }, 500)
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        handler.removeCallbacksAndMessages(null) // 清理 handler
    }
}
