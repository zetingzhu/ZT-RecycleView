package com.example.zzt.recycleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.act.ActRecycleViewV1
import com.example.zzt.recycleview.act.ActRecycleViewV10
import com.example.zzt.recycleview.act.ActRecycleViewV11
import com.example.zzt.recycleview.act.ActRecycleViewV12
import com.example.zzt.recycleview.act.ActRecycleViewV13
import com.example.zzt.recycleview.act.ActRecycleViewV14
import com.example.zzt.recycleview.act.ActRecycleViewV15
import com.example.zzt.recycleview.act.ActRecycleViewV17
import com.example.zzt.recycleview.act.ActRecycleViewV16
import com.example.zzt.recycleview.act.ActRecycleViewV18
import com.example.zzt.recycleview.act.ActRecycleViewV19
import com.example.zzt.recycleview.act.ActRecycleViewV2
import com.example.zzt.recycleview.act.ActRecycleViewV20
import com.example.zzt.recycleview.act.ActRecycleViewV21
import com.example.zzt.recycleview.act.ActRecycleViewV22
import com.example.zzt.recycleview.act.ActRecycleViewV3
import com.example.zzt.recycleview.act.ActRecycleViewV4
import com.example.zzt.recycleview.act.ActRecycleViewV5
import com.example.zzt.recycleview.act.ActRecycleViewV7
import com.example.zzt.recycleview.act.ActRecycleViewV8
import com.example.zzt.recycleview.act.ActRvViewV6
import com.example.zzt.recycleview.divider.DividerAct
import com.example.zzt.recycleview.act.ActRecycleViewV9
import com.example.zzt.recycleview.divider.DividerActV2
import com.example.zzt.recycleview.divider.DividerActV3
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
                "普通列表", "就是进入一个普通列表", ActRecycleViewV1::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "异步更新列表", "AdapterAsync", ActRecycleViewV2::class.java
            )
        )
        mListDialog.add(StartActivityDao("普通分组列表", "", ActRecycleViewV3::class.java))
        mListDialog.add(StartActivityDao("可收缩分组列表", "", ActRecycleViewV4::class.java))
        mListDialog.add(StartActivityDao("分组悬浮列表", "", ActRecycleViewV5::class.java))
        mListDialog.add(StartActivityDao("底部自定义分割线", "", ActRvViewV6::class.java))
        mListDialog.add(StartActivityDao("横竖分割线", "", DividerAct::class.java))
        mListDialog.add(StartActivityDao("横竖分割线 2", "第二部分", DividerActV2::class.java))
        mListDialog.add(StartActivityDao("横竖分割线 3", "第三部分", DividerActV3::class.java))
        mListDialog.add(StartActivityDao("添加移除动画", "", ActRecycleViewV7::class.java))
        mListDialog.add(StartActivityDao("头部悬浮固定", "", ActRecycleViewV8::class.java))
        mListDialog.add(StartActivityDao("分组悬浮列表", "", ActRecycleViewV5::class.java))
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 PullToRefreshRecyclerView 改造 ", "", ActRecycleViewV9::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 下方 NestedScrollView ", "", ActRecycleViewV14::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 下方 SmartRefreshLayout ", "", ActRecycleViewV15::class.java
            )
        )

        mListDialog.add(StartActivityDao("PullToRefresh 下拉上拉 自定义失败 ", "", ActRecycleViewV11::class.java))

        mListDialog.add(
            StartActivityDao(
                "下拉上拉 RV 加个弹性拷贝 ", "", ActRecycleViewV12::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 RV 加个弹性转义 ", "", ActRecycleViewV13::class.java
            )
        )


        mListDialog.add(
            StartActivityDao(
                "PullToRefresh 自定义ScrollView",
                "测试发现这个视图不显示",
                ActRecycleViewV19::class.java
            )
        )

        mListDialog.add(
            StartActivityDao(
                "PullToRefresh 自定义ScrollView 20",
                "测试发现这个视图不显示",
                ActRecycleViewV20::class.java
            )
        )

        mListDialog.add(
            StartActivityDao(
                "下拉上拉 SmartRefreshLayout CoordinatorLayout ", "", ActRecycleViewV16::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 SmartRefreshLayout CoordinatorLayout 使用自定义 MyBehavior ",
                "",
                ActRecycleViewV18::class.java
            )
        )
        mListDialog.add(
            StartActivityDao(
                "下拉上拉 SmartRefreshLayout CoordinatorLayout 使用测试系统的 Behavior ",
                "",
                ActRecycleViewV21::class.java
            )
        )

        mListDialog.add(
            StartActivityDao(
                "下拉上拉 SmartRefreshLayout",
                "SmartRefreshLayout 嵌套普通 View ",
                ActRecycleViewV22::class.java
            )
        )

        mListDialog.add(
            StartActivityDao(
                "SmartRefreshLayout 自定义加载更多", "", ActRecycleViewV17::class.java
            )
        )



        mListDialog.add(
            StartActivityDao(
                "handmark PullToRefresh库修改 ", "", ActRecycleViewV10::class.java
            )
        )

        StartActivityRecyclerAdapter.setAdapterData(
            rv_main, RecyclerView.VERTICAL, mListDialog
        ) { itemView: View?, position: Int, data: StartActivityDao ->
            when (data.arouter) {
                "1" -> {
                }
            }
        }
    }
}