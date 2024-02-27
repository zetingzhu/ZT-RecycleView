package com.example.zzt.recycleview.refresh.v1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by lin on 2018/1/10.
 */
public class PullToRefreshRecyclerViewV2 extends PullToRefreshBaseV2<RecyclerView> {

    private RecyclerView rv;
    /**
     * 用于滑到底部自动加载的Footer
     */
    private LoadingLayout mLoadMoreFooterLayout;

    /**
     * 设置一屏数据禁止上拉
     */
    private boolean isOnlyPullUpUnable = false;

    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshRecyclerViewV2(Context context) {
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshRecyclerViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PullToRefreshRecyclerViewV2(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
        setPullLoadEnabled(false);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        rv = new RecyclerView(context);
        rv.setLayoutManager(new LinearLayoutManager(context));
//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("zzz", "加载状态： isScr:" + isScrollLoadEnabled() + " hasMore:" + hasMoreData() + " newState：" + newState + " pullUp:" + isReadyForPullUp());
//                if (isScrollLoadEnabled() && hasMoreData()) {
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        if (isReadyForPullUp()) {
//                            startLoading();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
        return rv;
    }

    public RecyclerView getRv() {
        return rv;
    }

    /**
     * 设置是否有更多数据的标志
     *
     * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
     */
    public void setHasMoreData(boolean hasMoreData) {
        if (!hasMoreData) {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }

            LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
            if (null != footerLoadingLayout) {
                footerLoadingLayout.setState(ILoadingLayout.State.NO_MORE_DATA);
            }
        }
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected void startLoading() {
        super.startLoading();

        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.REFRESHING);
        }
    }

    @Override
    public void onPullUpRefreshComplete() {
        super.onPullUpRefreshComplete();

        if (null != mLoadMoreFooterLayout) {
            mLoadMoreFooterLayout.setState(ILoadingLayout.State.RESET);
        }
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollLoadEnabled(scrollLoadEnabled);
        if (isScrollLoadEnabled() == scrollLoadEnabled || rv.getAdapter() == null) {
            return;
        }
        if (scrollLoadEnabled) {
            // 设置Footer
            if (null == mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout = new FooterLoadingLayout(getContext());
            }
            mLoadMoreFooterLayout.show(true);
        } else {
            if (null != mLoadMoreFooterLayout) {
                mLoadMoreFooterLayout.show(false);
            }
        }
    }

    @Override
    public LoadingLayout getFooterLoadingLayout() {
        if (isScrollLoadEnabled()) {
            return mLoadMoreFooterLayout;
        }

        return super.getFooterLoadingLayout();
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context,
                                                      AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }

    /**
     * 表示是否还有更多数据
     *
     * @return true表示还有更多数据
     */
    private boolean hasMoreData() {
        if ((null != mLoadMoreFooterLayout)
                && (mLoadMoreFooterLayout.getState() == ILoadingLayout.State.NO_MORE_DATA)) {
            return false;
        }

        return true;
    }

    /**
     * 判断第一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {
        return !ViewCompat.canScrollVertically(rv, -1);
    }

    /**
     * 判断最后一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        if (isOnlyPullUpUnable && isOnlyScreenData(rv)) {
            return false;
        }
        return !ViewCompat.canScrollVertically(rv, 1);
    }


    @Override
    protected void onStateChanged(ILoadingLayout.State state, boolean isPullDown) {
        super.onStateChanged(state, isPullDown);
        if (pullStateChangedListener != null) {
            pullStateChangedListener.onStateChanged(state, isPullDown);
        }
    }

    PullStateChangedListener pullStateChangedListener;

    public void addPullStateChangedListener(PullStateChangedListener pullStateChangedListener) {
        this.pullStateChangedListener = pullStateChangedListener;
    }

    public interface PullStateChangedListener {
        void onStateChanged(ILoadingLayout.State state, boolean isPullDown);
    }

    /**
     * 判断是否只有一屏数据
     *
     * @param recyclerView
     * @return
     */
    private boolean isOnlyScreenData(RecyclerView recyclerView) {
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisiblePosition >= (totalItemCount - 1) && firstVisiblePosition == 0) {
                    // 第0条和最后一条都显示出来，说明只有一屏数据
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOnlyPullUpUnable() {
        return isOnlyPullUpUnable;
    }

    public void setOnlyPullUpUnable(boolean onlyPullUpUnable) {
        isOnlyPullUpUnable = onlyPullUpUnable;
    }
}
