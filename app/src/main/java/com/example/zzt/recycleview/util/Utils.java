package com.example.zzt.recycleview.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author: zeting
 * @date: 2025/10/21
 */
public class Utils {

    /**
     * 判断list是否为空
     */
    public static boolean isListEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list不为空
     */
    public static boolean isNotListEmpty(Collection<?> coll) {
        return !isListEmpty(coll);
    }

    /**
     * 判断map是否为空
     */
    public static boolean isMapEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 列表某一条数据不为空
     */
    public static boolean isNotEmptyDataForList(Collection<?> coll, int index) {
        if (isListEmpty(coll)) {
            return false;
        } else {
            int size = coll.size();
            if (index < 0 || index >= size) {
                return false;
            }
        }
        return true;
    }

}
