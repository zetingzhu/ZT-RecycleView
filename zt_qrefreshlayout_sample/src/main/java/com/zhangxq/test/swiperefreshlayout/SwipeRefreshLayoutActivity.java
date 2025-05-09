package com.zhangxq.test.swiperefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzt.zt_qrefreshlayout_sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoqi on 2019/4/19.
 */

public class SwipeRefreshLayoutActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private List<String> datas = new ArrayList<>();
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiperefreshlayout);
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        adapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        down();
    }

    private void down() {
        datas.clear();
        for (int i = 0; i < 20; i++) {
            datas.add("测试数据" + i);
        }
        adapter.notifyDataSetChanged();
    }

    private void up() {
        int size = datas.size();
        for (int i = size; i < size + 10; i++) {
            datas.add("测试数据" + i);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                down();
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(SwipeRefreshLayoutActivity.this).inflate(R.layout.view_list_item, null));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.tvTest.setText(datas.get(position));
            if (position % 2 == 0) {
                holder.tvTest.setBackgroundColor(0xffabcdef);
            } else {
                holder.tvTest.setBackgroundColor(0xffffffff);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tvTest;

        public ItemHolder(View itemView) {
            super(itemView);
            tvTest = itemView.findViewById(R.id.tvTest);
        }
    }
}
