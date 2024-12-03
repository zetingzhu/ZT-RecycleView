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
public class PullToRefreshLinearLayoutV2 extends PullToRefreshBase<LinearLayout> {
    //  可滑动 RecycleView
    private RecyclerView refRv;


    public PullToRefreshLinearLayoutV2(Context context) {
        super(context);
    }

    public PullToRefreshLinearLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshLinearLayoutV2(Context context, AttributeSet attrs, int defStyle) {
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

                    // 找到布局中的可滑动视图
                    int childCount = ((LinearLayout) childAt).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt1 = ((LinearLayout) childAt).getChildAt(i);
                        if (childAt1 instanceof RecyclerView) {
                            refRv = (RecyclerView) childAt1;
                        }
                    }
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
//        Log.d(TAG, "拉动数据 是否可以下拉（1） refreshAction：" + refreshAction);
        if (refreshAction == REFRESH_ACTION_UP) {
            if (refRv != null) {
                return !refRv.canScrollVertically(-1);
            }
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullUp() {
//        Log.d(TAG, "拉动数据 是否可以上拉（2） refreshAction：" + refreshAction);
        if (refreshAction == REFRESH_ACTION_DOWN) {
            if (refRv != null) {
                return !refRv.canScrollVertically(1);
            }
        }
        return false;
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
