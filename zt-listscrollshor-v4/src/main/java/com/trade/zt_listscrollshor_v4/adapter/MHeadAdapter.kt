package com.trade.zt_listscrollshor_v4.adapter

import android.widget.TextView
import com.trade.zt_listscrollshor_v4.R
import com.trade.zt_listscrollshor_v4.entiy.HeadObj
import com.trade.zt_listscrollshor_v4.util.BaseRecyclerAdapter
import com.trade.zt_listscrollshor_v4.util.BaseRecyclerViewHolder

/**
 * @author: zeting
 * @date: 2025/9/28
 *
 */
class MHeadAdapter(data: MutableList<HeadObj>) :
    BaseRecyclerAdapter<HeadObj, BaseRecyclerViewHolder>(data) {
    override fun getItemLayoutId(viewType: Int): Int {
        return R.layout.head_content_layout
    }

    override fun bindTheData(
        holder: BaseRecyclerViewHolder?,
        data: HeadObj?
    ) {
        var tv_title = holder?.get<TextView>(R.id.tv_title)
        tv_title?.text = data?.title
    }
}