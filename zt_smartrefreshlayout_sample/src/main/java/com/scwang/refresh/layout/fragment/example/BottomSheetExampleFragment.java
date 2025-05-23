package com.scwang.refresh.layout.fragment.example;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

/**
 * 使用示例-BottomSheet
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetExampleFragment extends Fragment {

    private BaseRecyclerAdapter<Void> mAdapter;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        root = onCreateView(LayoutInflater.from(getContext()), null, null);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()).setSpinnerStyle(SpinnerStyle.FixedBehind).setPrimaryColorId(R.color.colorPrimary).setAccentColorId(android.R.color.white));
        refreshLayout.setOnLoadMoreListener((OnLoadMoreListener) refreshLayout1 -> refreshLayout1.getLayout().postDelayed((Runnable) () -> {
            mAdapter.loadMore(initData());
            refreshLayout1.finishLoadMore();
        },2000));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), VERTICAL));

        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(initData(),simple_list_item_2) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
            }
        });

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setOnDismissListener(dialog1 -> requireActivity().finish());
        dialog.setContentView(root);
        dialog.setCancelable(false);
        dialog.show();
    }

    private Collection<Void> initData() {
        return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
}
