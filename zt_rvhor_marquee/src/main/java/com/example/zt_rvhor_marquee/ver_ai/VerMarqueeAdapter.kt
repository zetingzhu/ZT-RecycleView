package com.example.zt_rvhor_marquee.ver_ai

import androidx.compose.ui.semantics.text

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R
import com.example.zt_rvhor_marquee.hor.MarqueeItem


class VerMarqueeAdapter(private var items: List<MarqueeItem>) :
    RecyclerView.Adapter<VerMarqueeAdapter.MarqueeViewHolder>() {

    private val actualItemCount = items.size
    public val REPEAT_COUNT = if (actualItemCount > 0) 1000 else 1

    inner class MarqueeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView_marquee_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarqueeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marquee, parent, false) // 复用 item_marquee.xml
        return MarqueeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
        if (actualItemCount == 0) return
        val actualPosition = position % actualItemCount
        holder.textView.text = items[actualPosition].text
    }

    override fun getItemCount(): Int {
        return if (actualItemCount == 0) 0 else actualItemCount * REPEAT_COUNT
    }

    fun updateData(newItems: List<MarqueeItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
