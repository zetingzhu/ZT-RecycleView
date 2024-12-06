package com.zzt.xtrend.pulltorefresh;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 *
 * @param <T>
 * @author Li Hong
 * @since 2013-7-29
 */
@SuppressLint("NewApi")
public abstract class PullToRefreshBase_back1<T extends View> extends LinearLayout implements IPullToRefresh<T>, NestedScrollingParent3 {
    private static final String TAG = PullToRefreshBase_back1.class.getSimpleName();

    /**
     * 定义了下拉刷新和上拉加载更多的接口。
     *
     * @author Li Hong
     * @since 2013-7-29
     */
    public interface OnRefreshListener<V extends View> {

        /**
         * 下拉松手后会被调用
         *
         * @param refreshView 刷新的View
         */
        void onPullDownToRefresh(final PullToRefreshBase_back1<V> refreshView);

        /**
         * 加载更多时会被调用或上拉时调用
         *
         * @param refreshView 刷新的View
         */
        void onPullUpToRefresh(final PullToRefreshBase_back1<V> refreshView);
    }

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

    public static final int REFRESH_ACTION_NONE = 0;
    public static final int REFRESH_ACTION_HEADER = 1;
    public static final int REFRESH_ACTION_FOOTER = 2;
    public int refreshAction = 0;// 添加一个刷新动作 1 下拉，2上拉

    //<editor-fold desc="嵌套滚动">
    private boolean mEnableNestedScrolling = true;//是否启用嵌套滚动功能
    private int[] mParentOffsetInWindow = new int[2];
    private NestedScrollingChildHelper mNestedChild = new NestedScrollingChildHelper(this);
    private NestedScrollingParentHelper mNestedParent = new NestedScrollingParentHelper(this);
    protected int mMinimumVelocity; // 最小速度
    protected int mMaximumVelocity; // 最大速度
    private OverScroller mScroller; // 仍的时候位移计算
    protected int lastScrollY = 0;// 上一次滚动位置
    protected int flingScrollCountY = 0;// 记录底部一共上拉多少
    //</editor-fold>

    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshBase_back1(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshBase_back1(Context context, AttributeSet attrs) {
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
    public PullToRefreshBase_back1(Context context, AttributeSet attrs, int defStyle) {
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

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mHeaderLayout = createHeaderLoadingLayout(context, attrs);
        mFooterLayout = createFooterLoadingLayout(context, attrs);
        mRefreshableView = createRefreshableView(context, attrs);

        setRecyclerViewOnScrollListener();

        if (null == mRefreshableView) {
            throw new NullPointerException("Refreshable view can not be null.");
        }

        addRefreshableView(context, mRefreshableView);
        addHeaderAndFooter(context);

        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshLoadingViewsSize();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 设置 RecyclerView 滑动到底部自动触发加载更多
     */
    private void setRecyclerViewOnScrollListener() {
        if (mRefreshableView instanceof RecyclerView) {
            ((RecyclerView) mRefreshableView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    Log.d(TAG, "加载更多 1  a:" + (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING)
//                            + " b:" + (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() >= 0)
////                            + " c:" + isRecyclerViewToBottom(recyclerView)
//                            + " d:" + isReadyForPullUp()
//                            + " e:" + isReadyForPullDown()
//                            + " f:" + mFooterHeight);

                    if ((newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) && !isReadyForPullDown()  /*列表不在最顶部，在顶部说明列表不满一屏幕，不出发加载更多*/ && isReadyForPullUp()  /*列表在最底部部*/ && isPullLoadEnabled() /*列表有上拉加载更多功能*/) {
                        pullFooterLayout(-1 * mFooterHeight, " Rv 判断");
                        startLoading();
                    }
                }
            });
        }
    }

    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private void refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getContentSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout.getContentSize() : 0;

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
        headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getMeasuredHeight() : 0;
        footerHeight = (null != mFooterLayout) ? mFooterLayout.getMeasuredHeight() : 0;
        if (0 == footerHeight) {
            footerHeight = mFooterHeight;
        }

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();

        pTop = -headerHeight;
        if (pBottom > -footerHeight) pBottom = -footerHeight;

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
            throw new IllegalArgumentException("This class only supports VERTICAL orientation.");
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
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            refreshAction = REFRESH_ACTION_NONE;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsHandledTouchEvent = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = event.getY() - mLastMotionY;
                final float absDiff = Math.abs(deltaY);
                // 这里有三个条件：
                // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
                // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
                // 3，isPullLoading()，理由与第2条相同
                if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading()) {
                    mLastMotionY = event.getY();
                    // 第一个显示出来，Header已经显示或拉下
                    Log.d(TAG, "处理显示顶部状态  deltaY：" + deltaY + " refreshAction:" + refreshAction + " isPullRefreshEnabled:" + isPullRefreshEnabled() + " isReadyForPullDown():" + isReadyForPullDown());
                    if (deltaY >= 1f && isPullRefreshEnabled() && isReadyForPullDown()) {
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
                        if (!mIsHandledTouchEvent) {
                            onStateChanged(ILoadingLayout.State.NONE, true);
                        }

                        // 设置当前状态为下拉
                        refreshAction = REFRESH_ACTION_HEADER;

                    } else if (deltaY <= -1f && isPullLoadEnabled() && isReadyForPullUp()) {
                        // 原理如上
                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY < -0.5f);
                        if (!mIsHandledTouchEvent) {
                            onStateChanged(ILoadingLayout.State.NONE, false);
                        }

                        // 设置当前状态为上拉
                        refreshAction = REFRESH_ACTION_FOOTER;
                    } else {
                        onStateChanged(ILoadingLayout.State.NONE, true);
                        onStateChanged(ILoadingLayout.State.NONE, false);
                    }
                }
                break;

            default:
                break;
        }

        Log.w(TAG, "返回监听状态：   mIsHandledTouchEvent：" + mIsHandledTouchEvent + " refreshAction:" + refreshAction);
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mIsHandledTouchEvent = handled = isPullRefreshEnabled() && isReadyForPullDown() || isPullLoadEnabled() && isReadyForPullUp();
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getY() - mLastMotionY;
                mLastMotionY = ev.getY();
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    pullHeaderLayout(deltaY / OFFSET_RADIO);
                    handled = true;

                    // 设置当前状态为下拉
                    refreshAction = REFRESH_ACTION_HEADER;
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout(deltaY / OFFSET_RADIO, " ACTION_MOVE");
                    handled = true;

                    // 设置当前状态为上拉
                    refreshAction = REFRESH_ACTION_FOOTER;
                } else {
                    mIsHandledTouchEvent = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//            Log.d(TAG , "拉动数据 3  mPullUpState:"+mPullUpState +" mPullDownState:"+mPullDownState);
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

                // 初始化触摸滑动状态
                refreshAction = REFRESH_ACTION_NONE;
                break;

            default:
                break;
        }

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

//    @Override
//    public void setOnRefreshListener(OnRefreshListener<T> refreshListener) {
//        mRefreshListener = refreshListener;
//    }

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
//        setLastUpdatedLabel(DateUtil.formatYMDHM(getContext(), System.currentTimeMillis()));
    }

    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     *
     * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis  延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll, final long delayMillis) {
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
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

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
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }

    /**
     * 创建Footer的布局
     *
     * @param context context
     * @param attrs   属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createFooterLoadingLayout(Context context, AttributeSet attrs) {
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
            LayoutParams lp = (LayoutParams) mRefreshableViewWrapper.getLayoutParams();
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
        addView(mRefreshableViewWrapper, new LayoutParams(width, height));
    }

    /**
     * 添加Header和Footer
     *
     * @param context context
     */
    protected void addHeaderAndFooter(Context context) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
    protected void pullHeaderLayout(float delta) {
        pullHeaderLayout(delta, "");
    }

    protected void pullHeaderLayout(float delta, String tag) {
        if (!TextUtils.isEmpty(tag)) {
            Log.d(TAG, "顶部下拉：" + delta + " tag:" + tag);
        }

        // 向上滑动，并且当前scrollY为0时，不滑动
//        int oldScrollY = getScrollYValue();
//        if (delta < 0 && (oldScrollY - delta) >= 0) {
//            setScrollTo(0, 0);
//            return;
//        }

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
    protected void pullFooterLayout(float delta, String tag) {
        if (!TextUtils.isEmpty(tag)) {
            Log.d(TAG, "底部上拉：" + delta + " tag:" + tag + " mFooterHeight:" + mFooterHeight);
        }

//        int oldScrollY = getScrollYValue();
//        if (delta > 0 && (oldScrollY - delta) <= 0) {
//            setScrollTo(0, 0);
//            return;
//        }

        if (getScrollYValue() < Math.abs(mFooterHeight)) {
            setScrollBy(0, -(int) delta);
        }

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
                    mRefreshListener.onPullDownToRefresh(PullToRefreshBase_back1.this);
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
                    mRefreshListener.onPullUpToRefresh(PullToRefreshBase_back1.this);
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
        Log.i(TAG, "顶部下拉，底部上拉， setScrollTo 抖动监听 y:" + y);
        scrollTo(x, y);
    }

    /**
     * 设置滚动的偏移
     *
     * @param x 滚动x位置
     * @param y 滚动y位置
     */
    private void setScrollBy(int x, int y) {
        Log.i(TAG, "顶部下拉，底部上拉， setScrollBy 抖动监听 y:" + y);
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
    private void smoothScrollTo(int newScrollValue, long duration, long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
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
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY) * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;

                setScrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullToRefreshBase_back1.this.postDelayed(this, 16);// SUPPRESS
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

    public int getFooterHeight() {
        return mFooterHeight;
    }

    /**
     * 禁止 RecycleView 滑动
     * rv.suppressLayout
     */

    /**
     * 启用或者禁止嵌套滑动
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
        mEnableNestedScrolling = enabled;
        mNestedChild.setNestedScrollingEnabled(enabled);
    }

    /**
     * 用于判断嵌套滑动是否被启用
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return mEnableNestedScrolling;
    }


    @Override
    public int getNestedScrollAxes() {
        return mNestedParent.getNestedScrollAxes();
    }

    // 嵌套滑动开始时调用，
    // 方法返回 true 时，表示此Parent能够接收此次嵌套滑动事件
    // 返回 false，不接收此次嵌套滑动事件，后续方法都不会调用
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        // 接受竖直方向的嵌套滑动
        final View thisView = this;
        boolean accepted = thisView.isEnabled() && isNestedScrollingEnabled() && (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (mPullRefreshEnabled || mPullLoadEnabled);
        Log.d(TAG, "onStartNestedScroll >>  accepted：" + accepted);
        return accepted;
    }

    // 当 onStartNestedScroll() 方法返回 true 后，此方法会立刻调用可在此方法做每次嵌套滑动的初始化工作
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedParent.onNestedScrollAccepted(child, target, axes);

        /**
         * 告诉父类开始嵌套滑动
         * true 代表父 View 接受嵌套滑动, false  不接受
         */
        boolean boo = mNestedChild.startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        Log.d(TAG, "onNestedScrollAccepted axes：" + axes + " Parent View boo:" + boo);
    }

    /**
     * 当嵌套滑动即将结束时，会调用此方法
     */
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedParent.onStopNestedScroll(target);
        Log.d(TAG, "onStopNestedScroll target：" + target + " type:" + type);

        // 回弹处理
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
        }
        // Dispatch up our nested parent
        mNestedChild.stopNestedScroll();
    }

    /**
     * NestedScrollingChild滑动完之前将滑动值分发给NestedScrollingParent回调此方法
     * 在子View调用 dispatchNestedPreScroll 之后，这个方法拿到了回调
     *
     * @param target   同上
     * @param dx       水平方向的距离
     * @param dy       水平方向的距离
     * @param consumed 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        /**
         * 子View 滑动前，先分发给父类嵌套滑动
         */
        // 计算当前页面是否 消耗
        int consumedY = 0;
        int scrollY = getScrollYValue();
        if (dy < 0 && scrollY > 0) {
            consumedY = dy;
            setScrollBy(0, consumedY);
            Log.d(TAG, "顶部下拉，底部上拉，顶部校正 consumedY：" + consumedY);
        } else if (dy > 0 && scrollY < 0) {
            consumedY = dy;
            setScrollBy(0, consumedY);
            Log.d(TAG, "顶部下拉，底部上拉，底部校正 consumedY：" + consumedY);
        }
        // 分发给子类 ，
        boolean boo = mNestedChild.dispatchNestedPreScroll(dx, dy - consumedY, consumed, null);
        consumed[1] += consumedY;
//        Log.d(TAG, "onNestedPreScroll >> 已经滚动距离movePullY： " + movePullY + " HF-ScrollY:" + scrollY + "  dy:" + dy + " dy - consumedY:" + (dy - consumedY) + " consumed:" + Arrays.toString(consumed) + " type:" + type + " dispatchNestedPreScroll 3  boo:" + boo);
    }


    /**
     * NestedScrollingChild滑动完成后将滑动值分发给NestedScrollingParent回调此方法
     *
     * @param target       同上
     * @param dxConsumed   水平方向消费的距离
     * @param dyConsumed   垂直方向消费的距离
     * @param dxUnconsumed 水平方向剩余的距离
     * @param dyUnconsumed 垂直方向剩余的距离
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScrollInternal(dyConsumed, dyUnconsumed, type, null);
    }

    /**
     * 此 Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
     * 与上面方法的区别，此方法多了个 consumed 参数，用于存放嵌套滑动执行完后，
     * 被此 parent 消耗的滑动距离
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        onNestedScrollInternal(dyConsumed, dyUnconsumed, type, consumed);
    }

    /**
     * 当前控件滑动处理
     *
     * @param dyConsumed
     * @param dyUnconsumed 子类未消耗
     * @param type
     * @param consumed
     */
    private void onNestedScrollInternal(int dyConsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            /**
             *  先将滑动扔给父控件处理，父控件结束，当前继续
             */
            boolean scrolled = mNestedChild.dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, mParentOffsetInWindow);
            final int dy = dyUnconsumed + mParentOffsetInWindow[1];
            Log.d(TAG, "onNestedScrollInternal dyConsumed：" + dyConsumed + " 未消耗的:" + dyUnconsumed + " scrolled:" + scrolled + " dy:" + dy);
            if (!scrolled && dy != 0) {
                Log.d(TAG, "onNestedScrollInternal 滚动方向：" + refreshAction +
                        "\n 向上 canVerticalOverScroll(mRefreshableView, -1) (true 否能向下滚动) ：" + canVerticalOverScroll(mRefreshableView, -1) +
                        "\n 向下 canVerticalOverScroll(mRefreshableView, 1) (true 能向上滚动)：" + canVerticalOverScroll(mRefreshableView, 1));

                if (dy < 0 && isPullRefreshEnabled() && isReadyForPullDown()) {
                    //     dy < 0 向上滚动
                    pullHeaderLayout((float) (-1 * dy) / OFFSET_RADIO, " scroll");
                    //告诉child消费了多少距离
                    consumed[1] = dy;
                    refreshAction = REFRESH_ACTION_HEADER;
                } else if (dy > 0 && isPullLoadEnabled() && isReadyForPullUp()) {
                    // // dy > 0 向下滚动
                    pullFooterLayout((float) (-1 * dy) / OFFSET_RADIO, " scroll");
                    //告诉child消费了多少距离
                    consumed[1] = dy;
                    refreshAction = REFRESH_ACTION_FOOTER;
                }
            }
        }
    }

    /**
     * 当子控件产生fling滑动时，判断父控件是否处拦截fling，如果父控件处理了fling，那子控件就没有办法处理fling了。
     *
     * @param target    具体嵌套滑动的那个子类
     * @param velocityX 水平方向上的速度 velocityX > 0  向左滑动，反之向右滑动
     * @param velocityY 竖直方向上的速度 velocityY > 0  向上滑动，反之向下滑动
     * @return 父控件是否拦截该fling
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        boolean boo = startFlingIfNeed(velocityY) || mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
        Log.d(TAG, "onNestedFling - pre >> velocityY:" + velocityY + " boo:" + boo);
        return boo;
    }

    /**
     * 当父控件不拦截该fling,那么子控件会将fling传入父控件
     *
     * @param target    具体嵌套滑动的那个子类
     * @param velocityX 水平方向上的速度 velocityX > 0  向左滑动，反之向右滑动
     * @param velocityY 竖直方向上的速度 velocityY > 0  向上滑动，反之向下滑动
     * @param consumed  子控件是否可以消耗该fling，也可以说是子控件是否消耗掉了该fling
     * @return 父控件是否消耗了该fling
     */
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        boolean boo = mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
        Log.d(TAG, "onNestedFling >> velocityY:" + velocityY + " boo:" + boo + " consumed:" + consumed);
        return boo;
    }


    /**
     * 开始 fling
     *
     * @param velocity 速度
     * @return true 可以拦截 嵌套滚动的 Fling
     */
    protected boolean startFlingIfNeed(float velocity) {
        /** 处理扔的事件
         * velocityY >0 向下滚动
         * velocityY <0 向上滚动
         */
        int scrollY = getScrollYValue();
        Log.d(TAG, "onNestedFling >> start scrollY:" + scrollY + " mFooterHeight:" + mFooterHeight + " velocity:" + velocity);
        if (Math.abs(velocity) > mMinimumVelocity) {
            if (mScroller == null) {
                mScroller = new OverScroller(getContext());
            }
            // 启动惯性滚动
            mScroller.fling(0, getScrollY(),
                    0, (int) velocity,
                    0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
            mScroller.computeScrollOffset();
            // 先初始化一下，不然,刷新太快 computeScroll 方法执行延迟会闪出加载更多
            lastScrollY = mScroller.getCurrY();

            flingScrollCountY = 0;
            postInvalidate();

        }
        return false;
    }

    /**
     * 重写 computeScroll 来完成 smart 的特定功能
     * 1.越界回弹
     * 2.边界碰撞
     */
    @Override
    public void computeScroll() {
        if (mScroller != null) {
            int lastCurY = mScroller.getCurrY();
            if (mScroller.computeScrollOffset()) {
                int finalY = mScroller.getFinalY();
                Log.d(TAG, "onNestedFling >> computeScroll finalY:" + finalY + " lastCurY:" + lastCurY + " lastScrollY:" + lastScrollY);

                if (finalY < 0 && isPullRefreshEnabled() && isReadyForPullDown()) {
                    // 下拉刷新仍

                    // 计算向上移动位置
                    float moveY = (lastScrollY - lastCurY) / OFFSET_RADIO;
                    // 开始移动顶部
                    pullHeaderLayout(moveY, " fling");
                    // 记录上一次位置
                    flingScrollCountY -= moveY;
                    Log.d(TAG, "onNestedFling >> computeScroll 下拉刷新 finalY:" + finalY + " flingY:" + moveY + " lastCurY:" + lastCurY + " flingScrollCountY:" + flingScrollCountY + " mHeaderHeight:" + mHeaderHeight);
                    if (Math.abs(flingScrollCountY) > mHeaderHeight) {
                        // 主动刷新
                        startRefreshing();
                        mScroller.forceFinished(true);
                    }
                } else if (finalY > 0 && (isPullLoadEnabled() && isReadyForPullUp())) {
                    // 加载更多惯性

                    // 计算向上移动位置
                    float moveY = (lastScrollY - lastCurY) / OFFSET_RADIO;
                    // 开始移动顶部
                    pullFooterLayout(moveY, " fling");
                    // 记录上一次位置
                    flingScrollCountY += moveY;
                    Log.d(TAG, "onNestedFling >> computeScroll 加载更多 finalY:" + finalY + " moveY:" + moveY + " lastCurY:" + lastCurY + " flingScrollCountY:" + flingScrollCountY + " mFooterHeight:" + mFooterHeight);
                    if (Math.abs(flingScrollCountY) > mFooterHeight) {
                        // 主动加载更多
                        startLoading();
                        mScroller.forceFinished(true);
                    }
                }
                lastScrollY = lastCurY;
                postInvalidate();
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

}
