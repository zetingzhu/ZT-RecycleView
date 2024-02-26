package com.example.zzt.recycleview.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.refresh.v1.OnRefreshListenerV2
import com.example.zzt.recycleview.refresh.v1.PullToRefreshBaseV2
import com.example.zzt.recycleview.refresh.v1.PullToRefreshRecyclerViewV2
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

class ActRecycleViewV11 : AppCompatActivity() {
    lateinit var pullrv_list: PullToRefreshRecyclerViewV2
    lateinit var rv_list: RecyclerView
    var adapterAsync: AdapterAsync? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v11)
        initView()

        initViewParent()
    }


    private fun initView() {
        pullrv_list = findViewById(R.id.pullrv_list)
        pullrv_list.setPullLoadEnabled(true)
        pullrv_list.setPullRefreshEnabled(true)
        pullrv_list.setScrollLoadEnabled(true)
        pullrv_list.setOnRefreshListenerV2(object : OnRefreshListenerV2<RecyclerView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBaseV2<RecyclerView>?) {
                pullrv_list.postDelayed(Runnable {
                    pullrv_list.onPullDownRefreshComplete()
                    pullrv_list.onPullUpRefreshComplete()
                }, 1000L)
            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBaseV2<RecyclerView>?) {
                pullrv_list.postDelayed(Runnable {
                    pullrv_list.onPullDownRefreshComplete()
                    pullrv_list.onPullUpRefreshComplete()
                }, 1000L)
            }
        })
        rv_list = pullrv_list.refreshableView
    }

    fun initViewParent() {
        rv_list.apply {
            adapterAsync = AdapterAsync()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV11)
            //添加自定义分割线
            val decoration = RecycleViewDecorationRemovePos(
                this.context,
                RecycleViewDecorationRemovePos.VERTICAL_TOB_BOTTOM
            )
            decoration.setDrawable(
                DividerDrawable(
                    ContextCompat.getColor(
                        context, R.color.white_60
                    ), 20
                )
            )
            addItemDecoration(decoration)

            adapter = adapterAsync
            adapterAsync?.submitList(DataListUtil.getListNum(20))
        }
    }
}