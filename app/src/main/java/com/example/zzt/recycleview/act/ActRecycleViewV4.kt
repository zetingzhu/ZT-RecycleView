package com.example.zzt.recycleview.act

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.ExpandableAdapter
import com.example.zzt.recycleview.util.GroupModel
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos

class ActRecycleViewV4 : ActRecycleViewV1() {
    val TAG = ActRecycleViewV4::class.java.simpleName
    override fun initViewParent() {
        rv_list.apply {
            layoutManager = LinearLayoutManager(this@ActRecycleViewV4)
            //添加自定义分割线
            val decoration = RecycleViewDecorationRemovePos(
                this.context,
                RecycleViewDecorationRemovePos.VERTICAL_TOB_BOTTOM
            )
            decoration.setDrawable(
                DividerDrawable(
                    ContextCompat.getColor(
                        context, R.color.white_60
                    ), 20
                )
            )
            addItemDecoration(decoration)


            var adapter =
                ExpandableAdapter(this@ActRecycleViewV4, GroupModel.getExpandableGroups(10, 5))
            adapter.setOnHeaderClickListener { adapter, holder, groupPosition ->
                Log.d(TAG, "点击了组项：$groupPosition")
                val expandableAdapter = adapter as ExpandableAdapter
                if (expandableAdapter.isExpand(groupPosition)) {
                    expandableAdapter.collapseGroup(groupPosition)
                } else {
                    expandableAdapter.expandGroup(groupPosition)
                }
            }
            adapter.setOnFooterClickListener { adapter, holder, groupPosition ->
                Log.d(TAG, "点击了组项 尾部视图：$groupPosition")
            }
            adapter.setOnChildClickListener { adapter, holder, groupPosition, childPosition ->
                Log.d(TAG, "点击了组项:$groupPosition  子项:${childPosition}")
            }

            setAdapter(adapter)
        }

        topListDialog?.add(4, "空数据")

        topListener =
            BtnHorizontalRecyclerAdapter.OnItemClickListener<String> { itemView, position, data ->
                when (position) {
                    0 -> {
                    }

                    1 -> {
                    }

                    2 -> {
                    }

                    3 -> {
                    }

                    4 -> {
                    }
                }
            }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }
}