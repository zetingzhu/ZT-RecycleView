package com.dinuscxj.recycleritemdecoration.decor;

import android.graphics.drawable.ColorDrawable;

/**
 * Created by lin on 2018/7/6.
 */
public class DividerDrawable extends ColorDrawable {

    private int height = -1;
    private int width = -1;

    public DividerDrawable(int color, int height) {
        super(color);
        this.height = height;
    }

    public DividerDrawable(int color, int height, int width) {
        super(color);
        this.height = height;
        this.width = width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }
}
