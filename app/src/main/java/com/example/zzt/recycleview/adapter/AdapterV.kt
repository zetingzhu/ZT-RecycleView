package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.hold.BaseRecyclerViewHolder


/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
class AdapterV : RecyclerView.Adapter<BaseRecyclerViewHolder>() {
    var mList: MutableList<ItemData>? = null

    /**
     * 更新列表数据
     */
    fun setListData(data: MutableList<ItemData>?, recyclerView: RecyclerView?) {
        this.mList = data ?: mutableListOf()
        notifyChanged(recyclerView)
    }


    fun addOneData(item: ItemData?) {
        if (mList != null && item != null) {
            mList?.add(0, item)
            notifyItemInserted(0)
            notifyItemRangeChanged(0, getItemCount())
        }
    }

    fun removeOneData(pos: Int) {
        if (mList != null) {
            mList?.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(0, getItemCount())
        }
    }

    /**
     * 刷新数据
     */
    fun notifyChanged(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            if (recyclerView.isComputingLayout) {
                recyclerView.post(Runnable { notifyDataSetChanged() })
            } else {
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_v1, parent, false)
        return BaseRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        val itemData = mList?.get(holder.bindingAdapterPosition)
        itemData?.let {
            holder.get<TextView>(R.id.tv_title).text = "${it.id}  >> ${it.title}"
            holder.get<TextView>(R.id.tv_msg).text = "${it.msg}"
            if (it.bgColor != 0) {
                holder.itemView.setBackgroundColor(it.bgColor)
            }
        }
    }
}