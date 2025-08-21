package com.example.zt_listscrollshor3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_listscrollshor2.R

class MyAdapter(
    private val dataList: List<List<String>>,
    private val headerScrollView: HorizontalScrollView
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // 保存所有可横向滑动的HorizontalScrollView，用于同步滚动
    private val scrollViewList = mutableListOf<HorizontalScrollView>()

    // 标记是否正在同步滚动，避免循环触发
    private var isSyncing = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        // 设置固定列数据
        holder.tvFixed.text = data[0]

        // 设置其他列数据
        holder.tvCol1.text = data[1]
        holder.tvCol2.text = data[2]
        holder.tvCol3.text = data[3]
        holder.tvCol4.text = data[4]

        // 将当前的HorizontalScrollView添加到列表中
        if (!scrollViewList.contains(holder.scrollView)) {
            scrollViewList.add(holder.scrollView)
        }

        // 设置滚动监听，实现同步滚动
        holder.scrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            if (!isSyncing) {
                isSyncing = true
                // 同步表头滚动
                headerScrollView.scrollTo(scrollX, 0)
                // 同步其他所有HorizontalScrollView的滚动位置
                for (scrollView in scrollViewList) {
                    if (scrollView != holder.scrollView) {
                        scrollView.scrollTo(scrollX, 0)
                    }
                }
                isSyncing = false
            }
        }
    }

    // 同步滚动所有内容行
    fun syncScroll(scrollX: Int) {
        if (!isSyncing) {
            isSyncing = true
            for (scrollView in scrollViewList) {
                scrollView.scrollTo(scrollX, 0)
            }
            isSyncing = false
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFixed: TextView = itemView.findViewById(R.id.tv_fixed)
        val tvCol1: TextView = itemView.findViewById(R.id.tv_col1)
        val tvCol2: TextView = itemView.findViewById(R.id.tv_col2)
        val tvCol3: TextView = itemView.findViewById(R.id.tv_col3)
        val tvCol4: TextView = itemView.findViewById(R.id.tv_col4)
        val scrollView: HorizontalScrollView = itemView.findViewById(R.id.scrollView)
    }
}