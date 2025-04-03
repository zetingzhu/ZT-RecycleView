package com.zzt.downtimer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.downv2.RvListActV2;
import com.zzt.downv3.RvListActV3;
import com.zzt.entiy.ItemTitle;
import com.zzt.util.DownTimeUtils;

import java.util.ArrayList;
import java.util.List;
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


        for (int i = 0; i < 20; i++) {
            mList.add(new ItemTitle("标题 > " + i, sysTime + (random.nextInt(100) * 3000)));
        }


        adapter.setItemList(mList);


        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setAdapter(adapter);


        TextView tv_list_v2 = findViewById(R.id.tv_list_v2);
        tv_list_v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RvListActV2.start(v.getContext());
            }
        });

        findViewById(R.id.tv_list_v3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RvListActV3.start(v.getContext());
            }
        });
    }
}