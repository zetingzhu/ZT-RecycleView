package com.example.zt_rv_cache.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.example.zt_rv_cache.R
import com.example.zt_rv_cache.util.RecyclerViewDiffUtilManager

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 */
// MyData.kt - 你的数据模型，确保它是 data class 或者正确实现了 equals/hashCode
data class MyData(val id: String, val title: String, val description: String)


// MyDataItemCallback.kt - DiffUtil.ItemCallback 的实现
class MyDataItemCallback : DiffUtil.ItemCallback<MyData>() {
    // areItemsTheSame: 判断两个 Item 是否代表同一个逻辑实体（例如，基于唯一ID）
    override fun areItemsTheSame(oldItem: MyData, newItem: MyData): Boolean {
        return oldItem.id == newItem.id
    }

    // areContentsTheSame: 在 areItemsTheSame 返回 true 的情况下，判断 Item 的内容是否发生了变化
    override fun areContentsTheSame(oldItem: MyData, newItem: MyData): Boolean {
        // 如果 MyData 是 data class，直接比较对象即可
        return oldItem == newItem
        // 否则，你需要手动比较所有影响 UI 显示的属性
        // return oldItem.title == newItem.title && oldItem.description == newItem.description
    }

    // 可选：如果你想实现局部更新动画，可以重写此方法
    // override fun getChangePayload(oldItem: MyData, newItem: MyData): Any? {
    //     // 返回一个 Payload 对象，包含变化的具体信息，供 onBindViewHolder 的 payloads 参数使用
    //     // return super.getChangePayload(oldItem, newItem)
    // }
}

class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // 1. 创建 MyDataItemCallback 实例
    private val itemCallback = MyDataItemCallback()

    // 2. 初始化 RecyclerViewDiffUtilManager
    // 将 Adapter 自身和 itemCallback 传入
    private val diffUtilManager = RecyclerViewDiffUtilManager(this, itemCallback)

    // 提供一个公共方法来提交新数据列表
    fun submitList(newList: List<MyData>) {
        diffUtilManager.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_data, parent, false)
        return MyViewHolder(view)
    }

    // getItemCount 直接从 diffUtilManager 获取
    override fun getItemCount(): Int {
        return diffUtilManager.getItemCount()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = diffUtilManager.getItem(position) // 从 diffUtilManager 获取数据项
        holder.bind(item)
    }

    // 如果你在 MyDataItemCallback 中重写了 getChangePayload，可以在这里处理局部更新
    // override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
    //     if (payloads.isEmpty()) {
    //         super.onBindViewHolder(holder, position, payloads)
    //     } else {
    //         // 处理 payloads，只更新变化的UI部分
    //     }
    // }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(data: MyData) {
            titleTextView.text = data.title
            descriptionTextView.text = data.description
        }
    }
}