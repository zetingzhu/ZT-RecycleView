package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV8
import com.example.zzt.recycleview.anim3.OutLeftItemAnimator
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.groupedadapter.widget.RVStickyHeaderLayout
import com.example.zzt.recycleview.hold.BaseRecyclerViewHolder
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter

open class ActRecycleViewV8 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterV8? = null
    var topListDialog: MutableList<String>? = null
    var sticky_layout: RVStickyHeaderLayout<BaseRecyclerViewHolder>? = null

    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v8)
        initView()

        initViewParent()
    }

    open fun initViewParent() {
        rv_list.apply {
            mAdapterV1 = AdapterV8()
            mAdapterV1?.mList = DataListUtil.getList100()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV8)

            adapter = mAdapterV1
            val outLeftItemAnimator = OutLeftItemAnimator()
            itemAnimator = outLeftItemAnimator
        }
    }

    private fun initView() {
        rv_list = findViewById(R.id.rv_list)
        rv_list_top = findViewById(R.id.rv_list_top)
        sticky_layout = findViewById(R.id.sticky_layout)

        topListDialog = ArrayList()
        topListDialog?.add("添加")
        topListDialog?.add("移除")

        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {
                    0 -> {
                        val itemData = ItemData(-1, "title:添加", "msg:添加")
                        mAdapterV1?.addOneData(itemData)
                        rv_list?.scrollToPosition(0)
                    }

                    1 -> {
                        mAdapterV1?.removeOneData(0)
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }
}