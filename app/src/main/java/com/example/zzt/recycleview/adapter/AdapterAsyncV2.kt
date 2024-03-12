package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
class AdapterAsyncV2 : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    val TAG = AdapterAsyncV2::class.java.simpleName
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

    override fun getItemCount(): Int {
        return mDiffer?.currentList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MRVHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_v1, parent, false)
        return MRVHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MRVHolder) {
            val mPosition = holder.bindingAdapterPosition
            val itemData = mDiffer?.currentList?.get(mPosition)
            itemData?.let {
                holder.tv_title.text = "${it.id}  >> ${it.title}"
                holder.tv_msg.text = "${it.msg}"
                if (it.bgColor != 0) {
                    holder.itemView.setBackgroundColor(it.bgColor)
                }
            }
        }
    }

    class MRVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView
        var tv_msg: TextView

        init {
            tv_title = itemView.findViewById<TextView>(R.id.tv_title)
            tv_msg = itemView.findViewById<TextView>(R.id.tv_msg)
        }
    }
}