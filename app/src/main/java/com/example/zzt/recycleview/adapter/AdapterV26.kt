package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.groupedadapter.sticky.BaseGroupListAdapter

/**
 * @author: zeting
 * @date: 2023/8/30
 * 继承 BaseGroupListAdapter，只需关注 UI 绑定
 */
class AdapterV26(
    private val onHeaderClick: (headerPosition: Int) -> Unit
) : BaseGroupListAdapter<ItemData, RecyclerView.ViewHolder>(ItemDataDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    // ==================== 数据层判断 ====================

    override fun isHeader(item: ItemData): Boolean = item.header == 1

    override fun isExpanded(item: ItemData): Boolean = item.expanded

    override fun setExpanded(item: ItemData, expanded: Boolean) {
        item.expanded = expanded
    }

    // ==================== UI 绑定 ====================

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(getItem(position))) TYPE_HEADER else TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_v26_header, parent, false)
            )
        } else {
            ContentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_v26_content, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> {
                holder.tvTitle.text = item.title
                holder.ivArrow.animate()
                    .rotation(if (item.expanded) 180f else 0f)
                    .setDuration(200)
                    .start()
                val posInAll = findPositionInAll(item)
                holder.llHeader.setOnClickListener {
                    onHeaderClick(posInAll)
                }
            }

            is ContentViewHolder -> {
                holder.tvTitle.text = item.title
                holder.tvMsg.text = item.msg
            }
        }
    }

    override fun onBindViewHolderStickyHead(
        holder: RecyclerView.ViewHolder?,
        headPos: Int,
        showHead: Boolean
    ) {
        if (holder is HeaderViewHolder && headPos in 0 until itemCount) {
            val item = getItem(headPos)
            holder.tvTitle.text = item.title
            holder.ivArrow.rotation = if (item.expanded) 180f else 0f
            val posInAll = findPositionInAll(item)
            holder.llHeader.setOnClickListener {
                onHeaderClick(posInAll)
            }
        }
    }

    // ==================== ViewHolder ====================

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
        val llHeader: View = itemView.findViewById(R.id.ll_header)
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvMsg: TextView = itemView.findViewById(R.id.tv_msg)
    }

    class ItemDataDiffCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.header == newItem.header
                    && oldItem.headerIndex == newItem.headerIndex
                    && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem == newItem
        }
    }
}
