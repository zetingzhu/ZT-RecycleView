package com.trade.zt_rv_group_list.v1.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_rv_group_list.R
import com.trade.zt_rv_group_list.v1.model.DisplayItem
import com.trade.zt_rv_group_list.v1.model.GroupItem
import com.trade.zt_rv_group_list.v1.model.ItemType

/**
 * 可折叠的RecyclerView适配器
 */
class ExpandableAdapter(
    private val groupItems: MutableList<GroupItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayItems = mutableListOf<DisplayItem>()

    // 点击监听器
    var onGroupClickListener: ((GroupItem, Int) -> Unit)? = null
    var onChildClickListener: ((DisplayItem.ChildDisplayItem, Int) -> Unit)? = null

    init {
        updateDisplayItems()
    }

    /**
     * 更新显示项列表
     */
    private fun updateDisplayItems() {
        displayItems.clear()
        groupItems.forEach { group ->
            displayItems.add(DisplayItem.GroupDisplayItem(group))
            if (group.isExpanded) {
                group.children.forEach { child ->
                    displayItems.add(DisplayItem.ChildDisplayItem(child, group.id))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayItems[position].type) {
            ItemType.GROUP -> VIEW_TYPE_GROUP
            ItemType.CHILD -> VIEW_TYPE_CHILD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group, parent, false)
                GroupViewHolder(view)
            }

            VIEW_TYPE_CHILD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_child, parent, false)
                ChildViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupViewHolder -> {
                val groupItem = (displayItems[position] as DisplayItem.GroupDisplayItem).groupItem
                holder.bind(groupItem, position)
            }

            is ChildViewHolder -> {
                val childItem = displayItems[position] as DisplayItem.ChildDisplayItem
                holder.bind(childItem, position)
            }
        }
    }

    override fun getItemCount(): Int = displayItems.size

    /**
     * 切换组的展开/收起状态
     */
    fun toggleGroup(groupPosition: Int) {
        // 直接从 displayItems 中获取组项
        val displayItem = displayItems.getOrNull(groupPosition)
        if (displayItem !is DisplayItem.GroupDisplayItem) return
        
        val groupItem = displayItem.groupItem
        groupItem.isExpanded = !groupItem.isExpanded

        if (groupItem.isExpanded) {
            // 展开：插入子项
            val insertPosition = groupPosition + 1
            val childDisplayItems = groupItem.children.map { child ->
                DisplayItem.ChildDisplayItem(child, groupItem.id)
            }
            displayItems.addAll(insertPosition, childDisplayItems)
            notifyItemRangeInserted(insertPosition, childDisplayItems.size)
        } else {
            // 收起：移除子项
            val startPosition = groupPosition + 1
            val childCount = groupItem.children.size
            repeat(childCount) {
                displayItems.removeAt(startPosition)
            }
            notifyItemRangeRemoved(startPosition, childCount)
        }

        // 更新组项的图标
        notifyItemChanged(groupPosition)
    }

    /**
     * 组项ViewHolder
     */
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvGroupTitle: TextView by lazy { itemView.findViewById(R.id.tvGroupTitle) }
        private val ivExpandIcon: ImageView by lazy { itemView.findViewById(R.id.ivExpandIcon) }
        private val groupContainer: View by lazy { itemView.findViewById(R.id.groupContainer) }

        fun bind(groupItem: GroupItem, position: Int) {
            tvGroupTitle.text = groupItem.title

            // 设置展开/收起图标
            val iconRes = if (groupItem.isExpanded) {
                R.drawable.ic_expand_less
            } else {
                R.drawable.ic_expand_more
            }
            ivExpandIcon.setImageResource(iconRes)

            // 添加旋转动画
            val rotation = if (groupItem.isExpanded) 180f else 0f
            ObjectAnimator.ofFloat(ivExpandIcon, "rotation", rotation).apply {
                duration = 200L
                start()
            }

            // 设置点击监听
            groupContainer.setOnClickListener {
                onGroupClickListener?.invoke(groupItem, position)
                toggleGroup(position)
            }
        }
    }

    /**
     * 子项ViewHolder
     */
    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvChildTitle: TextView by lazy { itemView.findViewById(R.id.tvChildTitle) }
        private val tvChildDescription: TextView by lazy { itemView.findViewById(R.id.tvChildDescription) }
        private val childContainer: View by lazy { itemView.findViewById(R.id.childContainer) }

        fun bind(childDisplayItem: DisplayItem.ChildDisplayItem, position: Int) {
            val childItem = childDisplayItem.childItem

            tvChildTitle.text = childItem.title

            if (childItem.description.isNotEmpty()) {
                tvChildDescription.text = childItem.description
                tvChildDescription.visibility = View.VISIBLE
            } else {
                tvChildDescription.visibility = View.GONE
            }

            // 设置点击监听
            childContainer.setOnClickListener {
                onChildClickListener?.invoke(childDisplayItem, position)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_CHILD = 1
    }
}