package com.example.zzt.recycleview.refresh.v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

import java.util.Arrays;

import kotlin.jvm.Synchronized;


/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 * <p>
 * <p>
 * NestedScrollingChild3 -> startNestedScroll
 * <p>
 * NestedScrollingParent3 ->  onStartNestedScroll return true(支持) false(不支持)
 * <p>
 * NestedScrollingParent3 -> onNestedScrollAccepted
 * <p>
 * NestedScrollingChild3 -> dispatchNestedPreScroll
 * <p>
 * NestedScrollingParent3 -> onNestedPreScroll
 * <p>
 * NestedScrollingChild3 -> dispatchNestedScroll
 * <p>
 * NestedScrollingParent3 -> onNestedScroll
 * <p>
 * NestedScrollingChild3 -> stopNestedScroll
 * <p>
 * NestedScrollingParent3 -> onStopNestedScroll
 *
 * @param <T>
 * @author Li Hong
 * @since 2013-7-29
 */
@SuppressLint("NewApi")
public abstract class PullToRefreshBaseV2<T extends View> extends LinearLayout implements IPullToRefresh<T>, NestedScrollingParent3
        , NestedScrollingChild3 {
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

    //<editor-fold desc="嵌套滚动">
    protected int[] mParentOffsetInWindow = new int[2];// 子类滑动窗口位置变动
    protected NestedScrollingChildHelper mNestedChild;// 处理父类嵌套滑动工具
    protected NestedScrollingParentHelper mNestedParent;// 处理之类滑动嵌套工具
    protected boolean mEnableNestedScrolling = true;  //是否启用嵌套滚动功能
    //</editor-fold>


    //<editor-fold desc="仍事件">
    protected int mMinimumVelocity;
    protected int mMaximumVelocity;
    protected int mCurrentVelocity;
    protected Scroller mScroller;
    protected VelocityTracker mVelocityTracker;
    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;
    //</editor-fold>

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

        mNestedChild = new NestedScrollingChildHelper(this);
        mNestedParent = new NestedScrollingParentHelper(this);
        // 启动嵌套滚动
        mNestedChild.setNestedScrollingEnabled(mEnableNestedScrolling);

        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent ev=" + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onInterceptTouchEvent :" + isInterceptTouchEventEnabled() + " event:" + event.getAction() + ">> ( down=0,up=1,move=2 )");
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }

        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();
        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

//        Log.d(TAG, "onInterceptTouchEvent down=0,uo=1,move=2  12 :" + event.getAction());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsHandledTouchEvent = false;

                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getY() - mLastMotionY);
                final float absDiff = Math.abs(deltaY);

                //  先询问 Parent 是否消费
                boolean boo = dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH);

                Log.i(TAG, "dispatchNestedPreScroll 1 boo:" + boo );

                if (boo) {
                    Log.i(TAG, "onInterceptTouchEvent Move parent deltaY:" + deltaY + " mScrollConsumed:" + Arrays.toString(mScrollConsumed) + " off:" + Arrays.toString(mScrollOffset));
                    // 如果 Parent 消费了，就从 deltaY 中减去已经消费掉的部分
                    deltaY -= mScrollConsumed[1];
                    mNestedYOffset += mScrollOffset[1];
                }

                Log.i(TAG, "onInterceptTouchEvent Move deltaY:" + deltaY + " mTouchSlop:" + mTouchSlop);

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
                mIsHandledTouchEvent = false;
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_UP:
                mIsHandledTouchEvent = false;
                Log.d(TAG, " 抬起扔的时候 1 ");
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            default:
                break;
        }

        Log.e(TAG, "onInterceptTouchEvent return:" + mIsHandledTouchEvent);
        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onTouchEvent ev=" + ev.getAction() + "  >> ( down=0,up=1,move=2 )");
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mIsHandledTouchEvent = handled = isPullRefreshEnabled() && isReadyForPullDown() || isPullLoadEnabled() && isReadyForPullUp();

                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;

            case MotionEvent.ACTION_MOVE:
                final int y = (int) ev.getY();
                int deltaY = (int) (ev.getY() - mLastMotionY);

                //  先询问 Parent 是否消费
                boolean boo = dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH);

                Log.i(TAG, "dispatchNestedPreScroll 2 boo:" + boo );
                if (boo) {
                    Log.i(TAG, "Touch Move parent mScrollConsumed:" + mScrollConsumed);
                    // 如果 Parent 消费了，就从 deltaY 中减去已经消费掉的部分
                    deltaY -= mScrollConsumed[1];
                    mNestedYOffset += mScrollOffset[1];
                }

                Log.d(TAG, "Touch Move y:" + y + " deltaY:" + deltaY);

                if (Math.abs(deltaY) > 0) {
//                    mLastMotionY = ev.getY();
                    mLastMotionY = y - mScrollOffset[1];
                    if (isPullRefreshEnabled() && isReadyForPullDown()) {
                        pullHeaderLayout(deltaY / OFFSET_RADIO, "move");
                        handled = true;
                    } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                        pullFooterLayout(deltaY / OFFSET_RADIO);
                        handled = true;
                    } else {
                        mIsHandledTouchEvent = false;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, " 抬起扔的时候 2 ");

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

                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;

            default:
                break;
        }

//        Log.w(TAG, "onTouchEvent handled:" + handled);
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
    protected void pullHeaderLayout(float delta, String tag) {
        Log.d(TAG, "顶部下拉：" + delta + " tag:" + tag);
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
        Log.d(TAG, "底部上拉：" + delta);
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
        boolean boo = mNestedChild.dispatchNestedPreScroll(dx, dy, consumed, null);
        Log.d(TAG, "onNestedPreScroll >>  dy:" + dy + " consumed:" + Arrays.toString(consumed) + " type:" + type + " dispatchNestedPreScroll 3  boo:" + boo);

//        dispatchNestedPreScroll(dx, dy, consumed, null, type);
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
     */
    private void onNestedScrollInternal(int dyConsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (type == ViewCompat.TYPE_TOUCH) {
            /**
             *  先将滑动扔给父控件处理，父控件结束，当前继续
             */
            boolean scrolled = mNestedChild.dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, mParentOffsetInWindow);
            final int dy = dyUnconsumed + mParentOffsetInWindow[1];
            Log.d(TAG, "onNestedScrollInternal dyConsumed：" + dyConsumed + " dyUnconsumed:" + dyUnconsumed + " scrolled:" + scrolled + " dy:" + dy);

            if (!scrolled && dy != 0) {
                // dy > 0 向下滚动，dy < 0 向上滚动
                if ((dy < 0 && canVerticalOverScroll(mRefreshableView, -1)) || (dy > 0 && canVerticalOverScroll(mRefreshableView, 1))) {
                    if (isPullRefreshEnabled() && isReadyForPullDown()) {
                        pullHeaderLayout((float) (-1 * dy) / OFFSET_RADIO, " scroll");
                    } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                        pullFooterLayout((float) (-1 * dy) / OFFSET_RADIO);
                    }
                    //告诉child消费了多少距离
                    consumed[1] = dy;
                }
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
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        boolean boo = startFlingIfNeed(velocityY) || mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
        Log.d(TAG, "onNestedFling - pre >> velocityY:" + velocityY + " boo:" + boo);
        return boo;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        boolean boo = mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
        Log.d(TAG, "onNestedFling >> velocityY:" + velocityY + " boo:" + boo + " consumed:" + consumed);
        return boo;
    }

    /**
     * 开始 fling
     *
     * @param flingVelocity
     * @return true 可以拦截 嵌套滚动的 Fling
     */
    protected boolean startFlingIfNeed(float flingVelocity) {
        /** 处理扔的事件
         * velocityY >0 向下滚动
         * velocityY <0 向上滚动
         */
        int scrollY = getScrollY();
        Log.d(TAG, "onNestedFling >> start scrollY:" + scrollY + " mFooterHeight:" + mFooterHeight + " flingVelocity:" + flingVelocity);
//        if (flingVelocity > 0 && scrollY > 0 && scrollY > mFooterHeight) {
//            return true;
//        }
        return false;
    }


    /**
     * 表示View开始滑动了,一般是在ACTION_DOWN中调用，如果返回true则表示父布局支持嵌套滑动。
     * 这个时候正常情况会触发Parent的 onStartNestedScroll() 方法
     */
    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mNestedChild.startNestedScroll(axes);
    }

    /**
     * 一般是在事件结束比如ACTION_UP或者ACTION_CANCEL中调用,告诉父布局滑动结束。
     */
    @Override
    public void stopNestedScroll(int type) {
        mNestedChild.stopNestedScroll(type);
    }

    /**
     * 判断当前View是否有嵌套滑动的Parent。
     */
    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mNestedChild.hasNestedScrollingParent(type);
    }

    /**
     * 在当前View消费一定的滑动距离之后，可能没有消费完，可以通过调用该方法，把剩下的滚动距离
     * 分发给父布局，询问其是否可以再消费。
     * dxConsumed：被当前View消费了的水平方向滑动距离
     * dyConsumed：被当前View消费了的垂直方向滑动距离
     * dxUnconsumed：未被消费的水平滑动距离
     * dyUnconsumed：未被消费的垂直滑动距离
     * offsetInWindow：可选的输出参数。如果不是null，该方法返回时，会将该视图从该操作
     * 之前到该操作完成之后的本地视图坐标中的偏移量封装进该参数中，offsetInWindow[0]水平方向，
     * offsetInWindow[1]垂直方向
     *
     * @return true：表示滚动事件分发成功,fasle: 分发失败
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        boolean boo = mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        Log.w(TAG, "dispatchNestedScroll v2 ： boo:" + boo);
        return boo;
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        Log.w(TAG, "dispatchNestedScroll v3 ");
        mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }

    /**
     * 在当前View消费滚动距离之前把滑动距离传给父布局。相当于把优先处理权交给Parent
     * dx：当前水平方向滑动的距离
     * dy：当前垂直方向滑动的距离
     * consumed：输出参数，会将Parent消费掉的距离封装进该参数，consumed[0]代表水平方向，consumed[1]代表垂直方向
     *
     * @return true：代表Parent消费了滚动距离
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        boolean boo = mNestedChild.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        Log.w(TAG, "dispatchNestedPreScroll 4 ： boo:" + boo);
        return boo;
    }

    /**
     * 将惯性滑动的速度分发给Parent。
     * velocityX：表示水平滑动速度
     * velocityY：垂直滑动速度
     * consumed：true：表示当前View消费了滑动事件，否则传入false
     *
     * @return true：表示Parent处理了滑动事件
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * 在当前View自己处理惯性滑动前，先将滑动事件分发给Parent，一般来说如果想自己处理惯性的滑动事件，
     * 就不应该调用该方法给Parent处理。如果给了Parent并且返回true，那表示Parent已经处理了，自己就不应该再做处理。
     * 返回false，代表Parent没有处理，但是不代表Parent后面就不用处理了
     *
     * @return true：表示Parent处理了惯性滑动事件
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
    }
}
