package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.adapter.AdapterV1
import com.example.zzt.recycleview.util.DataListUtil
import com.handmark.pulltorefresh.library.PullToRefreshBase
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

class ActRecycleViewV10 : AppCompatActivity() {
    lateinit var pullrv_list: PullToRefreshRecyclerView
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterV1? = null
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
        pullrv_list.mode = PullToRefreshBase.Mode.BOTH
        pullrv_list.setHasPullUpFriction(true)
        pullrv_list.setHasPullDownFriction(true)
        pullrv_list.setOnRefreshListener(object :
            PullToRefreshBase.OnRefreshListener2<RecyclerView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
                pullrv_list.onRefreshComplete()
            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
                pullrv_list.onRefreshComplete()
            }
        })

//            PullToRefreshBase.OnRefreshListener<RecyclerView> {
//            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
////                BaseInterface.setPullFormartRefreshTime(pullrv_list)
//                pullrv_list.onPullDownRefreshComplete()
//                pullrv_list.onPullUpRefreshComplete()
//            }
//
//            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
////                BaseInterface.setPullFormartRefreshTime(pullrv_list)
//                pullrv_list.onPullDownRefreshComplete()
//                pullrv_list.onPullUpRefreshComplete()
//            }
//        })
        rv_list = pullrv_list.refreshableView

        rv_list_top = findViewById(R.id.rv_list_top)

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

                    1 -> {
                        mAdapterV1?.setListData(DataListUtil.getList100EvenNumber(), rv_list)
                    }

                    2 -> {
                        mAdapterV1?.setListData(DataListUtil.getList100Random(), rv_list)
                    }

                    3 -> {
                        mAdapterV1?.setListData(DataListUtil.getList100RandomBG(), rv_list)
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }

    fun initViewParent() {
        rv_list.apply {
            adapterAsync = AdapterAsync()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV10)
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



        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {
                    0 -> {
                        adapterAsync?.submitList(DataListUtil.getList100())
                    }

                    1 -> {
                        adapterAsync?.submitList(DataListUtil.getList100EvenNumber())
                    }

                    2 -> {
                        adapterAsync?.submitList(DataListUtil.getList100Random())
                    }

                    3 -> {
                        adapterAsync?.submitList(DataListUtil.getList100RandomBG())
                    }

                    4 -> {
                        adapterAsync?.submitList(mutableListOf())
                    }

                    5 -> {
                        adapterAsync?.submitList(DataListUtil.getList100Sort())
                    }

                    6 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV1(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        )
                    }

                    7 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV2(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        ) { rv_list.scrollToPosition(0) }
                    }

                    8 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV3(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        )
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }
}