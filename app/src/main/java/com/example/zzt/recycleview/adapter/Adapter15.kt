package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.View
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
class Adapter15 : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mList: List<ItemData>? = null

    /**
     * 更新列表数据
     */
    fun setListData(data: List<ItemData>?, recyclerView: RecyclerView?) {
        this.mList = data ?: ArrayList<ItemData>()
        notifyChanged(recyclerView)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_h1, parent, false)
        return MRVHolder(itemView)
    }



    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MRVHolder) {
            val itemData = mList?.get(holder.bindingAdapterPosition)
            itemData?.let {
                holder.tv_title.text = "${it.id}  >> ${it.title}"
                holder.tv_msg.text = "${it.msg}"
                if (it.bgColor != 0) {
                    holder.itemView.setBackgroundColor(it.bgColor)
                }
            }
        }
    }

    class MRVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView
        var tv_msg: TextView

        init {
            tv_title = itemView.findViewById<TextView>(R.id.tv_title)
            tv_msg = itemView.findViewById<TextView>(R.id.tv_msg)
        }
    }
}