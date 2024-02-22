package com.example.zzt.recycleview.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.anim.SlideInOutLeftAnimator
import com.example.zzt.recycleview.anim2.ScaleInLeftAnimator
import com.example.zzt.recycleview.anim3.OutLeftItemAnimator
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

open class ActRecycleViewV7 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterV? = null
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v1)
        initView()

        initViewParent()
    }

    open fun initViewParent() {
        rv_list.apply {
            mAdapterV1 = AdapterV()
            mAdapterV1?.mList = DataListUtil.getList100()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV7)

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
//            decoration.setHideBottom(mutableListOf(1, 2, 5))
            addItemDecoration(decoration)

            adapter = mAdapterV1
            //1
//            itemAnimator = SlideInOutLeftAnimator(this)
            //2
//            itemAnimator = ScaleInLeftAnimator()
            //3
            val outLeftItemAnimator = OutLeftItemAnimator()
            itemAnimator = outLeftItemAnimator
        }
    }

    private fun initView() {
        rv_list = findViewById(R.id.rv_list)
        rv_list_top = findViewById(R.id.rv_list_top)

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