package com.example.zzt.recycleview.util

import com.example.zzt.recycleview.entity.ItemData

/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
object DataListUtil {

    fun getList100(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        for (i in 0..100) {
            mList.add(ItemData(i, "title:${i}", "msg:$i"))
        }
        return mList
    }
}