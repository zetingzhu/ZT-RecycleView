package com.example.zzt.recycleview.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zeting
 * @date: 2023/12/26
 * 带有 AsyncListDiffer 适配器
 * submitList(it.map {
 * it.copy()
 * })
 */
public abstract class BaseRecyclerAdapterV3<D extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<D> {

    /**
     * 创建局部刷新对象
     */
    private AsyncListDiffer<D> mDiffer = null;

    /**
     * 用来判断 两个对象是否是相同的Item
     */
    public abstract boolean absAreItemsTheSame(@NonNull D oldItem, @NonNull D newItem);

    /**
     * 用来判断是否是一个对象的
     */
    public abstract boolean absAreContentsTheSame(@NonNull D oldItem, @NonNull D newItem);

    /**
     * 局部刷新机制
     * Bundle bundle = new Bundle();
     * bundle.putString("AAA","AAA");
     * return bundle;
     *
     * @param oldItem
     * @param newItem
     * @return
     */
    public abstract Object absChangePayload(@NonNull D oldItem, @NonNull D newItem);

    /**
     * 局部刷新
     * Bundle payload = (Bundle) payloads.get(0);//取出我们在getChangePayload（）方法返回的bundle
     *
     * @param holder
     * @param payloads
     * @return
     */
    public abstract void viewHolderPayloads(@NonNull RecyclerView.ViewHolder holder, @NonNull List payloads);


    /**
     * 刷新比较对象
     */
    DiffUtil.ItemCallback<D> DIFF_CALLBACK = new DiffUtil.ItemCallback<D>() {
        @Override
        public boolean areItemsTheSame(@NonNull D oldItem, @NonNull D newItem) {
            return absAreItemsTheSame(oldItem, newItem);
        }

        /**
         *  决定是否两个 item 的数据是相同的。只有当 areItemsTheSame() 返回true时会调用。
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return
         */
        @Override
        public boolean areContentsTheSame(@NonNull D oldItem, @NonNull D newItem) {
            return absAreContentsTheSame(oldItem, newItem);
        }

        /**
         * 当 areItemsTheSame() 返回 true ，并且 areContentsTheSame() 返回 false 时调用
         * @param oldItem
         * @param newItem
         * @return
         */
        @Nullable
        @Override
        public Object getChangePayload(@NonNull D oldItem, @NonNull D newItem) {
            return absChangePayload(oldItem, newItem);
        }
    };

    public BaseRecyclerAdapterV3(RecyclerView recyclerView, List<D> data) {
        mDiffer = new AsyncListDiffer<D>(this, DIFF_CALLBACK);
    }

    /**
     * 设置数据
     *
     * @param list List<ItemData>
     */
    public void submitList(List<D> list) {
        mDiffer.submitList(list);
    }

    /**
     * 设置数据
     *
     * @param list           List<ItemData>?
     * @param commitCallback Runnable
     */
    public void submitList(List<D> list, Runnable commitCallback) {
        mDiffer.submitList(list, commitCallback);
    }


    /**
     * 添加数据变动监听
     *
     * @param mLis
     */
    public void addDifferListListener(@NonNull AsyncListDiffer.ListListener<D> mLis) {
        mDiffer.addListListener(mLis);
    }


    /**
     * 返回一个可变集合 和 getDataList 有差异不可混用
     *
     * @return
     */
    public List<D> getListDataModify() {
        return new ArrayList<>(mDiffer.getCurrentList());
    }

    /**
     * 列表数量
     *
     * @return
     */
    public int getContentItemCount() {
        return mDiffer.getCurrentList().size();
    }

    /**
     * 得到数据源
     * <p>
     * 这个方法返回是个不可变集合，不能用来添加删除
     *
     * @return MutableList<ItemData>?
     */
    public List<D> getDataList() {
        return mDiffer.getCurrentList();
    }

}
