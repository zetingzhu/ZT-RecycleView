package com.example.zt_rvhor_marquee.danmu

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.layout
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R


class MarqueeStaggeredAdapter(private val items: MutableList<MarqueeStaggeredItem>) :
    RecyclerView.Adapter<MarqueeStaggeredAdapter.MarqueeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarqueeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marquee_staggered, parent, false)
        return MarqueeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarqueeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: MarqueeStaggeredItem) {
        items.add(0, item) // 添加到列表开头，配合从右到左滚动
        notifyItemInserted(0)
    }

    fun addItems(newItems: List<MarqueeStaggeredItem>) {
        items.addAll(0, newItems)
        notifyItemRangeInserted(0, newItems.size)
    }


    fun removeLastItem() { // 移除列表末尾的项（视觉上最左边的）
        if (items.isNotEmpty()) {
            items.removeAt(items.size - 1)
            notifyItemRemoved(items.size) // position 是移除前的位置
        }
    }

    fun removeItemsFromEnd(count: Int) {
        if (items.size >= count) {
            val startRemoveIndex = items.size - count
            repeat(count) {
                items.removeAt(items.size - 1)
            }
            notifyItemRangeRemoved(startRemoveIndex, count)
        } else if (items.isNotEmpty()) {
            val oldSize = items.size
            items.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
    }


    class MarqueeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tv_marquee_text)
        private val cardView: CardView = itemView as CardView

        fun bind(item: MarqueeStaggeredItem) {
            textView.text = item.text
            textView.textSize = item.textSizeSp
        }
    }
}
