package com.example.zt_rvhor_marquee.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.MarqueeRecycleViewManager
import com.example.zt_rvhor_marquee.R
import com.example.zt_rvhor_marquee.hor.MarqueeItem

class TestMarqueeAct : AppCompatActivity() {


    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, TestMarqueeAct::class.java)
            context.startActivity(starter)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test_marquee)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        initView2()
        initView3()
    }

    private fun initView3() {
        var rv_list_v3: RecyclerView = findViewById(R.id.rv_list_v3)
        var tAdapter3 = TestSingleAdapter(mutableListOf())
        var manager = MarqueeRecycleViewManager()
        rv_list_v3?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tAdapter3
            manager?.addRvManager(
                this, MarqueeRecycleViewManager.MarqueeType.MARQUEE_TYPE_VERTICAL_SINGLE
            )
        }

        rv_list_v3?.postDelayed({
            val marqueeItems = mutableListOf<MarqueeItem>()
            marqueeItems.add(MarqueeItem("消息1: 欢迎使用跑马灯功能!"))
            marqueeItems.add(MarqueeItem("消息2: RecyclerView 横向滚动。"))
            marqueeItems.add(MarqueeItem("消息3: 这是一个示例消息。"))
            marqueeItems.add(MarqueeItem("消息4: 可以显示很多内容。"))
            tAdapter3?.setListData(marqueeItems)
            manager?.startAutoScroll()

        }, 2000)
    }

    private fun initView2() {
        var rv_list_v2: RecyclerView = findViewById(R.id.rv_list_v2)
        var tAdapter2 = TestAdapter(mutableListOf())
        var manager = MarqueeRecycleViewManager()
        rv_list_v2?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tAdapter2
            manager?.addRvManager(
                this, MarqueeRecycleViewManager.MarqueeType.MARQUEE_TYPE_VERTICAL
            )
        }

        rv_list_v2?.postDelayed({
            val marqueeItems = mutableListOf<MarqueeItem>()
            marqueeItems.add(MarqueeItem("消息1: 欢迎使用跑马灯功能!"))
            marqueeItems.add(MarqueeItem("消息2: RecyclerView 横向滚动。"))
            marqueeItems.add(MarqueeItem("消息3: 这是一个示例消息。"))
            marqueeItems.add(MarqueeItem("消息4: 可以显示很多内容。"))
            tAdapter2?.setListData(marqueeItems)
            manager?.startAutoScroll()

        }, 2000)
    }

    private fun initView() {
        var rv_list: RecyclerView = findViewById(R.id.rv_list)
        var tAdapter = TestAdapter(mutableListOf())
        var manager = MarqueeRecycleViewManager()
        rv_list?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tAdapter
            manager?.addRvManager(
                this, MarqueeRecycleViewManager.MarqueeType.MARQUEE_TYPE_HORIZONTAL
            )
        }

        rv_list?.postDelayed({
            val marqueeItems = mutableListOf<MarqueeItem>()
            marqueeItems.add(MarqueeItem("消息1: 欢迎使用跑马灯功能!"))
            marqueeItems.add(MarqueeItem("消息2: RecyclerView 横向滚动。"))
            marqueeItems.add(MarqueeItem("消息3: 这是一个示例消息。"))
            marqueeItems.add(MarqueeItem("消息4: 可以显示很多内容。"))
            tAdapter?.setListData(marqueeItems)
            manager?.startAutoScroll()

        }, 2000)
    }
}