package com.zzt.entiy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: zeting
 * @date: 2025/4/3
 */
public class ItemListData {
    private static class InnerClass {
        private static final ItemListData INSTANCE = new ItemListData();
    }

    private ItemListData() {
    }

    public static ItemListData getInstance() {
        return InnerClass.INSTANCE;
    }

    public List<ItemTitle> dataList() {
        List<ItemTitle> mList = new ArrayList<>();
        Long sysTime = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            mList.add(new ItemTitle("标题 > " + i, sysTime + (random.nextInt(100) * 3000)));
        }

        return mList;
    }

}
