package com.example.zzt.recycleview.act

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterAsync
import com.example.zzt.recycleview.util.DataListUtil
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.decoration.DividerDrawable
import com.zzt.decoration.RecycleViewDecorationRemovePos
import java.util.Objects

class ActRecycleViewV2 : ActRecycleViewV1() {

    var adapterAsync: AdapterAsync? = null

    override fun initViewParent() {
        rv_list.apply {
            adapterAsync = AdapterAsync()

            layoutManager = LinearLayoutManager(this@ActRecycleViewV2)
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

            adapter = adapterAsync
            adapterAsync?.submitList(DataListUtil.getList100())
        }


        topListDialog?.add(4, "空数据")
        topListDialog?.add(5, "随机 sort")
        topListDialog?.add(6, "排序 sort 1")
        topListDialog?.add(7, "排序 sort 2")
        topListDialog?.add(8, "排序 sort 3")

        topListener = object : BtnHorizontalRecyclerAdapter.OnItemClickListener<String> {

            override fun onItemClick(itemView: View?, position: Int, data: String?) {
                when (position) {
                    0 -> {
                        adapterAsync?.submitList(DataListUtil.getList100())
                    }

                    1 -> {
                        adapterAsync?.submitList(DataListUtil.getList100EvenNumber())
                    }

                    2 -> {
                        adapterAsync?.submitList(DataListUtil.getList100Random())
                    }

                    3 -> {
                        adapterAsync?.submitList(DataListUtil.getList100RandomBG())
                    }

                    4 -> {
                        adapterAsync?.submitList(mutableListOf())
                    }

                    5 -> {
                        adapterAsync?.submitList(DataListUtil.getList100Sort())
                    }

                    6 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV1(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        )
                    }

                    7 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV2(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        ) { rv_list.scrollToPosition(0) }
                    }

                    8 -> {
                        adapterAsync?.submitList(
                            DataListUtil.getList100BySortV3(
                                adapterAsync?.getData()?.toMutableList()
                            )
                        )
                    }
                }
            }
        }
        BtnHorizontalRecyclerAdapter.setAdapterData(rv_list_top, topListDialog, topListener)
    }
}