package com.dinuscxj.recycleritemdecoration.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dinuscxj.recycleritemdecoration.foundation.RecyclerListAdapter;
import com.dinuscxj.recycleritemdecoration.model.ItemTitle;
import com.zzt.zt_rvitemdecoration.R;

public class TitleViewHolder extends RecyclerListAdapter.ViewHolder<ItemTitle> {
    private ImageView mIvTitleImage;
    private TextView mTvTitleName;

    public TitleViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_title_layout, parent, false));
        mTvTitleName = (TextView) itemView.findViewById(R.id.title_name);
        mIvTitleImage = (ImageView) itemView.findViewById(R.id.title_image);
    }

    @Override
    public void bind(ItemTitle item, int position) {
        mTvTitleName.setText(item.getTitleName());
        mIvTitleImage.setImageResource(item.getDrawableRes());
    }
}
