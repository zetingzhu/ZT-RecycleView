package com.example.zt_rvtophead

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Random

class MainActivity : AppCompatActivity() {
    var stickItemDecoration: StickyHeaderItemDecoration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 新增：初始化RecyclerView和测试数据
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var testData: MutableList<NewsItem> = mutableListOf()
        for (index in 1..20) {
            testData.add(
                NewsItem(
                    title = "新闻头条${index}",
                    imageUrl = R.drawable.th,
                    content = "这是新闻内容${index}。",
                    desc = randomStr()
                )
            )
        }
        recyclerView.adapter = NewsAdapter(testData, object : () -> Unit {
            override fun invoke() {
                Toast.makeText(this@MainActivity, "关闭", Toast.LENGTH_SHORT).show()
            }
        })
        // 添加悬浮标题装饰
        stickItemDecoration = StickyHeaderItemDecoration { position ->
            testData.getOrNull(position)?.title ?: ""
        }.apply {
            this.headerClickListener =
                object : StickyHeaderItemDecoration.OnHeaderClickListener {
                    override fun onHeaderClick(position: Int, title: String) {
                        Toast.makeText(
                            this@MainActivity,
                            "点击了Header: $title",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        stickItemDecoration?.let {
            recyclerView.addItemDecoration(it)
        }
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (stickItemDecoration?.isInHeaderArea(event.x, event.y) == true) {
                        val firstView = recyclerView.getChildAt(0)
                        val firstPosition = recyclerView.getChildAdapterPosition(firstView)
                        val title = "获取标题逻辑"
                        stickItemDecoration?.headerClickListener?.onHeaderClick(
                            firstPosition,
                            title
                        )
                        return true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

    }


    fun randomStr(): String {
        // 生成长度5-15的随机字符串
        val minLength = 20
        val maxLength = 100
        val random = Random()
        val length = random.nextInt(maxLength - minLength + 1) + minLength

        val randomString = random.ints(48, 122 + 1) // ASCII范围：'0'到'z'
            .filter { i: Int -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97) } // 过滤掉特殊字符
            .limit(length.toLong())
            .collect(
                { StringBuilder() },
                { obj: java.lang.StringBuilder, codePoint: Int -> obj.appendCodePoint(codePoint) },
                { obj: java.lang.StringBuilder, s: java.lang.StringBuilder? -> obj.append(s) })
            .toString()
        return randomString.toString()
    }
}