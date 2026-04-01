package com.example.zzt.recycleview.groupedadapter.sticky;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


/**
 * @author: zeting
 * @date: 2023/8/30
 * 头部吸顶布局。只要用 RVStickyHeaderLayoutV2 包裹{@link RecyclerView},
 * 适配器实现 {@link IStickyHeaderProvider} 接口就可以实现列表头部吸顶功能。
 * RVStickyHeaderMultiLayout 只能包裹 RecyclerView，而且只能包裹一个 RecyclerView。
 */
public class RVStickyHeaderLayoutV2<VH extends RecyclerView.ViewHolder> extends FrameLayout {

    private RecyclerView mRecyclerView;

    //吸顶容器，用于承载吸顶布局。
    private FrameLayout mStickyLayout;

    //保存吸顶布局的缓存池。它以列表组头的viewType为key,ViewHolder为value对吸顶布局进行保存和回收复用。
    private final SparseArray<VH> mStickyViews = new SparseArray<>();

    //用于在吸顶布局中保存viewType的key。
    private static final int VIEW_TAG_TYPE = -101;

    //用于在吸顶布局中保存ViewHolder的key。
    private static final int VIEW_TAG_HOLDER = -102;

    // 给 View 设置一个当前列表位置
    public static final int VIEW_TAG_POSITION = -999;

    //记录当前吸顶的组。
    private int mCurrentStickyGroup = -1;

    //是否吸顶。
    private boolean isSticky = true;

    //缓存的 adapter 引用，避免每次 onScrolled 都 getAdapter + instanceof
    private IStickyHeaderProvider<VH> mCachedAdapter;
    private RecyclerView.Adapter<VH> mCachedRawAdapter;

    //adapter 变更监听，用于在 adapter 切换时重新注册 observer
    private RecyclerView.AdapterDataObserver mDataObserver;

    // 缓存 StaggeredGridLayoutManager 的位置数组，避免频繁创建
    private int[] mStaggeredPositions;

    //复用的延迟更新 Runnable，避免每次 new + 支持 removeCallbacks 去重
    private final Runnable mDelayedUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateStickyView(true);
        }
    };

    private OnStickyChangedListener mListener;

    public RVStickyHeaderLayoutV2(@NonNull Context context) {
        super(context);
    }

    public RVStickyHeaderLayoutV2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RVStickyHeaderLayoutV2(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0 || !(child instanceof RecyclerView)) {
            //外界只能向StickyHeaderLayout添加一个RecyclerView,而且只能添加RecyclerView。
            throw new IllegalArgumentException("StickyHeaderLayout can host only one direct child --> RecyclerView");
        }
        super.addView(child, index, params);
        mRecyclerView = (RecyclerView) child;

        //创建吸顶容器
        mStickyLayout = new FrameLayout(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mStickyLayout.setLayoutParams(lp);
        super.addView(mStickyLayout, 1, lp);

        //添加滚动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isSticky) {
                    updateStickyView(false);
                }
            }
        });
    }

    /**
     * 获取当前 RecyclerView 的 adapter（如果实现了 IStickyHeaderProvider）
     */
    private IStickyHeaderProvider<?> getStickyAdapter() {
        RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
        if (adapter instanceof IStickyHeaderProvider) {
            return (IStickyHeaderProvider<?>) adapter;
        }
        return null;
    }

    /**
     * 绑定新的 adapter：反注册旧的 observer，注册新的
     */
    @SuppressWarnings("unchecked")
    private void attachAdapter(IStickyHeaderProvider<VH> newAdapter, RecyclerView.Adapter<VH> rawAdapter) {
        // 如果是同一个 adapter，不进行重复注册
        if (mCachedAdapter == newAdapter) {
            return;
        }

        //反注册旧 adapter 的 observer
        if (mCachedRawAdapter != null && mDataObserver != null) {
            try {
                mCachedRawAdapter.unregisterAdapterDataObserver(mDataObserver);
            } catch (IllegalStateException ignored) {
                //observer 可能已经被移除
            }
        }

        mCachedAdapter = newAdapter;
        mCachedRawAdapter = rawAdapter;

        if (mCachedRawAdapter != null) {
            //创建并注册 observer
            if (mDataObserver == null) {
                mDataObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        updateStickyViewDelayed();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        updateStickyViewDelayed();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        updateStickyViewDelayed();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        updateStickyViewDelayed();
                    }
                };
            }
            mCachedRawAdapter.registerAdapterDataObserver(mDataObserver);
            // 绑定新 adapter 后立即尝试更新一次
            updateStickyViewDelayed();
        } else {
            recycle();
        }
    }

    /**
     * 强制更新吸顶布局。
     */
    public void updateStickyView() {
        updateStickyView(true);
    }

    /**
     * 更新吸顶布局。
     *
     * @param imperative 是否强制更新。
     */
    @SuppressWarnings("unchecked")
    private void updateStickyView(boolean imperative) {
        // 动态检测 Adapter 变化
        RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
        if (adapter != mCachedRawAdapter) {
            if (adapter instanceof IStickyHeaderProvider) {
                attachAdapter((IStickyHeaderProvider<VH>) adapter, (RecyclerView.Adapter<VH>) adapter);
            } else {
                attachAdapter(null, null);
                return;
            }
        }

        IStickyHeaderProvider<VH> gAdapter = mCachedAdapter;
        if (gAdapter == null) {
            return;
        }

        //记录旧的吸顶组
        int oldGroupIndex = mCurrentStickyGroup;

        //获取列表显示的第一个项。
        int firstVisibleItem = getFirstVisibleItem();

        //精确判断是否在列表顶部：第一个 item 可见且完全没有滚出屏幕
        if (isAtTop(firstVisibleItem)) {
            recycle();
            mStickyLayout.setVisibility(GONE);
            if (mListener != null && oldGroupIndex != mCurrentStickyGroup) {
                mListener.onStickyChanged(oldGroupIndex, mCurrentStickyGroup);
            }
            return;
        }

        // 通过 adapter 将 firstVisibleItem 映射到所属分组的 Header position
        int headerPos = gAdapter.getStickyHeaderPosition(firstVisibleItem);
        boolean stickyHeaderEnabled = headerPos != -1;

        //如果当前吸顶的组头不是我们要吸顶的组头，就更新吸顶布局。避免频繁的更新吸顶布局。
        if (imperative || mCurrentStickyGroup != headerPos) {
            mCurrentStickyGroup = headerPos;

            if (headerPos != -1) {
                //获取吸顶布局的viewType
                int viewType = mCachedRawAdapter.getItemViewType(headerPos);

                //如果当前的吸顶布局的类型和我们需要的一样，就直接获取它的ViewHolder，否则就回收。
                VH holder = recycleStickyView(viewType);

                //标志holder是否是从当前吸顶布局取出来的。
                boolean fromCurrentLayout = holder != null;

                if (holder == null) {
                    //从缓存池中获取吸顶布局。
                    holder = getStickyViewByType(viewType);
                }

                if (holder == null) {
                    //如果没有从缓存池中获取到吸顶布局，则通过 RecyclerViewAdapter 创建。
                    holder = mCachedRawAdapter.onCreateViewHolder(mStickyLayout, viewType);
                    holder.itemView.setTag(VIEW_TAG_TYPE, viewType);
                    holder.itemView.setTag(VIEW_TAG_HOLDER, holder);
                }

                //通过 RecyclerViewAdapter 更新吸顶布局的数据。
                gAdapter.onBindViewHolderStickyHead(holder, headerPos, true);

                //如果holder不是从当前吸顶布局取出来的，就需要把吸顶布局添加到容器里。
                if (!fromCurrentLayout) {
                    mStickyLayout.addView(holder.itemView);
                }
            } else {
                //回收旧的吸顶布局。
                recycle();
            }
        }

        if (stickyHeaderEnabled) {
            mStickyLayout.setVisibility(VISIBLE);
            //处理第一次打开时，吸顶布局已经添加到StickyLayout，但StickyLayout的高依然为0的情况。
            if (mStickyLayout.getChildCount() > 0 && mStickyLayout.getHeight() == 0) {
                mStickyLayout.requestLayout();
            }
        } else {
            mStickyLayout.setVisibility(GONE);
        }

        //设置mStickyLayout的Y偏移量
        float offSet = calculateOffset(gAdapter, headerPos, stickyHeaderEnabled);
        mStickyLayout.setTranslationY(offSet);

        if (mListener != null && oldGroupIndex != mCurrentStickyGroup) {
            mListener.onStickyChanged(oldGroupIndex, mCurrentStickyGroup);
        }
    }

    /**
     * 精确判断列表是否滑动到了顶部。
     *
     * @param firstVisibleItem 当前第一个可见 item 的 position
     * @return true 表示列表在顶部，不需要吸顶
     */
    private boolean isAtTop(int firstVisibleItem) {
        if (firstVisibleItem > 0) {
            return false;
        }
        // 使用 canScrollVertically 快速判断
        if (mRecyclerView.canScrollVertically(-1)) {
            // 如果能向上滚动，说明不在绝对顶部
            // 但对于 position 0，我们还需要看它是否完全显示
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            if (lm != null) {
                View firstView = lm.findViewByPosition(0);
                if (firstView != null) {
                    return firstView.getTop() >= mRecyclerView.getPaddingTop();
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 延迟更新吸顶布局，自动去重。
     * 延迟时间缩短为 16ms（约 1 帧），提供更及时的反馈。
     */
    private void updateStickyViewDelayed() {
        removeCallbacks(mDelayedUpdateRunnable);
        postDelayed(mDelayedUpdateRunnable, 16);
    }

    /**
     * 判断是否需要先回收吸顶布局，如果要回收，则回收吸顶布局并返回null。
     */
    @SuppressWarnings("unchecked")
    private VH recycleStickyView(int viewType) {
        if (mStickyLayout.getChildCount() > 0) {
            View view = mStickyLayout.getChildAt(0);
            Object typeTag = view.getTag(VIEW_TAG_TYPE);
            if (typeTag instanceof Integer && (int) typeTag == viewType) {
                return (VH) view.getTag(VIEW_TAG_HOLDER);
            } else {
                recycle();
            }
        }
        return null;
    }

    /**
     * 回收并移除吸顶布局
     */
    @SuppressWarnings("unchecked")
    private void recycle() {
        mCurrentStickyGroup = -1;
        if (mStickyLayout.getChildCount() > 0) {
            View view = mStickyLayout.getChildAt(0);
            Object typeTag = view.getTag(VIEW_TAG_TYPE);
            Object holderTag = view.getTag(VIEW_TAG_HOLDER);
            if (typeTag instanceof Integer && holderTag != null) {
                mStickyViews.put((int) typeTag, (VH) holderTag);
            }
            mStickyLayout.removeAllViews();
        }
    }

    /**
     * 从缓存池中获取吸顶布局
     */
    private VH getStickyViewByType(int viewType) {
        return mStickyViews.get(viewType);
    }

    /**
     * 计算StickyLayout的偏移量。
     */
    private float calculateOffset(IStickyHeaderProvider<?> gAdapter, int currentHeaderPos, boolean stickyHeaderEnabled) {
        if (!stickyHeaderEnabled || currentHeaderPos == -1) {
            return 0;
        }
        // 通过 adapter 找到下一个分组的 Header position
        int nextHeaderPos = gAdapter.getNextStickyHeaderPosition(currentHeaderPos);
        if (nextHeaderPos == -1) {
            return 0;
        }

        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            //获取下一个分组 Header 的 itemView
            View view = layoutManager.findViewByPosition(nextHeaderPos);
            if (view != null) {
                float off = view.getY() - mStickyLayout.getHeight();
                if (off < 0) {
                    return off;
                }
            }
        }
        return 0;
    }

    /**
     * 获取当前第一个显示的item。
     */
    private int getFirstVisibleItem() {
        int firstVisibleItem = -1;
        RecyclerView.LayoutManager layout = mRecyclerView.getLayoutManager();
        if (layout != null) {
            if (layout instanceof LinearLayoutManager) {
                // GridLayoutManager is also a LinearLayoutManager
                firstVisibleItem = ((LinearLayoutManager) layout).findFirstVisibleItemPosition();
            } else if (layout instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager sgl = (StaggeredGridLayoutManager) layout;
                int spanCount = sgl.getSpanCount();
                if (mStaggeredPositions == null || mStaggeredPositions.length != spanCount) {
                    mStaggeredPositions = new int[spanCount];
                }
                sgl.findFirstVisibleItemPositions(mStaggeredPositions);
                firstVisibleItem = getMin(mStaggeredPositions);
            }
        }
        return firstVisibleItem;
    }

    private int getMin(int[] arr) {
        if (arr == null || arr.length == 0) return -1;
        int min = arr[0];
        for (int x = 1; x < arr.length; x++) {
            if (arr[x] < min) min = arr[x];
        }
        return min;
    }

    /**
     * 是否吸顶
     */
    public boolean isSticky() {
        return isSticky;
    }

    /**
     * 设置是否吸顶。
     */
    public void setSticky(boolean sticky) {
        if (isSticky != sticky) {
            isSticky = sticky;
            if (mStickyLayout != null) {
                if (isSticky) {
                    mStickyLayout.setVisibility(VISIBLE);
                    updateStickyView(false);
                } else {
                    recycle();
                    mStickyLayout.setVisibility(GONE);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //清理：移除延迟回调，反注册 observer
        removeCallbacks(mDelayedUpdateRunnable);
        if (mCachedRawAdapter != null && mDataObserver != null) {
            try {
                mCachedRawAdapter.unregisterAdapterDataObserver(mDataObserver);
            } catch (Exception ignored) {
            }
        }
        mCachedAdapter = null;
        mCachedRawAdapter = null;
        mDataObserver = null;
    }

    /**
     * 监听吸顶项改变
     */
    public void setOnStickyChangedListener(OnStickyChangedListener l) {
        mListener = l;
    }

    public interface OnStickyChangedListener {
        /**
         * @param oldGroupIndex 旧的吸顶组下标，-1表示没有吸顶
         * @param newGroupIndex 新的吸顶组下标，-1表示没有吸顶
         */
        void onStickyChanged(int oldGroupIndex, int newGroupIndex);
    }
}
