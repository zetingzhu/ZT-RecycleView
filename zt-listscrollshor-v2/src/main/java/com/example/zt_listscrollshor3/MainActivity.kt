package com.example.zt_listscrollshor3

import android.os.Bundle
import android.widget.HorizontalScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_listscrollshor2.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取表头的HorizontalScrollView
        val headerScrollView = findViewById<HorizontalScrollView>(R.id.headerScrollView)

        // 初始化RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 准备数据
        val dataList = mutableListOf<List<String>>()
        for (i in 1..20) {
            dataList.add(
                listOf(
                    "项目 $i",
                    "数据 ${i}A",
                    "数据 ${i}B",
                    "数据 ${i}C 来一个长数据，看看能不能同步",
                    "数据 ${i}D"
                )
            )
        }

        // 设置适配器
        val adapter = MyAdapter(dataList, headerScrollView)
        recyclerView.adapter = adapter

        // 监听表头滚动，同步内容区域
        headerScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            adapter.syncScroll(scrollX)
        }
    }
}