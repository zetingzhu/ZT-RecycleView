package com.zzt.xtrend.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @author: zeting
 * @date: 2024/11/19
 * LinearLayout 刷新
 */
public class PullToRefreshLinearLayoutV3 extends PullToRefreshBase<LinearLayout> {
    public PullToRefreshLinearLayoutV3(Context context) {
        super(context);
    }

    public PullToRefreshLinearLayoutV3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshLinearLayoutV3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected LinearLayout createRefreshableView(Context context, AttributeSet attrs) {
        LinearLayout linearLayout = new LinearLayout(context, attrs);
        linearLayout.setOrientation(VERTICAL);
        return linearLayout;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 4) {
            View childAt = getChildAt(3);
            if (childAt instanceof LinearLayout) {
                removeView(childAt);
                LinearLayout refreshableView = getRefreshableView();
                if (refreshableView != null) {
                    refreshableView.addView(childAt, new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
            }
        } else {
            throw new RuntimeException("里面只能允许包含一个视图，请重新查看布局文件");
        }
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return true;
    }

    @Override
    protected boolean isReadyForPullUp() {
        return true;
    }

    @Override
    protected void onStateChanged(ILoadingLayout.State state, boolean isPullDown) {
        super.onStateChanged(state, isPullDown);
//        Log.d(TAG, "拉动数据 释放状态 refreshAction：" + refreshAction + " isPullDown:" + isPullDown + " state:" + state);
        if (ILoadingLayout.State.RESET == state || ILoadingLayout.State.NONE == state) {
            refreshAction = 0;
        }
    }
}
