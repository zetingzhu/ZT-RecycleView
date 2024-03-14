package com.example.zzt.recycleview.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

/**
 * Created by lin on 2018/4/26.
 */
public class MyScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final String TAG = MyScrollingViewBehavior.class.getSimpleName();
    View childView;

    private int mTempTopBottomOffset;

    public void setBehaviorTopAndBottomOffset(int offset) {
        if (childView != null) {
            Log.e("TAG", "需要协调的  滑动位置：childView:" + childView.getClass() + "  -h:" + (childView.getHeight()) + " offset:" + offset);
            ViewCompat.offsetTopAndBottom(childView, offset);
        }
    }

    public MyScrollingViewBehavior() {
    }

    public MyScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child,
                                  int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec,
                                  int heightUsed) {
        final int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // If the menu's height is set to match_parent/wrap_content then measure it
            // with the maximum visible height

            final List<View> dependencies = parent.getDependencies(child);
            final View header = dependencies.get(0);
            if (header != null) {
                if (ViewCompat.getFitsSystemWindows(header) && !ViewCompat.getFitsSystemWindows(child)) {
                    // If the header is fitting system windows then we need to also,
                    // otherwise we'll get CoL's compatible measuring
                    ViewCompat.setFitsSystemWindows(child, true);

                    if (ViewCompat.getFitsSystemWindows(child)) {
                        // If the set succeeded, trigger a new layout and return true
                        child.requestLayout();
                        return true;
                    }
                }

                int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    // If the measure spec doesn't specify a size, use the current height
                    availableHeight = parent.getHeight();
                }

                final int height = availableHeight - header.getMeasuredHeight()
                        + getScrollRange(header);
                final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                        childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
                                ? View.MeasureSpec.EXACTLY
                                : View.MeasureSpec.AT_MOST);
//                Log.w(TAG, "首页滚动位置 >> 下方滑动9 height：" + height + " heightMeasureSpec:" + heightMeasureSpec);
                // Now measure the scrolling view with the correct height
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        layoutChild(parent, child, layoutDirection);
        childView = child;
        ViewCompat.offsetTopAndBottom(child, mTempTopBottomOffset);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        // We depend on any AppBarLayouts
        return dependency instanceof LinearLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        final CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
        if (behavior instanceof MyBehavior) {
            // Offset the child, pinning it to the bottom the header-dependency, maintaining
            // any vertical gap and overlap
            ViewCompat.offsetTopAndBottom(child, (dependency.getBottom() - child.getTop()));
            mTempTopBottomOffset = child.getTop();
        }
    }

    int getScrollRange(View v) {
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) v.getLayoutParams()).getBehavior();
        if (behavior instanceof MyBehavior) {
            return ((MyBehavior) behavior).getScrollRange(v);
        }
        return v.getMeasuredHeight();
    }
}
