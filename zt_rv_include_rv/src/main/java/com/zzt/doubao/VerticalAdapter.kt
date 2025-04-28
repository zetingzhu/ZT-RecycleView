package com.zzt.doubao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

class VerticalAdapter(private val verticalList: List<List<String>>) :
    RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder>() {

    class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val horizontalRecyclerView: RecyclerView =
            itemView.findViewById(R.id.horizontalRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.doubao_item_vertical, parent, false)
        return VerticalViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val horizontalList = verticalList[position]
        val horizontalAdapter = HorizontalAdapter(horizontalList)
        holder.horizontalRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.horizontalRecyclerView.adapter = horizontalAdapter
    }

    override fun getItemCount(): Int {
        return verticalList.size
    }
}    