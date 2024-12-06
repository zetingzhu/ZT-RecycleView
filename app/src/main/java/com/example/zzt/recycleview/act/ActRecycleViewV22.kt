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
import com.example.zzt.recycleview.util.DataListUtil
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos


class ActRecycleViewV22 : AppCompatActivity() {

    var refL_coord: SmartRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v22)
        initView()

        initViewParent()
    }


    private fun initView() {
        refL_coord = findViewById(R.id.refL_coord)
        refL_coord?.setRefreshHeader(ClassicsHeader(this))
        refL_coord?.setRefreshFooter(ClassicsFooter(this))
        refL_coord?.setEnableRefresh(true)
        refL_coord?.setEnableLoadMore(true)
        refL_coord?.setOnRefreshListener { refreshlayout ->
            refreshlayout.finishRefresh(2000 /*,false*/) //传入false表示刷新失败
        }
        refL_coord?.setOnLoadMoreListener { refreshlayout ->
            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
        }


    }

    fun initViewParent() {

    }
}