package com.zzt.gemin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zzt.zt_rv_include_rv.R

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */
class HorizontalAdapter(private var dataList: List<HorizontalItem>) :
    RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>() {

    class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        // 可以添加更多 UI 元素，例如商品描述、价格等

        fun bind(item: HorizontalItem) {
            nameTextView.text = item.name
            // 使用图片加载库（例如 Glide、Picasso）加载图片
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.gemin_item_horizontal_item, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    // 新增方法：用于更新数据
    fun updateData(newDataList: List<HorizontalItem>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    private var recyclerView: RecyclerView? = null

    // 新增方法：保存 RecyclerView 实例
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    // 新增方法：滚动 RecyclerView 到指定位置
    fun scrollToPosition(x: Int) {
        recyclerView?.scrollBy(x, 0)
    }

}
