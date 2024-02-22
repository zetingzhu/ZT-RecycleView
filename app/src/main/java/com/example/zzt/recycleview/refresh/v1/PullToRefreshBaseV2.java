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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.example.zzt.recycleview.refresh.v2.WidgetUtil;

import kotlin.jvm.Synchronized;
import kotlin.ranges.RangesKt;


/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 *
 * @param <T>
 * @author Li Hong
 * @since 2013-7-29
 */
@SuppressLint("NewApi")
public abstract class PullToRefreshBaseV2<T extends View> extends LinearLayout implements IPullToRefresh<T>, NestedScrollingParent3 {
    private static final String TAG = PullToRefreshBaseV2.class.getSimpleName();
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
    private OnRefreshListenerV2<T> mRefreshListener;
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
     * 表示是否嵌套滑动
     */
    private boolean mIsNestedTouchEvent = false;
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

    private boolean mIsAllowOverScroll = true;   // 是否允许过渡滑动
    //    private int mPreConsumedNeeded = 0;          // 在子 View 滑动前，此View需要滑动的距离
//    private float mSpinner = 0f;                      // 当前竖直方向上 translationY 的距离
    // 阻尼滑动参数
    private float mMaxDragRate = 2.5f;
    private int mMaxDragHeight = 250;
    private int mScreenHeightPixels;


    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshBaseV2(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshBaseV2(Context context, AttributeSet attrs) {
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
    public PullToRefreshBaseV2(Context context, AttributeSet attrs, int defStyle) {
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
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        // 最小滑动速度
        float mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        // 最大滑动速度
        float mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        mHeaderLayout = createHeaderLoadingLayout(context, attrs);
        mFooterLayout = createFooterLoadingLayout(context, attrs);
        mRefreshableView = createRefreshableView(context, attrs);

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
        Log.d(TAG, "onInterceptTouchEvent :" + isInterceptTouchEventEnabled() + " event:" + event.getAction() + ">> ( down=0,up=1,move=2 )");
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }

        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

//        Log.d(TAG, "onInterceptTouchEvent down=0,uo=1,move=2  12 :" + event.getAction());
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

            default:
                break;
        }

        Log.w(TAG, "onInterceptTouchEvent return:" + mIsHandledTouchEvent);
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent ev=" + ev.getAction() + "  >> ( down=0,up=1,move=2 )");
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
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout(deltaY / OFFSET_RADIO);
                    handled = true;
                } else {
                    mIsHandledTouchEvent = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsHandledTouchEvent) {
                    mIsHandledTouchEvent = false;
                    // 当第一个显示出来时
                    if (isReadyForPullDown()) {
                        // 调用刷新
                        if (mPullRefreshEnabled && (mPullDownState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
                            startRefreshing();
                            handled = true;
                        } else {
                            onStateChanged(ILoadingLayout.State.NONE, false);
                        }
                        resetHeaderLayout();
                    } else if (isReadyForPullUp()) {
                        // 加载更多
                        if (isPullLoadEnabled() && (mPullUpState == ILoadingLayout.State.RELEASE_TO_REFRESH)) {
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
    }

    @Override
    public void setOnRefreshListenerV2(OnRefreshListenerV2<T> refreshListener) {
        mRefreshListener = refreshListener;
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


        Log.d(TAG, "拉动 pullFooterLayout ：" + delta);
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
                    mRefreshListener.onPullDownToRefresh(PullToRefreshBaseV2.this);
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
                    mRefreshListener.onPullUpToRefresh(PullToRefreshBaseV2.this);
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
                PullToRefreshBaseV2.this.postDelayed(this, 16);// SUPPRESS
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


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy == 0) return;
        // 触摸事件的嵌套滑动才处理
        if (type == ViewCompat.TYPE_TOUCH && mIsNestedTouchEvent) {
            Log.d(TAG, "onNestedPreScroll 父类滚动 0 dy:" + dy);
            if (isPullRefreshEnabled() && isReadyForPullDown()) {
                pullHeaderLayout((-1 * dy) / OFFSET_RADIO);
            } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                pullFooterLayout((-1 * dy) / OFFSET_RADIO);
            }
            
            if (consumed != null) {
                consumed[1] += dy;
            }
        }
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
    }


    // 当嵌套滑动即将结束时，会调用此方法
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        Log.d(TAG, "滑动结束》》》》》》 mIsHandledTouchEvent：" + mIsHandledTouchEvent);
        mIsNestedTouchEvent = false;
        if (mIsHandledTouchEvent) {
            mIsHandledTouchEvent = false;
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
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

    }

    /**
     * 此 Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
     *
     * @param target       The descendent view controlling the nested scroll
     * @param dxConsumed   Horizontal scroll distance in pixels already consumed by target
     * @param dyConsumed   Vertical scroll distance in pixels already consumed by target
     * @param dxUnconsumed Horizontal scroll distance in pixels not consumed by target
     * @param dyUnconsumed Vertical scroll distance in pixels not consumed by target
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
     *
     * @param target       The descendant view controlling the nested scroll
     * @param dxConsumed   Horizontal scroll distance in pixels already consumed by target
     * @param dyConsumed   Vertical scroll distance in pixels already consumed by target
     * @param dxUnconsumed Horizontal scroll distance in pixels not consumed by target
     * @param dyUnconsumed Vertical scroll distance in pixels not consumed by target
     * @param type         the type of input which cause this scroll event
     * @param consumed     Output. Upon this method returning, will contain the scroll
     *                     distances consumed by this nested scrolling parent and the scroll distances
     *                     consumed by any other parent up the view hierarchy
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            Log.d(TAG, "onNestedScroll 子类滚动 0 未消耗距离:" + dyUnconsumed);
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
            if ((dy < 0 && mIsAllowOverScroll && WidgetUtil.canRefresh(mRefreshableView, null)) || (dy > 0 && mIsAllowOverScroll && WidgetUtil.canLoadMore(mRefreshableView, null))) {
                mIsNestedTouchEvent = true;
//                mPreConsumedNeeded -= dy;
//                Log.d(TAG, "拉动 ：" + mPreConsumedNeeded + " dy:" + dyUnconsumed);
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    pullHeaderLayout((float) dyUnconsumed / OFFSET_RADIO);
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    pullFooterLayout((float) dyUnconsumed / OFFSET_RADIO);
                }
//                mPreConsumedNeeded -= dy;
//                moveTranslation(computeDampedSlipDistance(mPreConsumedNeeded));
                if (consumed != null) {
                    consumed[1] += dy;
                }
            }
        }
    }

    private void moveTranslation(Float dy) {
        Log.d(TAG, "上拉滑动距离 dy:" + dy);
//        for (int i = 0; i < super.getChildCount(); i++) {
//            super.getChildAt(i).setTranslationY(dy);
//        }
//        mSpinner = dy;
    }

    /**
     * 计算阻尼滑动距离
     *
     * @param originTranslation 原始应该滑动的距离
     * @return Float, 计算结果
     */
    private float computeDampedSlipDistance(int originTranslation) {
        float dragRate = 0.5F;
        if (originTranslation >= 0) {
            float m = mMaxDragRate < 10f ? mMaxDragRate * mMaxDragHeight : mMaxDragRate;
            int h = Math.max(mScreenHeightPixels / 2, getHeight());
            float x = Math.max(originTranslation * dragRate, 0.0F);
            // y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            float hh = -x / (float) (h == 0 ? 1 : h);
            float y = (float) (m * (1 - Math.pow(100f, hh)));
            return y;
        } else {
            float m = mMaxDragRate < 10f ? mMaxDragRate * mMaxDragHeight : mMaxDragRate;
            int h = Math.max(mScreenHeightPixels / 2, getHeight());
            float x = -Math.min(originTranslation * dragRate, 0.0f);
            // y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            float hh = -x / (h == 0 ? 1 : h);
            float y = (float) (-m * (1 - Math.pow(100f, hh)));
            return y;
        }
    }


}