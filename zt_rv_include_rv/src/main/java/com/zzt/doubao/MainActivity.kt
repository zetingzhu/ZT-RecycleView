package com.zzt.doubao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.doubao_activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // 模拟数据
        val verticalData = listOf(
            listOf("横向数据 1 - 项目 1", "横向数据 1 - 项目 2", "横向数据 1 - 项目 3"),
            listOf("横向数据 2 - 项目 1", "横向数据 2 - 项目 2", "横向数据 2 - 项目 3"),
            listOf("横向数据 3 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 4 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 5 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 6 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 7 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 8 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 9 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 10 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 11 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 12 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 13 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 14 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 15 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 16 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 17 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 18 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
            listOf("横向数据 19 - 项目 1", "横向数据 3 - 项目 2", "横向数据 3 - 项目 3"),
        )

        val verticalAdapter = VerticalAdapter(verticalData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = verticalAdapter
    }
}    