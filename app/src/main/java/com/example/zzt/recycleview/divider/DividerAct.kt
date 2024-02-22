package com.example.zzt.recycleview.divider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.rvitemdecor.DividerDrawable
import com.example.zzt.recycleview.rvitemdecor.GridDivV4
import com.example.zzt.recycleview.rvitemdecor.RvLineHVDivider
import com.example.zzt.recycleview.rvitemdecor.UltraSpaceItemDecoration
import com.example.zzt.recycleview.util.DataListUtil

class DividerAct : AppCompatActivity() {
    var rv_list_h: RecyclerView? = null
    var rv_list_v: RecyclerView? = null
    var mAdapterV: AdapterV? = null
    var mAdapterH: AdapterV? = null

    var divColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_divider)
        divColor = ContextCompat.getColor(this@DividerAct, com.zzt.toolslib.R.color.darkkhaki)
        initView()
        initViewGV()
        initViewGH()
    }

    private fun initViewGH() {
        var rv_list_gh: RecyclerView? = findViewById(R.id.rv_list_gh)
        rv_list_gh?.apply {
            var mAdapter = AdapterH()
            mAdapter?.mList = DataListUtil.getListCount(12)
            layoutManager =
                GridLayoutManager(this@DividerAct, 2, GridLayoutManager.HORIZONTAL, false)
            //添加自定义分割线
//            addItemDecoration(RVGridHVDivider(this@DividerAct))

//            var d2 = GridDividerItemDecoration.Builder(this@DividerAct)
//                .setColor(divColor)
//                .setHorizontalSpan(30F)
//                .setVerticalSpan(30F)
//                .setShowLastLine(false)
//                .build()
//            addItemDecoration(d2)

//            var d3 = GridDiv3.Builder()
//                .setDraw(true)
//                .setColumn(2)
//                .setSingleLine(true)
//                .setmSpace(30)
//                .create(this@DividerAct)
//            addItemDecoration(d3)

//            var d4 = GridDivV4(30, true)
//            addItemDecoration(d4);

            var d5 = UltraSpaceItemDecoration.Builder()
                .dividerWidth(30, 50)
                .build()
            addItemDecoration(d5)

            adapter = mAdapter
        }
    }

    private fun initViewGV() {
        var rv_list_gv: RecyclerView? = findViewById(R.id.rv_list_gv)
        rv_list_gv?.apply {
            var mAdapterGv = AdapterV()
            mAdapterGv?.mList = DataListUtil.getListCount(12)
            layoutManager = GridLayoutManager(this@DividerAct, 2, GridLayoutManager.VERTICAL, false)
            //添加自定义分割线
//            addItemDecoration(RVGridHVDivider(this@DividerAct))

//            var d2 = GridDividerItemDecoration.Builder(this@DividerAct)
//                .setColor(divColor)
//                .setHorizontalSpan(30F)
//                .setVerticalSpan(30F)
//                .setShowLastLine(false)
//                .build()
//            addItemDecoration(d2)

//            var d3 = GridDiv3.Builder()
//                .setDraw(true)
//                .setColumn(2)
//                .setSingleLine(true)
//                .setmSpace(30)
//                .create(this@DividerAct)
//            addItemDecoration(d3)

//            var d4 = GridDivV4(30, true)
//            addItemDecoration(d4);

            adapter = mAdapterGv
        }
    }


    private fun initView() {
        rv_list_h = findViewById(R.id.rv_list_h)
        rv_list_v = findViewById(R.id.rv_list_v)
        rv_list_h?.apply {
            mAdapterH = AdapterV()
            mAdapterH?.mList = DataListUtil.getList100()
            layoutManager = LinearLayoutManager(this@DividerAct, RecyclerView.HORIZONTAL, false)
            //添加自定义分割线
            val decoration = RvLineHVDivider(
                this@DividerAct, RvLineHVDivider.HORIZONTAL
            )
            var divDraw = DividerDrawable(
                ContextCompat.getColor(context, com.zzt.toolslib.R.color.darkkhaki), 0, 30
            )
            decoration.setDrawable(divDraw)
            addItemDecoration(decoration)
            adapter = mAdapterH
        }
        rv_list_v?.apply {
            mAdapterV = AdapterV()
            mAdapterV?.mList = DataListUtil.getList100()
            layoutManager = LinearLayoutManager(this@DividerAct)

            //添加自定义分割线
            val decoration = RvLineHVDivider(
                this@DividerAct, RvLineHVDivider.VERTICAL
            )
            var divDraw = DividerDrawable(
                ContextCompat.getColor(context, com.zzt.toolslib.R.color.lightblue), 30
            )
            decoration.setDrawable(divDraw)
            addItemDecoration(decoration)
            adapter = mAdapterV
        }
    }
}