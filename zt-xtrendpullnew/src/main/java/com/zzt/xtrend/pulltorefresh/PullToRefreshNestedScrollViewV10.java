package com.zzt.xtrend.pulltorefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.zzt.xtrend.pull.R;


/**
 * 封装了ScrollView的下拉刷新
 *
 * @author Li Hong
 * @since 2013-8-22
 */
public abstract class PullToRefreshNestedScrollViewV10 extends SmartRefreshLayout {
    public PullToRefreshNestedScrollViewV10(Context context) {
        super(context);
    }

    public PullToRefreshNestedScrollViewV10(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
