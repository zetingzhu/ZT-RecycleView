package com.example.zzt.recycleview.refresh.v1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zeting
 * @date: 2024/2/28
 */
public class ZRecyclerView extends RecyclerView {
    private static final String TAG = "ZRV";

    public ZRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ZRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param enabled 开启或关闭嵌套滑动
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        Log.i(TAG, "<< setNestedScrollingEnabled >>  enabled:" + enabled);
        super.setNestedScrollingEnabled(enabled);
    }

    /**
     * @return 返回是否开启嵌套滑动
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        boolean boo = super.isNestedScrollingEnabled();
        Log.i(TAG, "<< isNestedScrollingEnabled >>  boo:" + boo);
        return boo;
    }

    /**
     * 沿着指定的方向开始滑动嵌套滑动
     *
     * @param axes 滑动方向
     * @return 返回是否找到NestedScrollingParent配合滑动
     */

    @Override
    public boolean startNestedScroll(int axes, int type) {
        Log.i(TAG, "<< startNestedScroll >> S >> ");
        boolean boo = super.startNestedScroll(axes, type);
        Log.i(TAG, "<< startNestedScroll >> E >>  boo:" + boo);
        return boo;
    }

    /**
     * 停止嵌套滑动
     */
    @Override
    public void stopNestedScroll(int type) {
        Log.i(TAG, "<< stopNestedScroll >>  type:" + type);
        super.stopNestedScroll(type);
    }


    /**
     * @return 返回是否有配合滑动NestedScrollingParent
     */
    @Override
    public boolean hasNestedScrollingParent(int type) {
        Log.i(TAG, "<< hasNestedScrollingParent >> S ");
        boolean boo = super.hasNestedScrollingParent(type);
        Log.i(TAG, "<< hasNestedScrollingParent >> E >> boo:" + boo);
        return boo;
    }

    /**
     * 滑动完成后，将已经消费、剩余的滑动值分发给NestedScrollingParent
     *
     * @param dxConsumed     水平方向消费的距离
     * @param dyConsumed     垂直方向消费的距离
     * @param dxUnconsumed   水平方向剩余的距离
     * @param dyUnconsumed   垂直方向剩余的距离
     * @param offsetInWindow 含有View从此方法调用之前到调用完成后的屏幕坐标偏移量，
     *                       可以使用这个偏移量来调整预期的输入坐标（即上面4个消费、剩余的距离）跟踪，此参数可空。
     * @return 返回该事件是否被成功分发
     */

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        boolean boo = super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        Log.i(TAG, "<< dispatchNestedScroll >>  boo:" + boo);
        return boo;
    }

    /**
     * 在滑动之前，将滑动值分发给NestedScrollingParent
     *
     * @param dx             水平方向消费的距离
     * @param dy             垂直方向消费的距离
     * @param consumed       输出坐标数组，consumed[0]为NestedScrollingParent消耗的水平距离、
     *                       consumed[1]为NestedScrollingParent消耗的垂直距离，此参数可空。
     * @param offsetInWindow 同上dispatchNestedScroll
     * @return 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        boolean boo = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        Log.i(TAG, "<< dispatchNestedScroll >>  boo:" + boo);
        return boo;
    }

    /**
     * 将惯性滑动的速度和NestedScrollingChild自身是否需要消费此惯性滑动分发给NestedScrollingParent
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed  NestedScrollingChild自身是否需要消费此惯性滑动
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * 在惯性滑动之前，将惯性滑动值分发给NestedScrollingParent
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return super.dispatchNestedPreFling(velocityX, velocityY);
    }
}
