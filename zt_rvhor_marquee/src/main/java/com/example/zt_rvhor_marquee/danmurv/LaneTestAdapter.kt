package com.example.zt_rvhor_marquee.danmurv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R


/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
class LaneTestAdapter : RecyclerView.Adapter<LaneTestAdapter.ViewHolderTT> {

    var oldItems: List<LaneBean>? = listOf()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LaneBean>() {
        override fun areItemsTheSame(oldItem: LaneBean, newItem: LaneBean): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: LaneBean, newItem: LaneBean): Boolean {
            return oldItem == newItem;
        }
    }
    var mDiffer: AsyncListDiffer<LaneBean>? = null

    constructor() : super() {
        mDiffer = AsyncListDiffer(this, DIFF_CALLBACK)
    }

    /**
     * 设置数据
     */
    fun submitList(list: List<LaneBean>?) {
        this.oldItems = list
        mDiffer?.submitList(list)
    }


    /**
     * 插入数据
     */
    fun addData(newData: List<LaneBean>?) {
        if (newData != null && mDiffer != null) {
            val currentList: MutableList<LaneBean> = ArrayList(mDiffer?.currentList)
            currentList.addAll(newData)
            submitList(currentList)
        }
    }

    /**
     * 设置数据
     */
    fun submitList(list: List<LaneBean>?, commitCallback: Runnable) {
        mDiffer?.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTT {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_lane_v2, parent, false)
        return ViewHolderTT(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderTT, position: Int) {
        val pos = position % (oldItems?.size ?: 1)
        val itemData = mDiffer?.currentList?.get(pos)
        holder.tvText.text = itemData?.text
    }

    override fun getItemCount(): Int {
        val diffSize = mDiffer?.currentList?.size ?: 0
//        return if (diffSize > 0) Integer.MAX_VALUE else 0
        return diffSize
    }

    inner class ViewHolderTT(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvText = itemView.findViewById<TextView>(R.id.tvText)
    }
}