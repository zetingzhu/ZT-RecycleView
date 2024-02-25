package com.example.zzt.recycleview.refresh.v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;

import java.util.Arrays;

import kotlin.jvm.Synchronized;


/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 *
 * @param <T>
 * @author Li Hong
 * @since 2013-7-29
 */
@SuppressLint("NewApi")
public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T>, NestedScrollingParent3, NestedScrollingChild3 {
    private static final String TAG = PullToRefreshBase.class.getSimpleName();
    /**
     * 回滚的时间
     */
    private static final int SCROLL_DURATION = 150;
    /**
     * 阻尼系数
     */
    private static final float OFFSET_RADIO = 2.5f;
    /**
     * 上一次移动的点
     */
    private float mLastMotionY = -1;
    /**
     * 下拉刷新和加载更多的监听器
     */
    private OnRefreshListener<T> mRefreshListener;
    /**
     * 下拉刷新的布局
     */
    private LoadingLayout mHeaderLayout;
    /**
     * 上拉加载更多的布局
     */
    private LoadingLayout mFooterLayout;
    /**
     * HeaderView的高度
     */
    private int mHeaderHeight;
    /**
     * FooterView的高度
     */
    private int mFooterHeight;
    /**
     * 下拉刷新是否可用
     */
    private boolean mPullRefreshEnabled = true;
    /**
     * 上拉加载是否可用
     */
    private boolean mPullLoadEnabled = false;
    /**
     * 判断滑动到底部加载是否可用
     */
    private boolean mScrollLoadEnabled = false;
    /**
     * 是否截断touch事件
     */
    private boolean mInterceptEventEnable = true;
    /**
     * 表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法
     */
    private boolean mIsHandledTouchEvent = false;
    /**
     * 移动点的保护范围值
     */
    private int mTouchSlop;
    /**
     * 下拉的状态
     */
    private ILoadingLayout.State mPullDownState = ILoadingLayout.State.NONE;
    /**
     * 上拉的状态
     */
    private ILoadingLayout.State mPullUpState = ILoadingLayout.State.NONE;
    /**
     * 可以下拉刷新的View
     */
    public T mRefreshableView;
    /**
     * 平滑滚动的Runnable
     */
    private SmoothScrollRunnable mSmoothScrollRunnable;
    /**
     * 可刷新View的包装布局
     */
    private FrameLayout mRefreshableViewWrapper;

    /**
     * 嵌套滚动工具类
     */
    private NestedScrollingParentHelper mParentHelper;
    /**
     * 表示是否嵌套滑动
     */
    private boolean mIsNestedTouchEvent = false;
    /**
     * 标识是否支持嵌套滚动
     */
    private boolean canNestedScrollBoo = false;

    /**
     * 嵌套工具子类工具
     */
    private NestedScrollingChildHelper mChildHelper;
    /**
     * 是否允许拦截事件
     */
    protected boolean mEnableDisallowIntercept;
    /**
     * 滚动视图，距离顶部视图位置
     */
    protected int[] mParentOffsetInWindow = new int[2];
    /**
     * 接受父View消耗的值，消耗的距离
     */
    private final int[] mScrollOffset = new int[2];
    // 窗口偏移
    private final int[] mScrollConsumed = new int[2];

    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PullToRefreshBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mHeaderLayout = createHeaderLoadingLayout(context, attrs);
        mFooterLayout = createFooterLoadingLayout(context, attrs);
        mRefreshableView = createRefreshableView(context, attrs);
        // 校验当前视图是否支持嵌套 滚动
        canNestedScrollBoo = isNestedScrollableView(mRefreshableView);

        if (null == mRefreshableView) {
            throw new NullPointerException("Refreshable view can not be null.");
        }

        addRefreshableView(context, mRefreshableView);
        addHeaderAndFooter(context);

        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        refreshLoadingViewsSize();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private void refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout
                .getContentSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout
                .getContentSize() : 0;

        if (headerHeight < 0) {
            headerHeight = 0;
        }

        if (footerHeight < 0) {
            footerHeight = 0;
        }

        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;

        // 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
        // 因为header和footer是完全看不见的
        headerHeight = (null != mHeaderLayout) ? mHeaderLayout
                .getMeasuredHeight() : 0;
        footerHeight = (null != mFooterLayout) ? mFooterLayout
                .getMeasuredHeight() : 0;
        if (0 == footerHeight) {
            footerHeight = mFooterHeight;
        }

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();

        pTop = -headerHeight;
        if (pBottom > -footerHeight)
            pBottom = -footerHeight;

        setPadding(pLeft, pTop, pRight, pBottom);
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // We need to update the header/footer when our size changes
        refreshLoadingViewsSize();

        // 设置刷新View的大小
        refreshRefreshableViewSize(w, h);

        /**
         * As we're currently in a Layout Pass, we need to schedule another one
         * to layout any changes we've made here
         */
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.VERTICAL != orientation) {
            throw new IllegalArgumentException(
                    "This class only supports VERTICAL orientation.");
        }

        // Only support vertical orientation
        super.setOrientation(orientation);
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }

        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsHandledTouchEvent = false;
                mEnableDisallowIntercept = true;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getY() - mLastMotionY;
                final float absDiff = Math.abs(deltaY);
                // 这里有三个条件：
                // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
                // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
                // 3，isPullLoading()，理由与第2条相同
                if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading()) {
                    mLastMotionY = event.getY();
                    // 第一个显示出来，Header已经显示或拉下
                    if (isPullRefreshEnabled() && isReadyForPullDown()) {
                        // 1，Math.abs(getScrollY()) >
                        // 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                        // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                        // 依然能向上滑动，直到HeaderView完全不可见
                        // 2，deltaY > 0.5f：表示下拉的值大于0.5f
                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY > 0.5f);
                        // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                        // Child的Selector隐藏
                        if (mIsHandledTouchEvent) {
                            mRefreshableView.onTouchEvent(event);
                        }
                    } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                        // 原理如上
                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY < -0.5f);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            default:
                break;
        }
        Log.w(TAG, ">> onInterceptTouchEvent mIsHandledTouchEvent:" + mIsHandledTouchEvent);
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mIsHandledTouchEvent = handled = isPullRefreshEnabled() && isReadyForPullDown()
                        || isPullLoadEnabled() && isReadyForPullUp();

                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;

            case MotionEvent.ACTION_MOVE:
                int dyUnconsumed = (int) (mLastMotionY - ev.getY());
                float deltaY = ev.getY() - mLastMotionY;

                //分发触屏事件给父类处理
                if (dispatchNestedPreScroll(0, (int) deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                    //减掉父类消耗的距离
                    deltaY -= mScrollConsumed[1];
                }

                mLastMotionY = ev.getY() - mScrollOffset[1];
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    pullHeaderLayout((int) (deltaY / OFFSET_RADIO), dyUnconsumed, "move ");
                    handled = true;
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout(deltaY / OFFSET_RADIO);
                    handled = true;
                } else {
                    mIsHandledTouchEvent = false;
                }
                mScrollConsumed[1] = 0;

                dispatchNestedScroll(0, 0, 0, (int) deltaY, mScrollOffset, ViewCompat.TYPE_TOUCH, mScrollConsumed);
                mLastMotionY -= mScrollOffset[1];

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsHandledTouchEvent) {
                    mIsHandledTouchEvent = false;
                    // 当第一个显示出来时
                    if (isReadyForPullDown()) {
                        // 调用刷新
                        if (mPullRefreshEnabled
                                && (mPullDownState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                            startRefreshing();
                            handled = true;
                        } else {
                            onStateChanged(ILoadingLayout.State.NONE, false);
                        }
                        resetHeaderLayout();
                    } else if (isReadyForPullUp()) {
                        // 加载更多
                        if (isPullLoadEnabled()
                                && (mPullUpState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                            startLoading();
                            handled = true;
                        } else {
                            onStateChanged(ILoadingLayout.State.NONE, false);
                        }
                        resetFooterLayout();
                    } else {
                        onStateChanged(ILoadingLayout.State.NONE, false);
                    }
                }
                break;

            default:
                break;
        }

        Log.w(TAG, ">> onTouchEvent handled:" + handled);
        return handled;
    }

    @Override
    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        mPullRefreshEnabled = pullRefreshEnabled;
    }

    @Override
    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        mPullLoadEnabled = pullLoadEnabled;
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        mScrollLoadEnabled = scrollLoadEnabled;
    }

    @Override
    public boolean isPullRefreshEnabled() {
        return mPullRefreshEnabled && (null != mHeaderLayout);
    }

    @Override
    public boolean isPullLoadEnabled() {
        return mPullLoadEnabled && (null != mFooterLayout);
    }

    @Override
    public boolean isScrollLoadEnabled() {
        return mScrollLoadEnabled;
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener<T> refreshListener) {
        mRefreshListener = refreshListener;
    }

    @Override
    public void setOnRefreshListenerV2(OnRefreshListenerV2<T> refreshListener) {

    }


    @Override
    public void onPullDownRefreshComplete() {
        if (isPullRefreshing()) {
            mPullDownState = ILoadingLayout.State.RESET;
            onStateChanged(ILoadingLayout.State.RESET, true);

            // 回滚动有一个时间，我们在回滚完成后再设置状态为normal
            // 在将LoadingLayout的状态设置为normal之前，我们应该禁止
            // 截断Touch事件，因为设里有一个post状态，如果有post的Runnable
            // 未被执行时，用户再一次发起下拉刷新，如果正在刷新时，这个Runnable
            // 再次被执行到，那么就会把正在刷新的状态改为正常状态，这就不符合期望
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mHeaderLayout.setState(ILoadingLayout.State.RESET);
                }
            }, getSmoothScrollDuration());

            resetHeaderLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    @Override
    public void onPullUpRefreshComplete() {
        if (isPullLoading()) {
            mPullUpState = ILoadingLayout.State.RESET;
            onStateChanged(ILoadingLayout.State.RESET, false);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mFooterLayout.setState(ILoadingLayout.State.RESET);
                }
            }, getSmoothScrollDuration());

            resetFooterLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    @Override
    public T getRefreshableView() {
        return mRefreshableView;
    }

    @Override
    public LoadingLayout getHeaderLoadingLayout() {
        return mHeaderLayout;
    }

    @Override
    public LoadingLayout getFooterLoadingLayout() {
        return mFooterLayout;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        if (null != mHeaderLayout) {
            mHeaderLayout.setLastUpdatedLabel(label);
        }

        if (null != mFooterLayout) {
            mFooterLayout.setLastUpdatedLabel(label);
        }
    }

    public void setLastUpdatedLabel() {
//		setLastUpdatedLabel(DateUtil.formatYMDHM(getContext(), System.currentTimeMillis()));
    }

    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     *
     * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis  延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll,
                                 final long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -mHeaderHeight;
                int duration = smoothScroll ? SCROLL_DURATION : 0;

                startRefreshing();
                smoothScrollTo(newScrollValue, duration, 0);
            }
        }, delayMillis);
    }

    /**
     * 创建可以刷新的View
     *
     * @param context context
     * @param attrs   属性
     * @return View
     */
    protected abstract T createRefreshableView(Context context,
                                               AttributeSet attrs);

    /**
     * 判断刷新的View是否滑动到顶部
     *
     * @return true表示已经滑动到顶部，否则false
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * 判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    protected abstract boolean isReadyForPullUp();

    /**
     * 创建Header的布局
     *
     * @param context context
     * @param attrs   属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createHeaderLoadingLayout(Context context,
                                                      AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }

    /**
     * 创建Footer的布局
     *
     * @param context context
     * @param attrs   属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createFooterLoadingLayout(Context context,
                                                      AttributeSet attrs) {
        return new FooterLoadingLayout(context);
    }

    /**
     * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
     *
     * @return 返回值时间为毫秒
     */
    protected long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }

    /**
     * 计算刷新View的大小
     *
     * @param width  当前容器的宽度
     * @param height 当前容器的宽度
     */
    protected void refreshRefreshableViewSize(int width, int height) {
        if (null != mRefreshableViewWrapper) {
            LayoutParams lp = (LayoutParams) mRefreshableViewWrapper
                    .getLayoutParams();
            if (lp.height != height) {
                lp.height = height;
                mRefreshableViewWrapper.requestLayout();
            }
        }
    }

    /**
     * 将刷新View添加到当前容器中
     *
     * @param context         context
     * @param refreshableView 可以刷新的View
     */
    protected void addRefreshableView(Context context, T refreshableView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (refreshableView instanceof ListView) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        // 创建一个包装容器
        mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshableView, width, height);

        // 这里把Refresh view的高度设置为一个很小的值，它的高度最终会在onSizeChanged()方法中设置为MATCH_PARENT
        // 这样做的原因是，如果此是它的height是MATCH_PARENT，那么footer得到的高度就是0，所以，我们先设置高度很小
        // 我们就可以得到header和footer的正常高度，当onSizeChanged后，Refresh view的高度又会变为正常。
        height = 10;
        addView(mRefreshableViewWrapper, new LayoutParams(width,
                height));
    }

    /**
     * 添加Header和Footer
     *
     * @param context context
     */
    protected void addHeaderAndFooter(Context context) {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final LoadingLayout headerLayout = mHeaderLayout;
        final LoadingLayout footerLayout = mFooterLayout;

        if (null != headerLayout) {
            if (this == headerLayout.getParent()) {
                removeView(headerLayout);
            }

            addView(headerLayout, 0, params);
        }

        if (null != footerLayout) {
            if (this == footerLayout.getParent()) {
                removeView(footerLayout);
            }

            addView(footerLayout, -1, params);
        }
    }

    /**
     * 拉动Header Layout时调用
     *
     * @param delta 移动的距离
     */
    protected void pullHeaderLayout(int delta, int dyUnconsumed, String tag) {
        boolean scrolled = mChildHelper.dispatchNestedScroll(0, 0, 0, dyUnconsumed, mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        Log.v(TAG, "pullHeaderLayout >> tag:" + tag + " > delta:" + delta + " dyUnconsumed:" + dyUnconsumed + " dy:" + dy);
        if (delta != 0 && dy == 0) {
            // 下拉时候，视图位置变动就不执行下拉刷新
            return;
        }
        // 向上滑动，并且当前scrollY为0时，不滑动
        int oldScrollY = getScrollYValue();
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            setScrollTo(0, 0);
            return;
        }

        // 向下滑动布局
        setScrollBy(0, -(int) delta);

        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mHeaderHeight;
            mHeaderLayout.onPull(scale);
        }

        // 未处于刷新状态，更新箭头
        int scrollY = Math.abs(getScrollYValue());
        if (isPullRefreshEnabled() && !isPullRefreshing()) {
            if (scrollY > mHeaderHeight) {
                mPullDownState = ILoadingLayout.State.RELEASE_TO_REFRESH;
            } else {
                mPullDownState = ILoadingLayout.State.PULL_TO_REFRESH;
            }

            mHeaderLayout.setState(mPullDownState);
            onStateChanged(mPullDownState, true);
        }
    }

    /**
     * 拉Footer时调用
     *
     * @param delta 移动的距离
     */
    protected void pullFooterLayout(float delta) {
        int oldScrollY = getScrollYValue();
        if (delta > 0 && (oldScrollY - delta) <= 0) {
            setScrollTo(0, 0);
            return;
        }

        setScrollBy(0, -(int) delta);

        if (null != mFooterLayout && 0 != mFooterHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mFooterHeight;
            mFooterLayout.onPull(scale);
        }

        int scrollY = Math.abs(getScrollYValue());
        if (isPullLoadEnabled() && !isPullLoading()) {
            if (scrollY > mFooterHeight) {
                mPullUpState = ILoadingLayout.State.RELEASE_TO_REFRESH;
            } else {
                mPullUpState = ILoadingLayout.State.PULL_TO_REFRESH;
            }
            mFooterLayout.setState(mPullUpState);
            onStateChanged(mPullUpState, false);
        }
    }

    /**
     * 得置header
     */
    protected void resetHeaderLayout() {
        final int scrollY = Math.abs(getScrollYValue());
        final boolean refreshing = isPullRefreshing();

        if (refreshing && scrollY <= mHeaderHeight) {
            smoothScrollTo(0);
            return;
        }

        if (refreshing) {
            smoothScrollTo(-mHeaderHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    /**
     * 重置footer
     */
    protected void resetFooterLayout() {
        int scrollY = Math.abs(getScrollYValue());
        boolean isPullLoading = isPullLoading();

        if (isPullLoading && scrollY <= mFooterHeight) {
            smoothScrollTo(0);
            return;
        }

        if (isPullLoading) {
            smoothScrollTo(mFooterHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    /**
     * 判断是否正在下拉刷新
     *
     * @return true正在刷新，否则false
     */
    protected boolean isPullRefreshing() {
        return (mPullDownState == ILoadingLayout.State.REFRESHING);
    }

    /**
     * 是否正的上拉加载更多
     *
     * @return true正在加载更多，否则false
     */
    protected boolean isPullLoading() {
        return (mPullUpState == ILoadingLayout.State.REFRESHING);
    }

    /**
     * 开始刷新，当下拉松开后被调用
     */
    protected void startRefreshing() {
        // 如果正在刷新
        if (isPullRefreshing()) {
            return;
        }

        mPullDownState = ILoadingLayout.State.REFRESHING;
        onStateChanged(ILoadingLayout.State.REFRESHING, true);

        if (null != mHeaderLayout) {
            mHeaderLayout.setState(ILoadingLayout.State.REFRESHING);
        }

        if (null != mRefreshListener) {
            // 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行刷新回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullDownToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration());
        }
    }

    /**
     * 开始加载更多，上拉松开后调用
     */
    protected void startLoading() {
        // 如果正在加载
        if (isPullLoading()) {
            return;
        }

        mPullUpState = ILoadingLayout.State.REFRESHING;
        onStateChanged(ILoadingLayout.State.REFRESHING, false);

        if (null != mFooterLayout) {
            mFooterLayout.setState(ILoadingLayout.State.REFRESHING);
        }

        if (null != mRefreshListener) {
            // 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行加载回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullUpToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration());
        }
    }

    /**
     * 当状态发生变化时调用
     *
     * @param state      状态
     * @param isPullDown 是否向下
     */
    protected void onStateChanged(ILoadingLayout.State state, boolean isPullDown) {

    }

    /**
     * 设置滚动位置
     *
     * @param x 滚动到的x位置
     * @param y 滚动到的y位置
     */
    private void setScrollTo(int x, int y) {
        scrollTo(x, y);
    }

    /**
     * 设置滚动的偏移
     *
     * @param x 滚动x位置
     * @param y 滚动y位置
     */
    private void setScrollBy(int x, int y) {
        scrollBy(x, y);
    }

    /**
     * 得到当前Y的滚动值
     *
     * @return 滚动值
     */
    private int getScrollYValue() {
        return getScrollY();
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     */
    private void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     * @param duration       滚动时候
     * @param delayMillis    延迟时间，0代表不延迟
     */
    private void smoothScrollTo(int newScrollValue, long duration,
                                long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue,
                    newScrollValue, duration);
        }

        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }

    /**
     * 设置是否截断touch事件
     *
     * @param enabled true截断，false不截断
     */
    private void setInterceptTouchEventEnabled(boolean enabled) {
        mInterceptEventEnable = enabled;
    }

    /**
     * 标志是否截断touch事件
     *
     * @return true截断，false不截断
     */
    private boolean isInterceptTouchEventEnabled() {
        return mInterceptEventEnable;
    }


    /**
     * 实现了平滑滚动的Runnable
     *
     * @author Li Hong
     * @since 2013-8-22
     */
    final class SmoothScrollRunnable implements Runnable {
        /**
         * 动画效果
         */
        private final Interpolator mInterpolator;
        /**
         * 结束Y
         */
        private final int mScrollToY;
        /**
         * 开始Y
         */
        private final int mScrollFromY;
        /**
         * 滑动时间
         */
        private final long mDuration;
        /**
         * 是否继续运行
         */
        private boolean mContinueRunning = true;
        /**
         * 开始时刻
         */
        private long mStartTime = -1;
        /**
         * 当前Y
         */
        private int mCurrentY = -1;

        /**
         * 构造方法
         *
         * @param fromY    开始Y
         * @param toY      结束Y
         * @param duration 动画时间
         */
        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             */
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY);
                return;
            }

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                final long oneSecond = 1000; // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime))
                        / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond),
                        0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime
                        / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;

                setScrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullToRefreshBase.this.postDelayed(this, 16);// SUPPRESS
                // CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }


    /**
     * 嵌套滑动开始时调用，
     * 方法返回 true 时，表示此Parent能够接收此次嵌套滑动事件
     * 返回 false，不接收此次嵌套滑动事件，后续方法都不会调用
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        // 接受竖直方向的嵌套滑动
        boolean boo = canNestedScrollBoo && (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        Log.d(TAG, "onStartNestedScroll boo：" + boo);
        return boo;
    }

    /**
     * 当 onStartNestedScroll() 方法返回 true 后，此方法会立刻调用可在此方法做每次嵌套滑动的初始化工作
     */
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
    }

    /**
     * 当嵌套滑动即将结束时，会调用此方法
     */
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        Log.d(TAG, "滑动结束》》》》》》 mIsHandledTouchEvent：" + mIsNestedTouchEvent);
        if (mIsNestedTouchEvent) {
            mIsNestedTouchEvent = false;
            // 当第一个显示出来时
            if (isReadyForPullDown()) {
                // 调用刷新
                if (mPullRefreshEnabled && (mPullDownState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                    startRefreshing();
                } else {
                    onStateChanged(ILoadingLayout.State.NONE, false);
                }
                resetHeaderLayout();
            } else if (isReadyForPullUp()) {
                // 加载更多
                if (isPullLoadEnabled() && (mPullUpState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                    startLoading();
                } else {
                    onStateChanged(ILoadingLayout.State.NONE, false);
                }
                resetFooterLayout();
            } else {
                onStateChanged(ILoadingLayout.State.NONE, false);
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        onNestedScrollInternal(dyConsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, null);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            onNestedScrollInternal(dyConsumed, dyUnconsumed, type, null);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            Log.d(TAG, "onNestedScroll 子类滚动 0 未消耗距离:" + dyUnconsumed);
            onNestedScrollInternal(dyConsumed, dyUnconsumed, type, consumed);
        } else {
            consumed[1] += dyUnconsumed;
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type);
        if (dy == 0) return;
        // 触摸事件的嵌套滑动才处理
        if (type == ViewCompat.TYPE_TOUCH && mIsNestedTouchEvent) {
            Log.d(TAG, "onNestedPreScroll 父类滚动 0 dy:" + dy + " consumed:" + Arrays.toString(consumed));
            if (isPullRefreshEnabled() && isReadyForPullDown()) {
                pullHeaderLayout((int) ((-1 * dy) / OFFSET_RADIO), (int) (dy / OFFSET_RADIO), " preScroll");
            } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                pullFooterLayout((-1 * dy) / OFFSET_RADIO);
            }
            consumed[1] += dy;
        }
    }

    /**
     * @param dyUnconsumed
     * @param type
     * @param consumed
     */
    @Synchronized
    private void onNestedScrollInternal(int dyConsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        Log.d(TAG, ">> onNestedScrollInternal dyConsumed:" + dyConsumed + "    dyUnconsumed:" + dyUnconsumed + " type:" + type + " consumed:" + Arrays.toString(consumed));

        final int oldScrollY = getScrollY();
        scrollBy(0, dyUnconsumed);
        final int myConsumed = getScrollY() - oldScrollY;

        if (consumed != null) {
            consumed[1] += myConsumed;
        }
        final int myUnconsumed = dyUnconsumed - myConsumed;
        mChildHelper.dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed);

        if (dyUnconsumed == 0) return;
        // dy > 0 向上滚
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            // fling 不处理，直接消耗
//            consumed[1] += dyUnconsumed;
        } else {
            if ((dyUnconsumed < 0 && canVerticalOverScroll(mRefreshableView, -1)) || (dyUnconsumed > 0 && canVerticalOverScroll(mRefreshableView, 1))) {
                mIsNestedTouchEvent = true;
                Log.d(TAG, "onNestedScrollInternal 嵌套滚动开始:" + mIsNestedTouchEvent);
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    pullHeaderLayout((int) ((-1 * dyUnconsumed) / OFFSET_RADIO), myUnconsumed, "NestedScroll");
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout((-1 * dyUnconsumed) / OFFSET_RADIO);
                }
//                consumed[1] += dyUnconsumed;
            }
        }
    }

    /**
     * 检测当前视图是否已经滚动完成
     *
     * @param targetView
     * @param direction  > 0 表向下滚动，< 0 表向上滚动
     * @return 可以过度滚动为 true，反之为 false
     */
    private boolean canVerticalOverScroll(View targetView, int direction) {
        if (targetView != null) {
            // 如果该View还可以滚动，说明还没到过度滚动的时候
            if (targetView.canScrollVertically(direction) && targetView.getVisibility() == View.VISIBLE) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断当前视图是否支持嵌套滚动
     *
     * @param view
     * @return
     */
    public boolean isNestedScrollableView(View view) {
        return view instanceof NestedScrollingChild;
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    /**
     * 各种分发嵌套滑动
     */
    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        Log.d(TAG, ">> dispatchNestedScroll 1 :   dyConsumed:" + dyConsumed + " dyUnconsumed:" + dyUnconsumed + " type:" + type + " consumed:" + Arrays.toString(consumed));
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }

    /**
     * 各种分发嵌套滑动
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        Log.d(TAG, ">> dispatchNestedScroll 2 :   dyConsumed:" + dyConsumed + " dyUnconsumed:" + dyUnconsumed + " type:" + type);
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    /**
     * 各种分发嵌套滑动
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, @Nullable int[] offsetInWindow) {
        Log.d(TAG, ">> dispatchNestedScroll 3 :   dyConsumed:" + dyConsumed + " dyUnconsumed:" + dyUnconsumed);
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    /**
     * 通知开始嵌套滑动流程，调用该函数的时候会去找嵌套滑动的父控件
     * 如果找到了父控件并且父控件说可以滑动就返回true，否则返回false
     * (一般在ACTION_DOWN 事件类型中调用)
     */
    @Override
    public boolean startNestedScroll(int axes, int type) {
        boolean boo = mChildHelper.startNestedScroll(axes, type);
        Log.d(TAG, ">> startNestedScroll:   axes:" + axes + " type:" + type + " boo:" + boo);
        return boo;
    }

    @Override
    public void stopNestedScroll(int type) {
        Log.d(TAG, ">> stopNestedScroll:    type:" + type);
        mChildHelper.stopNestedScroll(type);
    }

    /**
     * 是否有嵌套滑动对应的父控件
     */
    @Override
    public boolean hasNestedScrollingParent(int type) {
        boolean boo = mChildHelper.hasNestedScrollingParent(type);
        Log.d(TAG, ">> hasNestedScrollingParent:    type:" + type + " boo:" + boo);
        return boo;
    }

    /**
     * 在嵌套滑动的子View滑动之前，告诉父View滑动额距离，让父View做相应的处理
     *
     * @param dx             告诉父View水平方向需要滑动的距离
     * @param dy             告诉父View垂直方向需要滑动的距离
     * @param consumed       如果不为NULL，则告诉子View父View的滑动情况:
     *                       consumed[0] 父View告诉子View水平方向滑动的距离(dx)
     *                       consumed[1] 父View告诉子View垂直方向滑动的距离(dy)
     * @param offsetInWindow 可选，length=2的数组，如果父View滑动导致子View的窗口发生了变化(子View的位置发生变化)
     *                       该参数返回x(offsetInWindow[0]),y(offsetInWindow[1])方向的变化
     *                       如果你记录了手指最后的位置，需要根据参数offsetInWindow计算偏移量，
     *                       才能保证下一次的touch事件的计算是正确的。
     * @return true 父View滑动了；false 父View没有滑动。
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        boolean boo = mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        Log.d(TAG, ">> dispatchNestedPreScroll:   dy:" + dy + " consumed:" + Arrays.toString(consumed) +" offsetInWindow:"+ Arrays.toString(offsetInWindow) + " type:" + type + " boo:" + boo);
        return boo;
    }

    /**
     * 设置是否允许嵌套滑动
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }


    /**
     * 这段代码来自谷歌官方的 SwipeRefreshLayout
     */
    @Override
    @SuppressLint("ObsoleteSdkInt")
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        View target = mRefreshableView;
        if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
                && (/*target == null || */ViewCompat.isNestedScrollingEnabled(target))) {
            mEnableDisallowIntercept = disallowIntercept;
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

}
