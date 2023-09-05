package com.example.zzt.recycleview.util

import com.example.zzt.recycleview.entity.ItemData
import com.zzt.utilcode.util.ColorUtils
import java.util.Collections

/**
 * @author: zeting
 * @date: 2023/8/30
 *
 */
object DataListUtil {

    fun getList100(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        for (i in 0..1000) {
            mList.add(ItemData(i, "title:${i}", "msg:$i"))
        }
        return mList
    }


    fun getList100EvenNumber(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        for (i in 0..1000) {
            if (i % 2 == 0) {
                mList.add(ItemData(i, "title:${i}", "msg:$i >>> 偶数"))
            } else {
                mList.add(ItemData(i, "title:${i}", "msg:$i"))
            }
        }
        return mList
    }


    fun getList100Random(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        var index = (1..6).random()
        for (i in 0..1000) {
            if (i % index == 0) {
                var random = (1..9999).random()
                mList.add(ItemData(i, "title:${i}", "msg:$i >> 随机数${random}"))
            } else {
                mList.add(ItemData(i, "title:${i}", "msg:$i"))
            }
        }
        return mList
    }


    fun getList100RandomBG(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        for (i in 0..1000) {
            val randomColor = ColorUtils.getRandomColor()
            mList.add(ItemData(i, "title:${i}", "msg:$i >>> 随机颜色 ", randomColor))
        }
        return mList
    }

    fun getList100Sort(): List<ItemData> {
        var mList = mutableListOf<ItemData>()
        for (i in 0..100) {
            var sort = (1L..100L).random()
            mList.add(ItemData(i, "title:${i}", "msg:$i >>随机排序 sort:(${sort})", sort = sort))
        }
        return mList
    }

    fun getList100BySortV1(oldList: MutableList<ItemData>?): MutableList<ItemData>? {
        //sort比较器排序，重写compare方法
        if (oldList != null) {
            Collections.sort(oldList, object : Comparator<ItemData> {
                override fun compare(o1: ItemData?, o2: ItemData?): Int {
                    return o1?.sort?.compareTo(o2?.sort ?: 0) ?: 0
                }
            })
        };
        return oldList
    }

    fun getList100BySortV2(oldList: MutableList<ItemData>?): MutableList<ItemData>? {
        oldList?.sortWith(kotlin.Comparator { o1, o2 -> o1?.sort?.compareTo(o2?.sort ?: 0) ?: 0 });
        return oldList
    }

    fun getList100BySortV3(oldList: MutableList<ItemData>?): MutableList<ItemData>? {
        oldList?.sortBy { old -> old.sort }
        return oldList
    }
}