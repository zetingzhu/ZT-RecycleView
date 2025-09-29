package com.trade.zt_listscrollshor_v4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_listscrollshor_v4.R
import com.trade.zt_listscrollshor_v4.entiy.SubItemObj

/**
 * 横向子项适配器
 */
class SubItemAdapter(private var dataList: MutableList<SubItemObj>) :
    RecyclerView.Adapter<SubItemAdapter.ViewHolder>() {

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvSubTitle.text = data.title
        holder.tvSubContent.text = data.content
        
        // 确保不会在滑动过程中改变布局尺寸
        holder.itemView.setHasTransientState(false)
    }
    
    override fun getItemCount(): Int = dataList.size
    
    /**
     * 更新数据列表
     */
    fun updateData(newData: MutableList<SubItemObj>) {
        this.dataList = newData
        notifyDataSetChanged()
    }
    

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubTitle: TextView = itemView.findViewById(R.id.tv_sub_title)
        val tvSubContent: TextView = itemView.findViewById(R.id.tv_sub_content)
    }
}