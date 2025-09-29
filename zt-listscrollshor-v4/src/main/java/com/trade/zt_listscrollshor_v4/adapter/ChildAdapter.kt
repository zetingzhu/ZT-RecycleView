package com.trade.zt_listscrollshor_v4.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_listscrollshor_v4.R
import com.trade.zt_listscrollshor_v4.entiy.ChildObj
import com.trade.zt_listscrollshor_v4.util.SyncRecycleViewScrollUtil

/**
 * 垂直列表适配器，每个项目包含一个横向RecyclerView
 */
/**
 * 垂直列表适配器，每个项目包含一个横向RecyclerView
 * @property dataList 数据列表
 * @property headRv 头部RecyclerView，用于同步滚动
 */
class ChildAdapter(
    private var dataList: MutableList<ChildObj>,
    private val headRv: RecyclerView?
) : RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    
    // 滚动同步工具
    private val syncRvScrollUtil: SyncRecycleViewScrollUtil = SyncRecycleViewScrollUtil().apply {
        initRecyclerView(headRv)
    }
    
    // 缓存子项适配器，避免重复创建
    private val subAdapters = mutableMapOf<Int, SubItemAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.child_item_layout, parent, false)
        ).also { holder ->
            // 在创建ViewHolder时初始化RecyclerView，避免重复初始化
            holder.rvSubItems.apply {
                layoutManager = LinearLayoutManager(
                    parent.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                setHasFixedSize(true)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvChildTitle.text = data.title

        // 获取或创建子项适配器
        val subAdapter = subAdapters.getOrPut(position) {
            SubItemAdapter(data.subItems)
        }
        
        // 如果适配器发生变化才设置
        if (holder.rvSubItems.adapter !== subAdapter) {
            holder.rvSubItems.adapter = subAdapter
            // 初始化滚动同步
            syncRvScrollUtil.initRecyclerView(holder.rvSubItems)
        } else {
            // 数据变化时更新
            (holder.rvSubItems.adapter as? SubItemAdapter)?.updateData(data.subItems)
        }
    }

    override fun getItemCount(): Int = dataList.size

    /**
     * 更新数据列表
     * @param newData 新的数据列表
     */
    fun updateData(newData: MutableList<ChildObj>) {
        this.dataList = newData
        subAdapters.clear() // 清除缓存的适配器
        notifyDataSetChanged()
    }

    /**
     * 添加数据项
     * @param item 要添加的数据项
     */
    fun addItem(item: ChildObj) {
        val position = dataList.size
        dataList.add(item)
        notifyItemInserted(position)
    }

    /**
     * 在指定位置添加数据项
     * @param position 添加位置
     * @param item 要添加的数据项
     */
    fun addItem(position: Int, item: ChildObj) {
        if (position in 0..dataList.size) {
            dataList.add(position, item)
            // 更新position之后的缓存适配器
            for (i in subAdapters.keys.filter { it >= position }) {
                subAdapters.remove(i)
            }
            notifyItemInserted(position)
        }
    }

    /**
     * 移除指定位置的数据项
     * @param position 要移除的位置
     */
    fun removeItem(position: Int) {
        if (position in 0 until dataList.size) {
            dataList.removeAt(position)
            // 移除并更新缓存的适配器
            subAdapters.remove(position)
            // 更新position之后的缓存适配器
            for (i in subAdapters.keys.filter { it > position }) {
                subAdapters.remove(i)
            }
            notifyItemRemoved(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChildTitle: TextView = itemView.findViewById(R.id.tv_child_title)
        val rvSubItems: RecyclerView = itemView.findViewById(R.id.rv_sub_items)
    }
}