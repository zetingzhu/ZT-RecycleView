package com.example.zzt.recycleview.act

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.behavior.MyBehavior
import com.example.zzt.recycleview.smart.SmartClassicsHeaderLayout
import com.example.zzt.recycleview.util.DataListUtil
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.listener.CoordinatorLayoutListener
import com.scwang.smart.refresh.layout.listener.MyBehaviorListener
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos


class ActRecycleViewV18 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterH? = null
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    var adapterAsync: AdapterAsync? = null

    var refL_coord: SmartRefreshLayout? = null
    var ll_top_behavior: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v18)
        initView()

        initViewParent()
    }


    private fun initView() {
        ll_top_behavior = findViewById(R.id.ll_top_behavior)
        refL_coord = findViewById(R.id.refL_coord)
        refL_coord?.setRefreshHeader(ClassicsHeader(this))
        refL_coord?.setRefreshHeader(SmartClassicsHeaderLayout(this))
        refL_coord?.setRefreshFooter(ClassicsFooter(this))
        refL_coord?.setEnableRefresh(true)
        refL_coord?.setEnableLoadMore(true)
        refL_coord?.setMyBehaviorListener(object : MyBehaviorListener {
            override fun checkCoordinatorLayout(
                content: View?, kernel: RefreshKernel?, listener: CoordinatorLayoutListener?
            ) {
                kernel?.refreshLayout?.setEnableNestedScroll(false)
                //跟单标题滑动到顶部
                val behavior: MyBehavior? =
                    (ll_top_behavior?.getLayoutParams() as CoordinatorLayout.LayoutParams?)?.behavior as MyBehavior?

                behavior?.addOnOffsetChangedListener(object : MyBehavior.OnScrollChangeListener {
                    override fun onScrollChange(
                        headerView: View?,
                        scrollingView: View?,
                        offsetDy: Int,
                        leftOffsetDy: Int
                    ) {
//                        Log.d(
//                            "SmartRefreshLayout",
//                            "底部设置  off: $offsetDy leftOffsetDy:$leftOffsetDy"
//                        )
                        listener?.onCoordinatorUpdate(offsetDy >= 0, leftOffsetDy <= 0);
                    }
                })
            }
        })
        refL_coord?.setOnRefreshListener { refreshlayout ->
            refreshlayout.finishRefresh(2000 /*,false*/) //传入false表示刷新失败
        }
        refL_coord?.setOnLoadMoreListener { refreshlayout ->
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

            layoutManager = LinearLayoutManager(this@ActRecycleViewV18)
            //添加自定义分割线
            val decoration = RecycleViewDecorationRemovePos(
                this.context, RecycleViewDecorationRemovePos.VERTICAL_TOB_BOTTOM
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
            adapterAsync?.submitList(DataListUtil.getListNum(50))
        }

    }
}