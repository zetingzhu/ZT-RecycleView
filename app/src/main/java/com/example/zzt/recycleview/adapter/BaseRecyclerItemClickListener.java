package com.example.zzt.recycleview.adapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zeting
 * @date: 2021/6/21
 * 添加一个RecyclerView adapter 适配器点击列表接口
 */
public interface BaseRecyclerItemClickListener<VH extends RecyclerView.ViewHolder> {
    void onItemClick(VH holder, int position, Object obj);
}
