package com.example.zzt.recycleview.decor;

import android.graphics.drawable.ColorDrawable;

/**
 * 设置高度和颜色的分割线
 */
public class DividerDrawable extends ColorDrawable {

    private int height;

    public DividerDrawable(int color, int height) {
        super(color);
        this.height = height;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }
}
