package com.example.zzt.recycleview.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zzt.recycleview.databinding.LayoutItemV1Binding;
import com.example.zzt.recycleview.entity.ItemData;

import java.util.List;

/**
 * @author: zeting
 * @date: 2023/9/1
 * ViewBinding 适配器
 */
public class AdapterBinding extends RecyclerView.Adapter<AdapterBinding.MViewHolder> {
    private List<ItemData> mList;

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemV1Binding mBinding = LayoutItemV1Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
        int mPosition = holder.getBindingAdapterPosition();
        ItemData itemData = mList.get(mPosition);
        holder.mBinding.tvTitle.setText(itemData.getId() + "  >> " + itemData.getTitle());
        holder.mBinding.tvMsg.setText(itemData.getMsg());
        if (itemData.getBgColor() != 0) {
            holder.itemView.setBackgroundColor(itemData.getBgColor());
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class MViewHolder extends RecyclerView.ViewHolder {
        LayoutItemV1Binding mBinding;

        public MViewHolder(@NonNull LayoutItemV1Binding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

}
