package com.example.zzt.recycleview.act

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.util.DataListUtil
import com.example.zzt.recycleview.widget.TextViewRoundClick
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

open class ActRecycleViewV1 : AppCompatActivity() {
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

            layoutManager = LinearLayoutManager(this@ActRecycleViewV1)

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
            decoration.setHideBottom(mutableListOf(1, 2, 5))
            addItemDecoration(decoration)

            adapter = mAdapterV1
        }
    }

    private fun initView() {

        findViewById<TextViewRoundClick>(R.id.btn_sss).setOnClickListener {
            Log.d("TextViewRoundClick", "建仓事件触发了")
        }

        rv_list = findViewById(R.id.rv_list)
        rv_list_top = findViewById(R.id.rv_list_top)

        topListDialog = ArrayList()
        topListDialog?.add("初始化")
        topListDialog?.add("替换数据")
        topListDialog?.add("随机数")
        topListDialog?.add("随机颜色")

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
}