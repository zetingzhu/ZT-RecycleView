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
import com.zzt.MoreRVUtil
import com.zzt.zt_rv_include_rv.R


class VerticalAdapter(
    private val items: List<VerticalItem>,
    private val syncHelper: HorizontalScrollSync?,
    private val syncV2: SafeScrollSync?,
    private val onHorizontalItemClick: (Int, Int) -> Unit // 参数改为 position 方便演示
) : RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder>() {

    inner class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val horizontalRecyclerView: RecyclerView =
            itemView.findViewById(R.id.horizontalRecyclerView)

        fun bind(verticalItem: VerticalItem, position: Int) {
            titleTextView.text = verticalItem.title
            horizontalRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = HorizontalAdapter(verticalItem.horizontalItems) { item ->
                    onHorizontalItemClick(position, verticalItem.horizontalItems.indexOf(item))
                }

//                syncHelper?.registerRecyclerView(this) // 可以用，
                syncV2?.registerRecyclerView(this) // 不能用
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