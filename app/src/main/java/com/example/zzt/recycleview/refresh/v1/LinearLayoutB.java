package com.example.zzt.recycleview.refresh.v1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @author: zeting
 * @date: 2024/11/3
 */
public class LinearLayoutB extends LinearLayout {
    public LinearLayoutB(Context context) {
        super(context);
    }

    public LinearLayoutB(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutB(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        super.setOnScrollChangeListener(l);

    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }
}
