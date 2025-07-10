package com.example.zt_rvhor_marquee.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R
import com.example.zt_rvhor_marquee.hor.MarqueeItem

/**
 * @author: zeting
 * @date: 2025/7/10
 *
 */
class TestAdapter : RecyclerView.Adapter<TestAdapter.MVH> {
    var items: List<MarqueeItem>? = null
    var itemSize = 1

    constructor(items: List<MarqueeItem>?) : super() {
        this.items = items
        itemSize = items?.size ?: 1
    }

    fun setListData(items: List<MarqueeItem>?) {
        this.items = items
        itemSize = items?.size ?: 1
        notifyDataSetChanged()
    }


    inner class MVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView_marquee_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marquee, parent, false) // 创建你的 item_marquee.xml 布局
        return MVH(view)
    }

    override fun getItemCount(): Int {
        return if (itemSize > 0) Int.MAX_VALUE else itemSize
    }

    override fun onBindViewHolder(holder: MVH, position: Int) {
        val bindingAdapterPosition = holder.bindingAdapterPosition
        val pos = bindingAdapterPosition % itemSize
        holder.textView.text = items?.get(pos)?.text
    }
}