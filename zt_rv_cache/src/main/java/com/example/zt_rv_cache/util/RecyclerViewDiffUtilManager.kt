package com.example.zt_rv_cache.util

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: zeting
 * @date: 2025/7/9
 *
 * 通用的 RecyclerView 数据差异管理工具类，封装了 AsyncListDiffer。
 *
 * @param T 列表项的数据类型
 * @param adapter 与此 Manager 关联的 RecyclerView.Adapter
 * @param diffCallback 用于计算数据差异的 DiffUtil.ItemCallback
 */
class RecyclerViewDiffUtilManager<T>(
    private val adapter: RecyclerView.Adapter<*>, // 注意：这里使用 RecyclerView.Adapter<*>，因为我们不需要知道 ViewHolder 的具体类型
    private val diffCallback: DiffUtil.ItemCallback<T>
) {

    // 内部的 AsyncListDiffer 实例
    private val differ: AsyncListDiffer<T> = AsyncListDiffer(adapter, diffCallback)

    /**
     * 获取当前 RecyclerView 中显示的数据列表。
     * @return 当前数据列表（只读）
     */
    fun getCurrentList(): List<T> {
        return differ.currentList
    }

    /**
     * 提交一个新的数据列表以更新 RecyclerView。
     * AsyncListDiffer 会在后台线程计算差异并自动更新 Adapter。
     *
     * @param newList 要显示的新数据列表
     */
    fun submitList(newList: List<T>?) {
        // 传入 null 或 emptyList() 可以清空列表
        differ.submitList(newList)
    }

    /**
     * 获取指定位置的数据项。
     * @param position 数据项在列表中的位置
     * @return 对应位置的数据项
     * @throws IndexOutOfBoundsException 如果位置超出当前列表范围
     */
    fun getItem(position: Int): T {
        return differ.currentList[position]
    }

    /**
     * 获取当前列表中的数据项数量。
     * @return 数据项数量
     */
    fun getItemCount(): Int {
        return differ.currentList.size
    }
}