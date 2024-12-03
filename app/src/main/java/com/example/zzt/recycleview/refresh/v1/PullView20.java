package com.example.zzt.recycleview.refresh.v1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.example.zzt.recycleview.R;

/**
 * @author: zeting
 * @date: 2024/11/3
 */
public class PullView20 extends PullToRefreshNestedScrollViewV3 {
    public PullView20(Context context) {
        super(context);
    }

    public PullView20(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullView20(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View createRefreshableView() {
        return inflate(getContext(), R.layout.activity_act_recycle_view_v20, null);
    }
}
