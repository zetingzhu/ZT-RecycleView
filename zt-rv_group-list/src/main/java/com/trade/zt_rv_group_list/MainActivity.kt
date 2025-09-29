package com.trade.zt_rv_group_list

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_rv_group_list.v1.MainActivityV1
import com.trade.zt_rv_group_list.v1.adapter.ExpandableAdapter
import com.trade.zt_rv_group_list.v2.ChildEntity
import com.trade.zt_rv_group_list.v2.ExpandableActivity
import com.trade.zt_rv_group_list.v2.ExpandableGroupEntity
import com.zzt.adapter.StartActivityRecyclerAdapter
import com.zzt.entity.StartActivityDao

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpandableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)

        val itemList: MutableList<StartActivityDao> = ArrayList<StartActivityDao>()
        itemList.add(
            StartActivityDao(
                "Ai 生成的，用 RecycleView 实现的分组",
                " ",
                MainActivityV1::class.java
            )
        )
        itemList.add(
            StartActivityDao(
                "GroupedRecyclerViewAdapter 分组列表",
                " ",
                ExpandableActivity::class.java
            )
        )

        StartActivityRecyclerAdapter.setAdapterData(
            recyclerView, RecyclerView.VERTICAL, itemList
        ) { itemView: View?, position: Int, data: StartActivityDao ->
            when (data.arouter) {
                "1" -> {
                }
            }
        }
    }

}