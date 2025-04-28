package com.zzt.deepseek

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

class HorizontalAdapter(
    private val items: List<HorizontalItem>,
    private val onItemClick: (HorizontalItem) -> Unit
) : RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        fun bind(item: HorizontalItem) {
            imageView.setImageResource(item.imageResId)
            nameTextView.text = item.name
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deep_item_horizontal, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}