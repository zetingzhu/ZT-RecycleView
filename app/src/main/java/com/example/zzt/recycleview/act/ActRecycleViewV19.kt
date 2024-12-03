package com.example.zzt.recycleview.act

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.refresh.v1.OnRefreshListener
import com.example.zzt.recycleview.refresh.v1.PullToRefreshBase
import com.example.zzt.recycleview.refresh.v1.PullToRefreshNestedScrollViewV2


class ActRecycleViewV19 : AppCompatActivity() {

    private var pull_layout_aa: PullToRefreshNestedScrollViewV2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v19)

        initView()
    }

    private fun initView() {

        pull_layout_aa = findViewById(R.id.pull_layout_aa)

        pull_layout_aa?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d("Log", "ScrollChangeListener setOnScrollChangeListener >> $scrollY")
        }

        pull_layout_aa?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->

            Log.d("Log", "ScrollChangeListener addOnLayoutChangeListener >> $top")
        }



        pull_layout_aa?.setOnRefreshListener(object : OnRefreshListener<NestedScrollView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<NestedScrollView>?) {
                pull_layout_aa?.postDelayed(object : Runnable {
                    override fun run() {
                        pull_layout_aa?.onPullDownRefreshComplete()
                    }
                }, 2000L)

            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<NestedScrollView>?) {

            }
        })
        pull_layout_aa?.isPullRefreshEnabled = true
    }


}