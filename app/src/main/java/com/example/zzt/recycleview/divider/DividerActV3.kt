package com.example.zzt.recycleview.divider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinuscxj.itemdecoration.ShaderItemDecoration
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterH
import com.example.zzt.recycleview.adapter.AdapterV
import com.example.zzt.recycleview.rvitemdecor.UltraSpaceItemDecoration
import com.example.zzt.recycleview.util.DataListUtil
import com.github.magiepooh.recycleritemdecoration.ItemDecorations


class DividerActV3 : AppCompatActivity() {
    var rv_list_h: RecyclerView? = null
    var rv_list_v: RecyclerView? = null
    var mAdapterV: AdapterV? = null
    var mAdapterH: AdapterV? = null

    var divColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_divider)
        divColor = ContextCompat.getColor(this@DividerActV3, com.zzt.toolslib.R.color.darkkhaki)
        initRvLinH()
        initRvLinV()
        initViewGV()
        initViewGH()
    }

    private fun initViewGH() {
        var rv_list_gh: RecyclerView? = findViewById(R.id.rv_list_gh)
        rv_list_gh?.apply {
            var mAdapter = AdapterH()
            mAdapter?.mList = DataListUtil.getListCount(12)
            layoutManager =
                GridLayoutManager(this@DividerActV3, 2, GridLayoutManager.HORIZONTAL, false)
            //添加自定义分割线
            var d5 = UltraSpaceItemDecoration.Builder().dividerWidth(30, 50).build()
            addItemDecoration(d5)

            adapter = mAdapter
        }
    }

    private fun initViewGV() {
        var rv_list_gv: RecyclerView? = findViewById(R.id.rv_list_gv)
        rv_list_gv?.apply {
            var mAdapterGv = AdapterV()
            mAdapterGv?.mList = DataListUtil.getListCount(12)
            layoutManager =
                GridLayoutManager(this@DividerActV3, 2, GridLayoutManager.VERTICAL, false)
            //添加自定义分割线

            adapter = mAdapterGv
        }
    }

    fun initRvLinH() {
        rv_list_h = findViewById(R.id.rv_list_h)
        rv_list_h?.apply {
            mAdapterH = AdapterV()
            mAdapterH?.mList = DataListUtil.getList100()
            layoutManager = LinearLayoutManager(this@DividerActV3, RecyclerView.HORIZONTAL, false)
            //添加自定义分割线

            adapter = mAdapterH
        }
    }

    private fun initRvLinV() {
        rv_list_v = findViewById(R.id.rv_list_v)
        rv_list_v?.apply {
            mAdapterV = AdapterV()
            mAdapterV?.mList = DataListUtil.getListCount(20)
            layoutManager = LinearLayoutManager(this@DividerActV3)

            //添加自定义分割线


            adapter = mAdapterV
        }
    }
}