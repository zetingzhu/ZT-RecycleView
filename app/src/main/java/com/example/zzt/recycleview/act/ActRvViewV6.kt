package com.example.zzt.recycleview.act


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.rvitemdecor.BottomViewDivider
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter

/**
 * @author: zeting
 * @date: 2023/11/7
 */
class ActRvViewV6 : AppCompatActivity() {
    val TAG = ActRecycleViewV5::class.java.simpleName

    private var mAdapterV1: AdapterV? = null
    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v1)
        initView()
        initViewParent()
    }

    private fun initView() {
        rv_list = findViewById(R.id.rv_list)
        rv_list_top = findViewById(R.id.rv_list_top)

        topListDialog = ArrayList()
        topListDialog?.add("空布局")
        topListDialog?.add("设置数据")
        topListDialog?.add("数据刷新")

        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {

                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }

    fun initViewParent() {
        rv_list.apply {
            mAdapterV1 = AdapterV()
            mAdapterV1?.mList = DataListUtil.getListCount(7)
            layoutManager = LinearLayoutManager(this@ActRvViewV6)
            val bottomViewDivider = BottomViewDivider(true)
            addItemDecoration(BottomViewDivider(true))
            bottomViewDivider.isDrawBottomView = true
            adapter = mAdapterV1
        }
    }
}