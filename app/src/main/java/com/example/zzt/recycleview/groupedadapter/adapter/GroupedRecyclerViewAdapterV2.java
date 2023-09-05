package com.example.zzt.recycleview.groupedadapter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.zzt.recycleview.R;
import com.example.zzt.recycleview.entity.BaseDiffEntity;
import com.example.zzt.recycleview.groupedadapter.holder.BaseViewHolder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通用的分组列表Adapter。通过它可以很方便的实现列表的分组效果。
 * 这个类提供了一系列的对列表的更新、删除和插入等操作的方法。
 * 使用者要使用这些方法的列表进行操作，而不要直接使用RecyclerView.Adapter的方法。
 * 因为当分组列表发生变化时，需要及时更新分组列表的组结构{@link GroupedRecyclerViewAdapterV2#mStructures}
 */
public abstract class GroupedRecyclerViewAdapterV2<T extends BaseDiffEntity, G extends BaseDiffEntity, C extends BaseDiffEntity> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupedRecyclerViewAdapterV2.class.getSimpleName();

    public static final int TYPE_HEADER = R.integer.type_header;
    public static final int TYPE_CHILD = R.integer.type_child;
    public static final int TYPE_EMPTY = R.integer.type_empty;

    private OnHeaderClickListener mOnHeaderClickListener;
    private OnChildClickListener mOnChildClickListener;
    private OnHeaderLongClickListener mOnHeaderLongClickListener;
    private OnChildLongClickListener mOnChildLongClickListener;

    protected Context mContext;
    // 刷新数据后组成新的数据结构
    protected CopyOnWriteArrayList<T> mStructures = new CopyOnWriteArrayList<>();


    DiffUtil.ItemCallback<T> DIFF_CALLBACK = new DiffUtil.ItemCallback<T>() {
        @Override
        public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            Log.d(TAG, "比较对象是否相同 oldItem：" + oldItem.toString() + ">" + newItem.toString());
            return oldItem.getGroupId().equals(newItem.getGroupId()) && oldItem.getChildId().equals(newItem.getChildId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.equals(newItem);
        }
    };
    protected AsyncListDiffer<T> mDiffer;

    /**
     * 设置数据
     */
    protected void notifyDataList(CopyOnWriteArrayList<G> mList) {
        mDiffer.submitList(startStructures(mList));
    }

    protected void submitList(CopyOnWriteArrayList<T> mList) {
        mDiffer.submitList(mList);
    }

    protected void clear() {
        mDiffer.submitList(null);
    }


    public GroupedRecyclerViewAdapterV2(Context context) {
        mContext = context;
        registerAdapterDataObserver(new GroupDataObserver());
        mDiffer = new AsyncListDiffer<T>(this, DIFF_CALLBACK);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        structureChanged();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        //处理StaggeredGridLayout，保证组头和组尾占满一行。
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    private void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (isEmptyPosition(position) || judgeType(position) == TYPE_HEADER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new BaseViewHolder(getEmptyView(parent));
        } else {
            View view = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false);
            return new BaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Log.w(TAG, "BindView >position:" + position + " payloads:" + payloads);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.w(TAG, "BindView >position:" + position);
        int type = judgeType(position);
        final int groupPosition = getGroupPositionForPosition(position);
        if (type == TYPE_HEADER) {
            if (mOnHeaderClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnHeaderClickListener != null) {
                            ViewParent parent = holder.itemView.getParent();
                            int gPosition = parent instanceof FrameLayout ? groupPosition : getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                mOnHeaderClickListener.onHeaderClick(GroupedRecyclerViewAdapterV2.this, (BaseViewHolder) holder, gPosition);
                            }
                        }
                    }
                });
            }

            if (mOnHeaderLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnHeaderLongClickListener != null) {
                            ViewParent parent = holder.itemView.getParent();
                            int gPosition = parent instanceof FrameLayout ? groupPosition : getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                return mOnHeaderLongClickListener.onHeaderLongClick(GroupedRecyclerViewAdapterV2.this, (BaseViewHolder) holder, gPosition);
                            }
                        }
                        return false;
                    }
                });
            }
            onBindHeaderViewHolder((BaseViewHolder) holder, groupPosition);
        } else if (type == TYPE_CHILD) {
            int childPosition = getChildPositionForPosition(groupPosition, position);
            if (mOnChildClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnChildClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            int cPosition = getChildPositionForPosition(gPosition, holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size() && cPosition >= 0 && cPosition < mStructures.get(gPosition).getChildrenCount()) {
                                mOnChildClickListener.onChildClick(GroupedRecyclerViewAdapterV2.this, (BaseViewHolder) holder, gPosition, cPosition);
                            }
                        }
                    }
                });
            }

            if (mOnChildLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnChildLongClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            int cPosition = getChildPositionForPosition(gPosition, holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size() && cPosition >= 0 && cPosition < mStructures.get(gPosition).getChildrenCount()) {
                                return mOnChildLongClickListener.onChildLongClick(GroupedRecyclerViewAdapterV2.this, (BaseViewHolder) holder, gPosition, cPosition);
                            }
                        }
                        return false;
                    }
                });
            }
            onBindChildViewHolder((BaseViewHolder) holder, groupPosition, childPosition);
        }
    }

    @Override
    public int getItemCount() {
        int count = count();
        if (count > 0) {
            return count;
        } else {
            return 0;
        }
    }

    public boolean isEmptyPosition(int position) {
        return position == 0 && count() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmptyPosition(position)) {
            // 空布局
            return TYPE_EMPTY;
        }
        try {
            return judgeType(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getItemViewType(position);
    }

    private int getLayoutId(int viewType) {
        if (viewType == TYPE_HEADER) {
            return getHeaderLayout(viewType);
        } else if (viewType == TYPE_CHILD) {
            return getChildLayout(viewType);
        }
        return 0;
    }

    private int count() {
        return mStructures == null ? 0 : mStructures.size();
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    public int judgeType(int position) {
        if (mStructures != null) {
            T t = mStructures.get(position);
            if (t != null) {
                if (t.getGroupId() >= 0 && t.getChildId() == -1) {
                    return TYPE_HEADER;
                } else if (t.getChildId() >= 0) {
                    return TYPE_CHILD;
                }
            }
        }
        return TYPE_EMPTY;
    }

    /**
     * 重置组结构列表
     */
    private void structureChanged() {

    }

    /**
     * 根据下标计算position所在的组（groupPosition）
     *
     * @param position 下标
     * @return 组下标 groupPosition
     */
    public int getGroupPositionForPosition(int position) {
        if (mStructures.size() > position) {
            T t = mStructures.get(position);
            if (t != null) {
                return t.getGroupId();
            }
        }
        return -1;
    }

    /**
     * 根据下标计算position在组中位置（childPosition）
     *
     * @param groupPosition 所在的组
     * @param position      下标
     * @return 子项下标 childPosition
     */
    public int getChildPositionForPosition(int groupPosition, int position) {
        if (mStructures.size() > position) {
            T t = mStructures.get(position);
            if (t != null) {
                return t.getChildId();
            }
        }
        return -1;
    }

    /**
     * 设置组头点击事件
     *
     * @param listener
     */
    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    /**
     * 设置子项长按事件
     *
     * @param listener
     */
    public void setOnChildLongClickListener(OnChildLongClickListener listener) {
        mOnChildLongClickListener = listener;
    }

    /**
     * 设置组头长按事件
     *
     * @param listener
     */
    public void setOnHeaderLongClickListener(OnHeaderLongClickListener listener) {
        mOnHeaderLongClickListener = listener;
    }

    /**
     * 设置子项点击事件
     *
     * @param listener
     */
    public void setOnChildClickListener(OnChildClickListener listener) {
        mOnChildClickListener = listener;
    }

    /**
     * 启动结构数据转换
     *
     * @param mList
     * @return
     */
    public abstract CopyOnWriteArrayList<T> startStructures(CopyOnWriteArrayList<G> mList);

    public abstract int getHeaderLayout(int viewType);

    public abstract int getChildLayout(int viewType);

    public abstract void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition);

    /**
     * 获取空布局
     *
     * @param parent
     * @return
     */
    public View getEmptyView(ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.group_adapter_default_empty_view, parent, false);
    }

    class GroupDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(GroupedRecyclerViewAdapterV2 adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnChildClickListener {
        void onChildClick(GroupedRecyclerViewAdapterV2 adapter, BaseViewHolder holder, int groupPosition, int childPosition);
    }

    public interface OnHeaderLongClickListener {
        boolean onHeaderLongClick(GroupedRecyclerViewAdapterV2 adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnChildLongClickListener {
        boolean onChildLongClick(GroupedRecyclerViewAdapterV2 adapter, BaseViewHolder holder, int groupPosition, int childPosition);
    }
}