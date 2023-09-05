package com.example.zzt.recycleview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzt.recycleview.R;
import com.example.zzt.recycleview.entity.BaseDiffEntity;
import com.example.zzt.recycleview.entity.ChildEntity;
import com.example.zzt.recycleview.entity.ExpandableChildEntityV2;
import com.example.zzt.recycleview.entity.ExpandableGroupEntityV2;
import com.example.zzt.recycleview.entity.ExpandableGroupEntityV2;
import com.example.zzt.recycleview.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.example.zzt.recycleview.groupedadapter.adapter.GroupedRecyclerViewAdapterV2;
import com.example.zzt.recycleview.groupedadapter.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 可展开收起的Adapter。他跟普通的{@link GroupedListAdapter}基本是一样的。
 * 它只是利用了{@link GroupedRecyclerViewAdapter}的
 * 删除一组里的所有子项{@link GroupedRecyclerViewAdapter#notifyChildrenRemoved(int)}} 和
 * 插入一组里的所有子项{@link GroupedRecyclerViewAdapter#notifyChildrenInserted(int)}
 * 两个方法达到列表的展开和收起的效果。
 * 这种列表类似于{@link ExpandableListView}的效果。
 * 这里我把列表的组尾去掉是为了效果上更像ExpandableListView。
 */
public class ExpandableAdapterV2 extends GroupedRecyclerViewAdapterV2<BaseDiffEntity, ExpandableGroupEntityV2, ExpandableChildEntityV2> {
    private static final String TAG = ExpandableAdapterV2.class.getSimpleName();

    private CopyOnWriteArrayList<ExpandableGroupEntityV2> mListData;

    public ExpandableAdapterV2(Context context) {
        super(context);
    }

    /**
     * 设置数据
     */
    public void submitList(List<ExpandableGroupEntityV2> mGroups) {
        super.notifyDataList(new CopyOnWriteArrayList<>(mGroups));
    }

    @Override
    public CopyOnWriteArrayList<BaseDiffEntity> startStructures(CopyOnWriteArrayList<ExpandableGroupEntityV2> mList) {
        this.mListData = mList;
        if (mList != null) {
            mStructures = new CopyOnWriteArrayList<>();
            for (int i = 0; i < mList.size(); i++) {
                ExpandableGroupEntityV2 entityV2 = mList.get(i);
                int childrenCount = 0;
                if (entityV2.getChildren() != null) {
                    childrenCount = entityV2.getChildren().size();
                }
                mStructures.add(new BaseDiffEntity(i, -1, childrenCount, entityV2.isExpand()));
                if (entityV2.isExpand() && entityV2.getChildren() != null) {
                    for (int k = 0; k < childrenCount; k++) {
                        mStructures.add(new BaseDiffEntity(i, k));
                    }
                }
            }
            return mStructures;
        }
        return new CopyOnWriteArrayList<>();
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_expandable_header;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.layout_item_v1;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        ExpandableGroupEntityV2 entity = mListData.get(groupPosition);
        Log.d(TAG, "头信息 groupPosition:" + groupPosition + " entity:" + entity.toString());
        holder.setText(R.id.tv_expandable_header, entity.getHeader());
        ImageView ivState = holder.get(R.id.iv_state);
        if (entity.isExpand()) {
            ivState.setRotation(90);
        } else {
            ivState.setRotation(0);
        }
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        ChildEntity entity = mListData.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tv_title, entity.getChild());

        TextView tv_msg = holder.get(R.id.tv_msg);
        tv_msg.setText("点击");
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "点击了：" + entity.getChild(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断当前组是否展开
     *
     * @param groupPosition
     * @return
     */
    public boolean isExpand(int groupPosition) {
        ExpandableGroupEntityV2 entity = mListData.get(groupPosition);
        return entity.isExpand();
    }

    /**
     * 展开一个组
     *
     * @param groupPosition
     */
    public void expandGroup(int groupPosition) {
        ExpandableGroupEntityV2 entity = mListData.get(groupPosition);
        entity.setExpand(true);
        CopyOnWriteArrayList<BaseDiffEntity> baseDiffEntities = startStructures(mListData);
        submitList(baseDiffEntities);
    }

    /**
     * 收起一个组
     *
     * @param groupPosition
     */
    public void collapseGroup(int groupPosition) {
        ExpandableGroupEntityV2 entity = mListData.get(groupPosition);
        entity.setExpand(false);
        CopyOnWriteArrayList<BaseDiffEntity> baseDiffEntities = startStructures(mListData);
        submitList(baseDiffEntities);
    }

}