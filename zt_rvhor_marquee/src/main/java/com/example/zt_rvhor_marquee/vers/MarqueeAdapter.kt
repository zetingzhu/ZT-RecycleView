package com.example.zt_rvhor_marquee.vers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
class MarqueeAdapter(private val items: List<String>) :
    RecyclerView.Adapter<MarqueeAdapter.MarqueeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarqueeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_marquee_single, parent, false)
        return MarqueeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
        if (items.isEmpty()) return
        val actualItem = items[position % items.size]
        holder.tvMarqueeLine.text = actualItem
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 0 else Integer.MAX_VALUE
    }

    inner class MarqueeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMarqueeLine: TextView = itemView.findViewById(R.id.textView_marquee_item)
    }
}