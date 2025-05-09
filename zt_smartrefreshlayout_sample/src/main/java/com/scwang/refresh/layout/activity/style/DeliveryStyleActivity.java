package com.scwang.refresh.layout.activity.style;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DeliveryStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        ThemeDefault("默认主题", R.string.item_style_theme_default_abstract),
        ThemeOrange("橙色主题", R.string.item_style_theme_orange_abstract),
        ThemeRed("红色主题", R.string.item_style_theme_red_abstract),
        ThemeGreen("绿色主题", R.string.item_style_theme_green_abstract),
        ThemeBlue("蓝色主题", R.string.item_style_theme_blue_abstract),
        ;
        public final String remark;
        public final int nameId;
        Item(String remark, @StringRes int nameId) {
            this.remark = remark;
            this.nameId = nameId;
        }
    }

    private Toolbar mToolbar;
    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_delivery);
        getWindow().setStatusBarColor(0xfff0f0f0);
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mRefreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = false;
            mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }

        View view = findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            List<Item> items = new ArrayList<>();
            items.addAll(Arrays.asList(Item.values()));
            items.addAll(Arrays.asList(Item.values()));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(items, R.layout.item_style_delivery,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case ThemeDefault:
                mToolbar.setBackgroundResource(android.R.color.white);
                mToolbar.setTitleTextColor(0xffbbbbbb);
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray_24dp);
                mRefreshLayout.setPrimaryColors(0xfff0f0f0, 0xffffffff);
                getWindow().setStatusBarColor(0xfff0f0f0);
                if (Build.VERSION.SDK_INT >= 23) {
                    Window window = getWindow();
                    int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    window.getDecorView().setSystemUiVisibility(systemUiVisibility);
                }
                break;
            case ThemeBlue:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                break;
            case ThemeGreen:
                setThemeColor(android.R.color.holo_green_light, android.R.color.holo_green_dark);
                break;
            case ThemeRed:
                setThemeColor(android.R.color.holo_red_light, android.R.color.holo_red_dark);
                break;
            case ThemeOrange:
                setThemeColor(android.R.color.holo_orange_light, android.R.color.holo_orange_dark);
                break;
        }
        mRefreshLayout.autoRefresh();
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
        mToolbar.setBackgroundResource(colorPrimary);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }
}
