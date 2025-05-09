package com.scwang.refresh.layout.fragment.example;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static android.R.layout.simple_list_item_2;

/**
 * 使用示例-嵌套滚动-整体
 * A simple {@link Fragment} subclass.
 */
public class NestedScrollExampleFragmentIntegral extends Fragment implements AdapterView.OnItemClickListener, OnRefreshLoadMoreListener {

    private ViewPager2 mViewPager;
    private SmartPagerAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example_nestedscroll_integral, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());

//        Banner banner = root.findViewById(R.id.banner);
//        banner.setImageLoader(new BannerImageLoader());
//        banner.setImages(Arrays.asList(image_weibo_home_1, image_weibo_home_2));
//        banner.start();

        mViewPager = root.findViewById(R.id.viewPager);
        mViewPager.setAdapter(mAdapter = new SmartPagerAdapter(this));

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(this);

        TextView textView = root.findViewById(R.id.target);
        textView.setOnClickListener(v -> Toast.makeText(getContext(), "点击测试", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onRefresh(refreshLayout);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onLoadMore(refreshLayout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

//    private static class BannerImageLoader extends ImageLoader {
//        @Override
//        public void displayImage(Context context, Object path, ImageView imageView) {
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource((Integer)path);
//        }
//    }


    public static class SmartPagerAdapter extends FragmentStateAdapter {

        private final SmartFragment[] fragments;

        SmartPagerAdapter(Fragment fm) {
            super(fm);
            this.fragments = new SmartFragment[]{
                    new SmartFragment(), new SmartFragment()
            };
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }

    public static class SmartFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private BaseRecyclerAdapter<NestedScrollExampleFragment.Item> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return mRecyclerView = new RecyclerView(inflater.getContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<NestedScrollExampleFragment.Item>(buildItems(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, NestedScrollExampleFragment.Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.name);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        private Collection<NestedScrollExampleFragment.Item> buildItems() {
            List<NestedScrollExampleFragment.Item> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                items.addAll(Arrays.asList(NestedScrollExampleFragment.Item.values()));
            }
            return items;
        }


        public void onRefresh(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.refresh(buildItems());
                refreshLayout.finishRefresh();
                refreshLayout.resetNoMoreData();
            }, 2000);
        }

        public void onLoadMore(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.loadMore(buildItems());
                if (mAdapter.getItemCount() > 60) {
                    Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                } else {
                    refreshLayout.finishLoadMore();
                }
            }, 2000);
        }
    }
}
