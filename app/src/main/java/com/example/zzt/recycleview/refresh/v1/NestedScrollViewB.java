package com.example.zzt.recycleview.refresh.v1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * @author: zeting
 * @date: 2024/11/3
 */
public class NestedScrollViewB extends NestedScrollView {
    public NestedScrollViewB(@NonNull Context context) {
        super(context);
    }

    public NestedScrollViewB(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollViewB(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);


        Log.d("Log", "ScrollChangeListener NestedScrollViewB onScrollChanged:" + t);
    }

    @Override
    public void setOnScrollChangeListener(View.OnScrollChangeListener l) {
        super.setOnScrollChangeListener(l);

        Log.d("Log", "ScrollChangeListener NestedScrollViewB setOnScrollChangeListener");
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
