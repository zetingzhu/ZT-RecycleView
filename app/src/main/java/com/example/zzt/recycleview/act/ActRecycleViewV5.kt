package com.example.zzt.recycleview.act

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.ExpandableAdapter
import com.example.zzt.recycleview.adapter.ExpandableAdapterV2
import com.example.zzt.recycleview.util.GroupModel
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

class ActRecycleViewV5 : AppCompatActivity() {
    val TAG = ActRecycleViewV5::class.java.simpleName

    lateinit var rv_list: RecyclerView
    lateinit var rv_list_top: RecyclerView
    var topListDialog: MutableList<String>? = null
    var topListener: BtnHorizontalRecyclerAdapter.OnItemClickListener<String>? = null

    var exAdapter: ExpandableAdapterV2? = null

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
                    0 -> {
                        exAdapter?.clear()
                    }

                    1 -> {
                        exAdapter?.submitList(GroupModel.getExpandableGroupsV2(10, 5))
                    }

                    2 -> {
                        exAdapter?.submitList(
                            GroupModel.getExpandableGroupsRandomV2(
                                10,
                                5,
                                (0..4).random()
                            )
                        )
                    }

                    3 -> {
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }

    fun initViewParent() {
        rv_list.apply {
            layoutManager = LinearLayoutManager(this@ActRecycleViewV5)
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

            exAdapter =
                ExpandableAdapterV2(this@ActRecycleViewV5)
            exAdapter?.setOnHeaderClickListener { adapter, holder, groupPosition ->
                Log.d(TAG, "点击了组项：$groupPosition")
                val expandableAdapter = adapter as ExpandableAdapterV2
                if (expandableAdapter.isExpand(groupPosition)) {
                    expandableAdapter.collapseGroup(groupPosition)
                } else {
                    expandableAdapter.expandGroup(groupPosition)
                }
            }
            exAdapter?.setOnChildClickListener { adapter, holder, groupPosition, childPosition ->
                Log.d(TAG, "点击了组项:$groupPosition  子项:${childPosition}")
            }

            adapter = exAdapter
            exAdapter?.submitList(GroupModel.getExpandableGroupsV2(10, 5))
        }
    }
}