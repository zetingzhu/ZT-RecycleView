package com.zzt.deepseek

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

class VerticalAdapter(
    private val items: List<VerticalItem>,
    private val onHorizontalItemClick: (Int, Int) -> Unit // 参数改为 position 方便演示
) : RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val horizontalRecyclerView: RecyclerView = itemView.findViewById(R.id.horizontalRecyclerView)

        fun bind(verticalItem: VerticalItem, position: Int) {
            titleTextView.text = verticalItem.title

            horizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = HorizontalAdapter(verticalItem.horizontalItems) { item ->
                    onHorizontalItemClick(position, verticalItem.horizontalItems.indexOf(item))
                }
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deep_item_vertical, parent, false)
        return VerticalViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size
}