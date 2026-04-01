package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.groupedadapter.sticky.IStickyHeaderProvider


/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
class AdapterV25 : RecyclerView.Adapter<RecyclerView.ViewHolder>(), IStickyHeaderProvider<RecyclerView.ViewHolder> {
    var mList: MutableList<ItemData>? = null

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        val itemData = mList?.get(position)
        if (itemData?.header == 1) {
            return TYPE_HEADER
        }
        return TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            return HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_v25_h, parent, false)
            )
        } else {
            return ContentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_v25_c, parent, false)
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val itemData = mList?.get(position)
        if (holder is HeaderViewHolder) {
            holder.tv_title.setText(itemData?.title)
            holder.btn_title.setOnClickListener {
                Toast.makeText(
                    holder.itemView.context,
                    "点击了:" + itemData?.title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (holder is ContentViewHolder) {
            holder.tv_title.setText(itemData?.title)
            holder.tv_msg.setText(itemData?.msg)
        }
    }


    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onBindViewHolderStickyHead(
        holder: RecyclerView.ViewHolder?,
        headPos: Int,
        showHead: Boolean
    ) {
        if (mList != null && holder is HeaderViewHolder && headPos in mList!!.indices) {
            val item = mList?.get(headPos)
            holder.tv_title.setText(item?.title)
        }
    }

    /**
     * 向上回溯，找到当前 position 所属分组的 Header 位置。
     * 同一分组内的所有 content item 都映射到同一个 header position。
     */
    override fun getStickyHeaderPosition(pos: Int): Int {
        if (mList == null || pos < 0) return -1
        val size = mList!!.size
        for (i in pos.coerceAtMost(size - 1) downTo 0) {
            if (mList!![i].header == 1) {
                return i
            }
        }
        return -1
    }

    /**
     * 从当前 header 位置向后查找下一个分组的 Header 位置。
     * 用于 calculateOffset 计算推挤偏移量。
     */
    override fun getNextStickyHeaderPosition(currentHeaderPos: Int): Int {
        if (mList == null) return -1
        val size = mList!!.size
        for (i in (currentHeaderPos + 1) until size) {
            if (mList!![i].header == 1) {
                return i
            }
        }
        return -1
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val btn_title = itemView.findViewById<Button>(R.id.btn_title)
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val tv_msg = itemView.findViewById<TextView>(R.id.tv_msg)
    }

}