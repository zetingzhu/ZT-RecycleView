package com.example.zzt.recycleview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.databinding.LayoutItemV1Binding
import com.example.zzt.recycleview.entity.ItemData

/**
 * @author: zeting
 * @date: 2023/9/1
 * ViewBinding 适配器
 */
open class AdapterBindingKotlin : RecyclerView.Adapter<AdapterBindingKotlin.MViewHolder>() {
    private val mList: List<ItemData>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        val mBinding =
            LayoutItemV1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val mPosition = holder.bindingAdapterPosition
        val itemData = mList?.get(mPosition)
        itemData?.let {
            holder.mBinding.tvTitle.text = "${it.id}  >> ${it.title}"
            holder.mBinding.tvMsg.text = it.msg
            if (it.bgColor != 0) {
                holder.itemView.setBackgroundColor(it.bgColor)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    inner class MViewHolder(var mBinding: LayoutItemV1Binding) :
        RecyclerView.ViewHolder(mBinding.root)
}