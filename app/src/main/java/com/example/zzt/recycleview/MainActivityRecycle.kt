package com.example.zzt.recycleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.act.ActRecycleViewV1
import com.example.zzt.recycleview.act.ActRecycleViewV2
import com.example.zzt.recycleview.act.ActRecycleViewV3
import com.example.zzt.recycleview.act.ActRecycleViewV4
import com.example.zzt.recycleview.act.ActRecycleViewV5
import com.zzt.adapter.StartActivityRecyclerAdapter
import com.zzt.entity.StartActivityDao

class MainActivityRecycle : AppCompatActivity() {
    lateinit var rv_main: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle)
        initView()
    }

    private fun initView() {
        rv_main = findViewById(R.id.rv_main)

        val mListDialog: MutableList<StartActivityDao> = ArrayList()
        mListDialog.add(
            StartActivityDao(
                "普通列表",
                "就是进入一个普通列表",
                ActRecycleViewV1::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "异步更新列表",
                "AdapterAsync",
                ActRecycleViewV2::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "普通分组列表",
                "",
                ActRecycleViewV3::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "可收缩分组列表",
                "",
                ActRecycleViewV4::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "分组悬浮列表",
                "",
                ActRecycleViewV5::class.java
            )
        )

        StartActivityRecyclerAdapter.setAdapterData(
            rv_main,
            RecyclerView.VERTICAL,
            mListDialog
        ) { itemView: View?, position: Int, data: StartActivityDao ->
            when (data.arouter) {
                "1" -> {
                }
            }
        }
    }
}