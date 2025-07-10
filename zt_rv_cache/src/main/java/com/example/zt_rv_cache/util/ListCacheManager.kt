package com.example.zt_rv_cache.util
import android.util.LruCache
import com.example.zt_rv_cache.entiy.MyDataList

/**
 * @author: zeting
 * @date: 2025/7/9
 * LruCache 工具类，用于存储列表数据对象。
 * Key 为列表的唯一标识 (例如：API路径 + 参数哈希)。
 * Value 为 List<MyListItem>。
 */
object ListCacheManager {

    // 1. 定义缓存的最大容量
    // 这里我们选择以“条目数”作为单位，而不是字节，
    // 因为计算整个列表的字节数可能比较复杂且不直观。
    // 例如，我们想缓存最多 10 个不同的列表。
    private const val MAX_LIST_COUNT = 10

    private val listCache: LruCache<String, MyDataList> =
        object : LruCache<String, MyDataList>(MAX_LIST_COUNT) {

            /**
             * 重写 sizeOf 方法：计算每个 Value（即每个列表）的大小。
             *
             * 在这里，我们选择将每个列表的大小计为 **1**。
             * 这意味着 LruCache 会按照缓存的列表数量来管理容量，而不是列表内部元素的数量。
             * 如果你想根据列表内部元素的总数来管理容量，你需要在这里返回 list.size。
             * 但是那样的话，当 list.size 很大时，LruCache 会很快达到容量上限。
             * 所以，通常缓存整个列表时，计算为 1 个单位更常见。
             */
            override fun sizeOf(key: String, list: MyDataList): Int {
                return 1 // 每个列表算作一个缓存条目
                // 如果你想根据列表内元素的数量来计算大小，可以返回 list.size
                // return list.size
            }

            /**
             * 重写 entryRemoved 方法：当一个列表条目被移除时调用。
             *
             * 在这里可以进行一些清理工作，但对于 List<MyListItem> 这种普通数据类列表，
             * 通常不需要额外的资源释放，因为它们没有像 Bitmap 那样的 native 内存或文件句柄。
             */
            override fun entryRemoved(
                evicted: Boolean, // 如果为 true，表示是因为容量不足而被移除
                key: String,
                oldValue: MyDataList,
                newValue: MyDataList?
            ) {
                super.entryRemoved(evicted, key, oldValue, newValue)
                // Log.d("ListCacheManager", "Cache entry removed: $key, evicted: $evicted")
                // 如果 List 中的 MyListItem 包含需要释放的资源，则在此处处理
                // 例如：oldValue.forEach { it.releaseSomeResource() }
            }
        }

    /**
     * 将列表数据添加到缓存。
     * @param key 列表的唯一标识，例如：API_PATH + Query_Params.hashCode()
     * @param dataList 要缓存的列表数据
     */
    fun put(key: String, dataList: MyDataList) {
        if (get(key) == null) { // 避免重复添加，LruCache 会根据 key 自动更新
            listCache.put(key, dataList)
        }
    }

    /**
     * 从缓存中获取列表数据。
     * @param key 列表的唯一标识
     * @return 缓存的列表数据，如果不存在则返回 null
     */
    fun get(key: String): MyDataList? {
        return listCache.get(key)
    }

    /**
     * 从缓存中移除指定 key 的列表数据。
     * @param key 要移除的列表的唯一标识
     */
    fun remove(key: String) {
        listCache.remove(key)
    }

    /**
     * 清除所有缓存的列表数据。
     */
    fun clear() {
        listCache.evictAll()
    }

    /**
     * 获取当前缓存中的列表数量。
     */
    fun size(): Int {
        return listCache.size()
    }

    /**
     * 获取缓存的最大容量（列表数量）。
     */
    fun maxSize(): Int {
        return listCache.maxSize()
    }
}