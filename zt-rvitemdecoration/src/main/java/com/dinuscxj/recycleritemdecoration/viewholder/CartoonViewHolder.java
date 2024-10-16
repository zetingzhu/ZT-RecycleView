package com.dinuscxj.recycleritemdecoration.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dinuscxj.recycleritemdecoration.foundation.RecyclerListAdapter;
import com.dinuscxj.recycleritemdecoration.model.ItemCartoon;
import com.zzt.zt_rvitemdecoration.R;

public class CartoonViewHolder extends RecyclerListAdapter.ViewHolder<ItemCartoon> {
    private ImageView mIvCartoonImage;
    private TextView mTvCartoonName;

    public CartoonViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_image_layout, parent, false));
        mTvCartoonName = (TextView) itemView.findViewById(R.id.item_name);
        mIvCartoonImage = (ImageView) itemView.findViewById(R.id.item_image);
    }

    @Override
    public void bind(ItemCartoon item, int position) {
        mTvCartoonName.setText(item.getCartoonName());
        mIvCartoonImage.setImageResource(item.getDrawableRes());
    }
}
