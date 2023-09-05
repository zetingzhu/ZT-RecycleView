package com.example.zzt.recycleview.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.entity.ItemData
import com.example.zzt.recycleview.hold.BaseRecyclerViewHolder


/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
class AdapterAsync : RecyclerView.Adapter<BaseRecyclerViewHolder> {
    val TAG = AdapterAsync::class.java.simpleName
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldItem == newItem;
        }
    }
    var mDiffer: AsyncListDiffer<ItemData>? = null

    constructor() : super() {
        mDiffer = AsyncListDiffer(this, DIFF_CALLBACK)
    }


    /**
     * 设置数据
     * @param list List<ItemData>
     */
    fun submitList(list: List<ItemData>?) {
        mDiffer?.submitList(list)
    }

    /**
     * 设置数据
     * @param list List<ItemData>?
     * @param commitCallback Runnable
     */
    fun submitList(list: List<ItemData>?, commitCallback: Runnable) {
        mDiffer?.submitList(list, commitCallback)
    }

    /**得到数据源
     *
     * @return MutableList<ItemData>?
     */
    fun getData(): List<ItemData>? {
        return mDiffer?.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_v1, parent, false)
        return BaseRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mDiffer?.currentList?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        val mPosition = holder.bindingAdapterPosition
        val itemData = mDiffer?.currentList?.get(mPosition)
        Log.d(TAG, "刷新数据 mPosition：${mPosition}")
        itemData?.let {
            holder.get<TextView>(R.id.tv_title).text = "${it.id}  >> ${it.title}"
            holder.get<TextView>(R.id.tv_msg).text = "${it.msg}"
            if (it.bgColor != 0) {
                holder.itemView.setBackgroundColor(it.bgColor)
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        Log.d(TAG, "刷新数据 payloads mPosition：${position}")
    }
}