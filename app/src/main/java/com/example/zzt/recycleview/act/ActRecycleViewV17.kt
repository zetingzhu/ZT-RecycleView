package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.Adapter15
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.more.LoadMoreWrapper
import com.example.zzt.recycleview.more.OnLoadMoreScrollListener
import com.example.zzt.recycleview.util.DataListUtil
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import java.util.Timer
import java.util.TimerTask


class ActRecycleViewV17 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterH? = null
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    var adapterAsync: Adapter15? = null

    private var mLoadMoreWrapper: LoadMoreWrapper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v15)
        initView()

        initViewParent()
    }


    private fun initView() {
        val refreshLayout = findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout.setRefreshHeader(ClassicsHeader(this))
        refreshLayout.setRefreshFooter(ClassicsFooter(this))
        refreshLayout.setEnableLoadMore(false)
        refreshLayout.setOnRefreshListener { refreshlayout ->
            refreshlayout.finishRefresh(2000 /*,false*/) //传入false表示刷新失败
        }
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
        }
        rv_list_top = findViewById(R.id.rv_list_top)
        rv_list = findViewById(R.id.rv_list)

        topListDialog = ArrayList()
        topListDialog?.add("初始化")
        topListDialog?.add("替换数据")
        topListDialog?.add("随机数")
        topListDialog?.add("随机颜色")
        topListDialog?.add(4, "空数据")
        topListDialog?.add(5, "随机 sort")
        topListDialog?.add(6, "排序 sort 1")
        topListDialog?.add(7, "排序 sort 2")
        topListDialog?.add(8, "排序 sort 3")

        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {
                    0 -> {
                        mAdapterV1?.setListData(DataListUtil.getList100(), rv_list)
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }

    fun initViewParent() {
        rv_list.apply {
            adapterAsync = Adapter15()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV17)
            mLoadMoreWrapper = LoadMoreWrapper(adapterAsync)

            adapter = mLoadMoreWrapper
            adapterAsync?.setListData(DataListUtil.getListCount(30), rv_list)

            this.addOnScrollListener(object : OnLoadMoreScrollListener() {
                override fun onLoadMore() {
                    mLoadMoreWrapper?.let { lmw ->
                        lmw.setLoadStateNotify(lmw.LOADING);
                        val adSize = adapterAsync?.getItemCount() ?: 0
                        if (adSize < 100) {
                            // Simulate get network data，delay 1s
                            Timer().schedule(object : TimerTask() {
                                override fun run() {
                                    runOnUiThread {
                                        adapterAsync?.setListData(
                                            DataListUtil.getListNum(adSize + 30),
                                            rv_list
                                        )
                                        lmw.setLoadStateNotify(lmw.LOADING_COMPLETE);
                                    }
                                }
                            }, 1000)
                        } else {
                            lmw.setLoadStateNotify(lmw.LOADING_END);
                        }
                    }
                }
            })
        }

    }
}