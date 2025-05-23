package com.scwang.refresh.layout.activity.style;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.header.StoreHouseHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class StoreHouseStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        ShowSymbol("显示商标", R.string.item_style_store_house_brand),
        ShowIcon("显示图标", R.string.item_style_store_house_icon),
        ShowChinese("显示中文", R.string.item_style_store_house_chinese),
        ShowEnglish("显示英文", R.string.item_style_store_house_english),
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
        setContentView(R.layout.activity_style_storehouse);

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
            recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            List<Item> items = new ArrayList<>();
            items.addAll(Arrays.asList(Item.values()));
            items.addAll(Arrays.asList(Item.values()));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(items, simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.nameId);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case ShowChinese: {
                RefreshHeader refreshHeader = mRefreshLayout.getRefreshHeader();
                if (refreshHeader instanceof StoreHouseHeader) {
                    ((StoreHouseHeader) refreshHeader).initWithPointList(getPointList());
                }
                break;
            }
            case ShowEnglish: {
                RefreshHeader refreshHeader = mRefreshLayout.getRefreshHeader();
                if (refreshHeader instanceof StoreHouseHeader) {
                    ((StoreHouseHeader) refreshHeader).initWithString("SmartRefresh");
                }
                break;
            }
            case ShowIcon: {
                RefreshHeader refreshHeader = mRefreshLayout.getRefreshHeader();
                if (refreshHeader instanceof StoreHouseHeader) {
                    ((StoreHouseHeader) refreshHeader).initWithStringArray(R.array.storehouse);
                }
                break;
            }
            case ShowSymbol: {
                RefreshHeader refreshHeader = mRefreshLayout.getRefreshHeader();
                if (refreshHeader instanceof StoreHouseHeader) {
                    ((StoreHouseHeader) refreshHeader).initWithStringArray(R.array.akta);
                }
                break;
            }
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
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
    }

    private List<float[]> getPointList() {
        // this point is taken from https://github.com/cloay/CRefreshLayout
        List<Point> startPoints = new ArrayList<>();
        startPoints.add(new Point(240, 80));
        startPoints.add(new Point(270, 80));
        startPoints.add(new Point(265, 103));
        startPoints.add(new Point(255, 65));
        startPoints.add(new Point(275, 80));
        startPoints.add(new Point(275, 80));
        startPoints.add(new Point(302, 80));
        startPoints.add(new Point(275, 107));

        startPoints.add(new Point(320, 70));
        startPoints.add(new Point(313, 80));
        startPoints.add(new Point(330, 63));
        startPoints.add(new Point(315, 87));
        startPoints.add(new Point(330, 80));
        startPoints.add(new Point(315, 100));
        startPoints.add(new Point(330, 90));
        startPoints.add(new Point(315, 110));
        startPoints.add(new Point(345, 65));
        startPoints.add(new Point(357, 67));
        startPoints.add(new Point(363, 103));

        startPoints.add(new Point(375, 80));
        startPoints.add(new Point(375, 80));
        startPoints.add(new Point(425, 80));
        startPoints.add(new Point(380, 95));
        startPoints.add(new Point(400, 63));

        List<Point> endPoints = new ArrayList<>();
        endPoints.add(new Point(270, 80));
        endPoints.add(new Point(270, 110));
        endPoints.add(new Point(270, 110));
        endPoints.add(new Point(250, 110));
        endPoints.add(new Point(275, 107));
        endPoints.add(new Point(302, 80));
        endPoints.add(new Point(302, 107));
        endPoints.add(new Point(302, 107));

        endPoints.add(new Point(340, 70));
        endPoints.add(new Point(360, 80));
        endPoints.add(new Point(330, 80));
        endPoints.add(new Point(340, 87));
        endPoints.add(new Point(315, 100));
        endPoints.add(new Point(345, 98));
        endPoints.add(new Point(330, 120));
        endPoints.add(new Point(345, 108));
        endPoints.add(new Point(360, 120));
        endPoints.add(new Point(363, 75));
        endPoints.add(new Point(345, 117));

        endPoints.add(new Point(380, 95));
        endPoints.add(new Point(425, 80));
        endPoints.add(new Point(420, 95));
        endPoints.add(new Point(420, 95));
        endPoints.add(new Point(400, 120));
        List<float[]> list = new ArrayList<>();

        int offsetX = Integer.MAX_VALUE;
        int offsetY = Integer.MAX_VALUE;

        for (int i = 0; i < startPoints.size(); i++) {
            offsetX = Math.min(startPoints.get(i).x, offsetX);
            offsetY = Math.min(startPoints.get(i).y, offsetY);
        }
        for (int i = 0; i < endPoints.size(); i++) {
            float[] point = new float[4];
            point[0] = startPoints.get(i).x - offsetX;
            point[1] = startPoints.get(i).y - offsetY;
            point[2] = endPoints.get(i).x - offsetX;
            point[3] = endPoints.get(i).y - offsetY;
            list.add(point);
        }
        return list;
    }
}
