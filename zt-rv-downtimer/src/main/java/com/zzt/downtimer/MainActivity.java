package com.zzt.downtimer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        EdgeToEdge.enable(this);
//        EdgeToEdge.enable(this,
//                SystemBarStyle.auto(Color.YELLOW, Color.YELLOW),
//                SystemBarStyle.auto(Color.GREEN, Color.GREEN));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            Log.d(TAG, "系统状态栏高度 systemBars：" + systemBars);
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void initView() {
        rv_list = findViewById(R.id.rv_list);
        RecyclerListAdapter<ItemTitle, TitleViewHolder> adapter = new RecyclerListAdapter<ItemTitle, TitleViewHolder>() {

            @Override
            public TitleViewHolder onCreateViewHolder(ViewGroup parent) {
                return new TitleViewHolder(parent);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public void onViewRecycled(@NonNull TitleViewHolder holder) {
                super.onViewRecycled(holder);
                holder.onRecycled();
            }
        };

//        adapter.addViewType(0, new RecyclerListAdapter.ViewHolderFactory<RecyclerListAdapter.ViewHolder<?>>() {
//            @Override
//            public RecyclerListAdapter.ViewHolder<?> onCreateViewHolder(ViewGroup parent) {
//                return new TitleViewHolder(parent);
//            }
//        });

//        RecyclerListAdapter<ItemTitle ,TitleViewHolder> adapter = new RecyclerListAdapter() {
//            {
//                addViewType(0, new ViewHolderFactory<ViewHolder>() {
//                    @Override
//                    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//                        return new TitleViewHolder(parent);
//                    }
//                });
//
//            }
//
//            @Override
//            public int getItemViewType(int position) {
//                return 0;
//            }
//
//            @Override
//            public void onViewRecycled(@NonNull ViewHolder holder) {
//                super.onViewRecycled(holder);
//            }
//        };

        List<ItemTitle> mList = new ArrayList<>();
        long sysTime = System.currentTimeMillis();
        Random random = new Random();
        mList.add(new ItemTitle("标题1", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题2", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题3", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题4", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题5", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题6", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题7", sysTime + (random.nextInt(100) * 3000)));
        mList.add(new ItemTitle("标题8", sysTime + (random.nextInt(100) * 3000)));

        adapter.setItemList(mList);

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setAdapter(adapter);

    }
}