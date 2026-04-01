package com.example.zzt.recycleview.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV25
import com.example.zzt.recycleview.anim3.OutLeftItemAnimator
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter

open class ActRecycleViewV25 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    private var mAdapterV1: AdapterV25? = null
    var topListDialog: MutableList<String>? = null

    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v25)
        initView()

        initViewParent()
    }

    open fun initViewParent() {
        rv_list.apply {
            mAdapterV1 = AdapterV25()
            mAdapterV1?.mList = DataListUtil.getList100Group()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV25)

            adapter = mAdapterV1
            val outLeftItemAnimator = OutLeftItemAnimator()
            itemAnimator = outLeftItemAnimator
        }
    }

    private fun initView() {
        rv_list = findViewById(R.id.rv_list)
        rv_list_top = findViewById(R.id.rv_list_top)
    }
}