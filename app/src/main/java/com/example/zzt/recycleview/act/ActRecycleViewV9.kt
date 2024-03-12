package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.refresh.v1.OnRefreshListener
import com.example.zzt.recycleview.refresh.v1.PullToRefreshBase
import com.example.zzt.recycleview.refresh.v1.PullToRefreshRecyclerView
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

class ActRecycleViewV9 : AppCompatActivity() {
    lateinit var pullrv_list: PullToRefreshRecyclerView
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterH? = null
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    var adapterAsync: AdapterAsync? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v9)
        initView()

        initViewParent()
    }


    private fun initView() {
        pullrv_list = findViewById(R.id.pullrv_list)
        pullrv_list.setPullLoadEnabled(true)
        pullrv_list.setPullRefreshEnabled(true)
        pullrv_list.setScrollLoadEnabled(true)
        pullrv_list.setOnRefreshListener(object :
            OnRefreshListener<RecyclerView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
                pullrv_list.postDelayed(Runnable {
                    pullrv_list.onPullDownRefreshComplete()
                    pullrv_list.onPullUpRefreshComplete()
                }, 1000L)
            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
                pullrv_list.postDelayed(Runnable {
                    pullrv_list.onPullDownRefreshComplete()
                    pullrv_list.onPullUpRefreshComplete()
                }, 1000L)
            }
        })
        rv_list = pullrv_list.refreshableView

        rv_list_top = findViewById(R.id.rv_list_top)

        topListDialog = ArrayList()
        topListDialog?.add(0,"设置可以下拉")
        topListDialog?.add(1,"设置不能下拉")

        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {
                    0 -> {
                        pullrv_list.setPullRefreshEnabled(true)
                    }

                    1 -> {
                        pullrv_list.setPullRefreshEnabled(false)
                    }

                    2 -> {
                    }

                    3 -> {
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }

    fun initViewParent() {
        rv_list.apply {
            adapterAsync = AdapterAsync()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV9)
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
            adapterAsync?.submitList(DataListUtil.getListNum(30))
        }
    }
}