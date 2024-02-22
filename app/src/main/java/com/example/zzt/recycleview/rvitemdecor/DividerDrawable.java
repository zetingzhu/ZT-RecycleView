package com.example.zzt.recycleview.rvitemdecor;

import android.graphics.drawable.ColorDrawable;

/**
 * Created by lin on 2018/7/6.
 */
public class DividerDrawable extends ColorDrawable {

    private int height;
    private int width;

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
        if (height != 0) {
            return height;
        }
        return super.getIntrinsicHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        if (width != 0) {
            return width;
        }
        return super.getIntrinsicWidth();
    }
}
