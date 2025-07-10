package com.example.zt_rvhor_marquee.hor

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

class MarqueeAdapter(private var items: List<MarqueeItem>) :
    RecyclerView.Adapter<MarqueeAdapter.MarqueeViewHolder>() {

    // 为了实现近似无限滚动，可以将列表放大一定倍数
    // 注意：如果数据量本身就很大，直接放大很多倍可能会有性能问题
    // 更高级的无限滚动会动态加载或在 ViewHolder 中处理循环逻辑
    private val actualItemCount = items.size
    val REPEAT_COUNT = if (actualItemCount > 0) 1000 else 1 // 重复次数，用于模拟无限

    inner class MarqueeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView_marquee_item) // 假设 item 布局中有这个 TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarqueeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marquee, parent, false) // 创建你的 item_marquee.xml 布局
        return MarqueeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
        if (actualItemCount == 0) return
        val actualPosition = position % actualItemCount // 映射到实际数据的位置
        holder.textView.text = items[actualPosition].text
    }

    override fun getItemCount(): Int {
        return if (actualItemCount == 0) 0 else actualItemCount * REPEAT_COUNT
    }

    fun updateData(newItems: List<MarqueeItem>) {
        this.items = newItems
        // this.actualItemCount = newItems.size // 如果在构造函数外更新，也需要更新这个
        notifyDataSetChanged() // 或者使用更精确的 notify 方法
    }
}
