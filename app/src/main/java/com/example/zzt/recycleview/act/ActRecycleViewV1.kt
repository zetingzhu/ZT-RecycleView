package com.example.zzt.recycleview.act

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV1
import com.example.zzt.recycleview.decor.DividerDrawable
import com.example.zzt.recycleview.decor.RecycleViewDecorationRemovePos
import com.example.zzt.recycleview.util.DataListUtil

class ActRecycleViewV1 : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    var mAdapterV1: AdapterV1? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v1)
        initView()
    }

    private fun initView() {
        rv_list = findViewById(R.id.rv_list)

        rv_list.apply {
            mAdapterV1 = AdapterV1()
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
}