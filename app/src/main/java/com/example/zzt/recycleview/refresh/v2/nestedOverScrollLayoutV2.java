package com.example.zzt.recycleview.refresh.v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.Synchronized;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;

/**
 * @author: zeting
 * @date: 2024/2/22
 */
public class nestedOverScrollLayoutV2 extends LinearLayout implements NestedScrollingParent3 {
    private static final String TAG = nestedOverScrollLayoutV2.class.getSimpleName();
    Context mContext;
    private Scroller mScroller;

    private NestedScrollingParentHelper mParentHelper;

    private int mTouchSlop = 0;
    private Float mMinimumVelocity = 0f;
    private Float mMaximumVelocity = 0f;
    private Float mCurrentVelocity = 0f;

    // 阻尼滑动参数
    private float mMaxDragRate = 2.5f;
    private int mMaxDragHeight = 250;
    private int mScreenHeightPixels = 0;

    private Handler mHandler;
    private boolean mNestedInProgress = false;
    private boolean mIsAllowOverScroll = true;       // 是否允许过渡滑动
    private int mPreConsumedNeeded = 0;         // 在子 View 滑动前，此View需要滑动的距离
    private float mSpinner = 0f;                 // 当前竖直方向上 translationY 的距离

    private ValueAnimator mReboundAnimator;
    private ReboundInterpolatorV2 mReboundInterpolator;

    private Runnable mAnimationRunnable = null;    // 用来实现fling时，先过度滑动再回弹的效果
    private boolean mVerticalPermit = false;                 // 控制fling时等待contentView回到translation = 0 的位置

    private View mRefreshContent = null;

    public nestedOverScrollLayoutV2(Context context) {
        super(context);
        initView(context);
    }

    public nestedOverScrollLayoutV2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public nestedOverScrollLayoutV2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    public nestedOverScrollLayoutV2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }


    private void initView(Context context) {
        this.mContext = context;
        mScroller = new Scroller(context);
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReboundInterpolator = new ReboundInterpolatorV2(ReboundInterpolatorV2.INTERPOLATOR_VISCOUS_FLUID);
        mHandler = new Handler(Looper.getMainLooper());
        mParentHelper = new NestedScrollingParentHelper(this);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinimumVelocity = (float) viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = (float) viewConfiguration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = super.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = super.getChildAt(i);
            if (isContentView(childView)) {
                mRefreshContent = childView;
                break;
            }
        }
    }


    public boolean isScrollableView(View view) {
        return view instanceof AbsListView
                || view instanceof ScrollView
                || view instanceof ScrollingView
                || view instanceof WebView
                || view instanceof NestedScrollingChild;
    }

    public boolean isContentView(View view) {
        return isScrollableView(view)
                || view instanceof ViewPager
                || view instanceof NestedScrollingParent;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 如果处于嵌套滑动状态，正常下发，以确保嵌套滑动的正常运行。
        if (mNestedInProgress) {
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getActionMasked();
        if (interceptReboundByAction(action)) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据条件，是否拦截事件
     * 如果是 down 事件，会终止回弹动画
     */
    private Boolean interceptReboundByAction(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            if (mReboundAnimator != null) {
                mReboundAnimator.setDuration(0);
                mReboundAnimator.cancel();
                mReboundAnimator = null;
            }
        }
        return mReboundAnimator != null;
    }

    // 嵌套滑动开始时调用，
// 方法返回 true 时，表示此Parent能够接收此次嵌套滑动事件
// 返回 false，不接收此次嵌套滑动事件，后续方法都不会调用
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        // 接受竖直方向的嵌套滑动
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    // 当 onStartNestedScroll() 方法返回 true 后，此方法会立刻调用可在此方法做每次嵌套滑动的初始化工作
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        mPreConsumedNeeded = reverseComputeFromDamped2Origin(mSpinner);
        mNestedInProgress = true;
        interceptReboundByAction(MotionEvent.ACTION_DOWN);
    }


    // 当嵌套滑动即将结束时，会调用此方法
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        mNestedInProgress = false;
        overSpinner();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.d(TAG, "onNestedPreScroll");
        if (dy == 0) return;
        // 触摸事件的嵌套滑动才处理
        if (type == ViewCompat.TYPE_TOUCH) {
            int consumedY;
            // 两者异向，加剧过度滑动
            if (mPreConsumedNeeded * dy < 0) {
                consumedY = dy;
                mPreConsumedNeeded -= dy;
                moveTranslation(computeDampedSlipDistance(mPreConsumedNeeded));
            } else {
                // 两者同向，需先将 mPreConsumedNeeded 消耗掉
                int lastConsumedNeeded = mPreConsumedNeeded;
                if (Math.abs(dy) > Math.abs(mPreConsumedNeeded)) {
                    consumedY = mPreConsumedNeeded;
                    mPreConsumedNeeded = 0;
                } else {
                    consumedY = dy;
                    mPreConsumedNeeded -= dy;
                }
                if (lastConsumedNeeded != mPreConsumedNeeded) {
                    moveTranslation(computeDampedSlipDistance(mPreConsumedNeeded));
                }
            }
            consumed[1] = consumedY;
        }
    }

    /**
     * 此 Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            onNestedScrollInternal(dyUnconsumed, type, null);
        }
    }


    /**
     * 此 Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
     * 与上面方法的区别，此方法多了个 consumed 参数，用于存放嵌套滑动执行完后，
     * 被此 parent 消耗的滑动距离
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            onNestedScrollInternal(dyUnconsumed, type, consumed);
        } else {
            consumed[1] += dyUnconsumed;
        }
    }

    @Synchronized
    private void onNestedScrollInternal(int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed == 0) return;
        // dy > 0 向上滚
        int dy = dyUnconsumed;
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            // fling 不处理，直接消耗
            if (consumed != null) {
                consumed[1] += dy;
            }
        } else {
            if ((dy < 0 && mIsAllowOverScroll && WidgetUtil.canRefresh(mRefreshContent, null)) || (dy > 0 && mIsAllowOverScroll && WidgetUtil.canLoadMore(mRefreshContent, null))) {
                mPreConsumedNeeded -= dy;
                moveTranslation(computeDampedSlipDistance(mPreConsumedNeeded));
                if (consumed != null) {
                    consumed[1] += dy;
                }
            }
        }
    }

    private void moveTranslation(Float dy) {
        for (int i = 0; i < super.getChildCount(); i++) {
            super.getChildAt(i).setTranslationY(dy);
        }
        mSpinner = dy;
    }

    /**
     * 计算阻尼滑动距离
     *
     * @param originTranslation 原始应该滑动的距离
     * @return Float, 计算结果
     */
    private float computeDampedSlipDistance(int originTranslation) {
        float var10001;
        float dragRate;
        float m;
        int h;
        float x;
        float y;
        float var7;
        float var8;
        if (originTranslation >= 0) {
            dragRate = 0.5F;
            m = this.mMaxDragRate < (float) 10 ? this.mMaxDragRate * (float) this.mMaxDragHeight : this.mMaxDragRate;
            h = RangesKt.coerceAtLeast(this.mScreenHeightPixels / 2, this.getHeight());
            x = RangesKt.coerceAtLeast((float) originTranslation * dragRate, 0.0F);
            var10001 = (float) 1;
            var7 = 100.0F;
            var8 = -x / (float) (h == 0 ? 1 : h);
            y = m * (var10001 - (float) Math.pow((double) var7, (double) var8));
            return y;
        } else {
            dragRate = 0.5F;
            m = this.mMaxDragRate < (float) 10 ? this.mMaxDragRate * (float) this.mMaxDragHeight : this.mMaxDragRate;
            h = RangesKt.coerceAtLeast(this.mScreenHeightPixels / 2, this.getHeight());
            x = -RangesKt.coerceAtMost((float) originTranslation * dragRate, 0.0F);
            float var10000 = -m;
            var10001 = (float) 1;
            var7 = 100.0F;
            var8 = -x / (float) (h == 0 ? 1 : h);
            y = var10000 * (var10001 - (float) Math.pow((double) var7, (double) var8));
            return y;
        }
    }

    private void overSpinner() {
        animSpinner(0f, 0, mReboundInterpolator, 1000);
    }

    /**
     * 通过动画模拟滑动到translationY = [endSpinner] 处
     *
     * @param endSpinner   最终要滑动到的距离
     * @param startDelay   动画延迟开始时间 ms
     * @param interpolator 动画插值器
     * @param duration     动画持续时间
     * @return ValueAnimator 执行动画的对象
     */
    private ValueAnimator animSpinner(float endSpinner, long startDelay, Interpolator interpolator, long duration) {
        if (mSpinner != endSpinner) {
            Log.d(TAG, "start anim");
            if (mReboundAnimator != null) {
                mReboundAnimator.setDuration(0);
                mReboundAnimator.cancel();
                mAnimationRunnable = null;
            }
            AnimatorListenerAdapter endListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                    super.onAnimationEnd(animation, isReverse);
                    // cancel() 会导致 onAnimationEnd，通过设置duration = 0 来标记动画被取消
                    if (animation != null && animation.getDuration() == 0L) {
                        return;
                    }

                    mReboundAnimator.removeAllUpdateListeners();
                    mReboundAnimator.removeAllListeners();
                    mReboundAnimator = null;
                }
            };
            ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    Integer spinner = (Integer) animation.getAnimatedValue();
                    moveTranslation(spinner.floatValue());
                }
            };
            mReboundAnimator = ValueAnimator.ofInt((int) mSpinner, (int) endSpinner);
            mReboundAnimator.setDuration(duration);
            mReboundAnimator.setInterpolator(interpolator);
            mReboundAnimator.setStartDelay(startDelay);
            mReboundAnimator.addListener(endListener);
            mReboundAnimator.addUpdateListener(updateListener);
            mReboundAnimator.start();
            return mReboundAnimator;
        }
        return null;
    }

    /**
     * 给出阻尼计算的距离，计算原始滑动距离
     *
     * @param dampedDistance 阻尼计算过后的距离
     * @return Float, 计算结果
     */
    private int reverseComputeFromDamped2Origin(float dampedDistance) {
        int var10000;
        float dragRate;
        float m;
        int h;
        if (dampedDistance >= (float) 0) {
            dragRate = 0.5F;
            m = this.mMaxDragRate < (float) 10 ? this.mMaxDragRate * (float) this.mMaxDragHeight : this.mMaxDragRate;
            h = RangesKt.coerceAtLeast(this.mScreenHeightPixels / 2, this.getHeight());
            var10000 = MathKt.roundToInt((float) (-h) * MathKt.log((float) 1 - dampedDistance / m, 100.0F) / dragRate);
        } else {
            dragRate = 0.5F;
            m = this.mMaxDragRate < (float) 10 ? this.mMaxDragRate * (float) this.mMaxDragHeight : this.mMaxDragRate;
            h = RangesKt.coerceAtLeast(this.mScreenHeightPixels / 2, this.getHeight());
            float y = -dampedDistance;
            var10000 = -MathKt.roundToInt((float) (-h) * MathKt.log((float) 1 - y / m, 100.0F) / dragRate);
        }

        return var10000;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        // 返回 true，会接管子 View 的 fling 事件，子 View 的 fling 代码不会执行。
        return startFlingIfNeed(-velocityY);
    }

    private boolean startFlingIfNeed(float flingVelocity) {
        float velocity = flingVelocity == 0.0F ? this.mCurrentVelocity : flingVelocity;
        if (Math.abs(velocity) > this.mMinimumVelocity && (velocity < (float) 0 && this.mIsAllowOverScroll && this.mSpinner == 0.0F || velocity > (float) 0 && this.mIsAllowOverScroll && this.mSpinner == 0.0F)) {
            this.mScroller.fling(0, 0, 0, (int) (-velocity), 0, 0, -2147483647, Integer.MAX_VALUE);
            this.mScroller.computeScrollOffset();
            View thisView = (View) this;
            thisView.invalidate();
        }

        return false;
    }

    // 这个方法会被多次调用，直至满足过度滑动的条件：
    // finalY < 0 && WidgetUtil.canRefresh(mRefreshContent, null)
    // || finalY > 0 && WidgetUtil.canLoadMore(mRefreshContent, null
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            int finalY = this.mScroller.getFinalY();
            if ((finalY >= 0 || !WidgetUtil.canRefresh(this.mRefreshContent, (PointF) null)) && (finalY <= 0 || !WidgetUtil.canLoadMore(this.mRefreshContent, (PointF) null))) {
                this.mVerticalPermit = true;
                this.invalidate();
            } else {
                if (this.mVerticalPermit) {
                    float velocity = finalY > 0 ? -this.mScroller.getCurrVelocity() : this.mScroller.getCurrVelocity();
                    this.animSpinnerBounce(velocity);
                }
                this.mScroller.forceFinished(true);
            }
        }
    }


    protected void animSpinnerBounce(float velocity) {
        if (this.mReboundAnimator == null) {
            Log.d(TAG, "animSpinnerBounce = " + this.mSpinner);
            if (this.mSpinner == 0.0F && this.mIsAllowOverScroll) {
                this.mAnimationRunnable = (Runnable) (new BounceRunnable(velocity, 0));
            }
        }
    }

    public int dp2px(@NotNull Context context, float dpValue) {
        Intrinsics.checkNotNullParameter(context, "context");
        Resources var10002 = context.getResources();
        Intrinsics.checkNotNullExpressionValue(var10002, "context.resources");
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, var10002.getDisplayMetrics());
    }

    protected final class BounceRunnable implements Runnable {
        private int mFrame;
        private int mFrameDelay;
        private long mLastTime;
        private float mOffset;
        private float mVelocity;
        private int mSmoothDistance;

        public final int getMFrame() {
            return this.mFrame;
        }

        public final void setMFrame(int var1) {
            this.mFrame = var1;
        }

        public final int getMFrameDelay() {
            return this.mFrameDelay;
        }

        public final void setMFrameDelay(int var1) {
            this.mFrameDelay = var1;
        }

        public final long getMLastTime() {
            return this.mLastTime;
        }

        public final void setMLastTime(long var1) {
            this.mLastTime = var1;
        }

        public final float getMOffset() {
            return this.mOffset;
        }

        public final void setMOffset(float var1) {
            this.mOffset = var1;
        }

        public void run() {
            if (mAnimationRunnable == this) {
                float var10001 = this.mVelocity;
                float var1 = mSpinner;
                float var10002 = Math.abs(var1);
                int var8 = this.mSmoothDistance;
                double var3;
                double var9;
                if (var10002 >= (float) Math.abs(var8)) {
                    if (this.mSmoothDistance != 0) {
                        var9 = 0.45;
                        ++this.mFrame;
                        var3 = (double) (this.mFrame * 2);
                        var10002 = (float) Math.pow(var9, var3);
                    } else {
                        var9 = 0.85;
                        ++this.mFrame;
                        var3 = (double) (this.mFrame * 2);
                        var10002 = (float) Math.pow(var9, var3);
                    }
                } else {
                    var9 = 0.95;
                    ++this.mFrame;
                    var3 = (double) (this.mFrame * 2);
                    var10002 = (float) Math.pow(var9, var3);
                }

                this.mVelocity = var10001 * var10002;
                long now = AnimationUtils.currentAnimationTimeMillis();
                float t = 1.0F * (float) (now - this.mLastTime) / (float) 1000;
                float velocity = this.mVelocity * t;
                if (Math.abs(velocity) >= (float) 1) {
                    this.mLastTime = now;
                    this.mOffset += velocity;
                    moveTranslation(computeDampedSlipDistance(MathKt.roundToInt(this.mOffset)));
                    Handler var10000 = mHandler;
                    if (var10000 != null) {
                        var10000.postDelayed((Runnable) this, (long) this.mFrameDelay);
                    }
                } else {
                    mAnimationRunnable = null;
                    float var5 = mSpinner;
                    float var14 = Math.abs(var5);
                    int var12 = this.mSmoothDistance;
                    if (var14 >= (float) Math.abs(var12)) {
                        nestedOverScrollLayoutV2 var15 = nestedOverScrollLayoutV2.this;
                        Context var16 = getContext();
                        Intrinsics.checkNotNullExpressionValue(var16, "context");
                        float var7 = mSpinner - (float) this.mSmoothDistance;
                        long duration = 10L * (long) RangesKt.coerceAtMost(RangesKt.coerceAtLeast(var15.dp2px(var16, Math.abs(var7)), 30), 100);
                        animSpinner((float) this.mSmoothDistance, 0L, (Interpolator) mReboundInterpolator, duration);
                    }
                }
            }

        }

        public final float getMVelocity() {
            return this.mVelocity;
        }

        public final void setMVelocity(float var1) {
            this.mVelocity = var1;
        }

        public final int getMSmoothDistance() {
            return this.mSmoothDistance;
        }

        public final void setMSmoothDistance(int var1) {
            this.mSmoothDistance = var1;
        }

        public BounceRunnable(float mVelocity, int mSmoothDistance) {
            this.mVelocity = mVelocity;
            this.mSmoothDistance = mSmoothDistance;
            this.mFrameDelay = 10;
            this.mLastTime = AnimationUtils.currentAnimationTimeMillis();
            Handler var10000 = mHandler;
            if (var10000 != null) {
                var10000.postDelayed((Runnable) this, (long) this.mFrameDelay);
            }

        }
    }
}
