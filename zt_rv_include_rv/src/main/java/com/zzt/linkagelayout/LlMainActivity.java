package com.zzt.linkagelayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.zzt.zt_rv_include_rv.R;

public class LlMainActivity extends AppCompatActivity {


    private List<Entity> mEntities = new ArrayList<>();
    private List<String> rightMoveDatas = new ArrayList<>();
    private List<String> topTabs = new ArrayList<>();

    RecyclerView rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt_activity_main);
        rv_list = findViewById(R.id.rv_list);
        //处理顶部标题部分
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //处理内容部分
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setHasFixedSize(true);
        final ContentAdapter contentAdapter = new ContentAdapter(this, null);
        rv_list.setAdapter(contentAdapter);

        rv_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 30; i++) {
                    Entity entity = new Entity();
                    entity.setLeftTitle("贵州茅台" + i);
                    rightMoveDatas.clear();
                    for (int j = 0; j < 50; j++) {
                        rightMoveDatas.add("年份" + j);
                    }
                    entity.setRightDatas(rightMoveDatas);
                    mEntities.add(entity);
                }
                contentAdapter.setDatas(mEntities);
                Toast.makeText(LlMainActivity.this, "加载完毕,加载了30条数据", Toast.LENGTH_SHORT).show();
            }
        }, 1500);


    }
}
