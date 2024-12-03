package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.refresh.v1.OnRefreshListener
import com.example.zzt.recycleview.refresh.v1.PullToRefreshBase
import com.example.zzt.recycleview.refresh.v1.PullToRefreshNestedScrollViewV2
import com.example.zzt.recycleview.util.DataListUtil
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos


class ActRecycleViewV20 : AppCompatActivity() {

    private var pull_layout: PullToRefreshNestedScrollViewV2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v20_base )

        initView()
    }

    private fun initView() {

        pull_layout = findViewById(R.id.pull_layout)
        pull_layout?.setOnRefreshListener(object : OnRefreshListener<NestedScrollView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<NestedScrollView>?) {
                pull_layout?.postDelayed(object : Runnable {
                    override fun run() {
                        pull_layout?.onPullDownRefreshComplete()
                    }
                }, 2000L)

            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<NestedScrollView>?) {

            }
        })
        pull_layout?.isPullRefreshEnabled = true
    }


}