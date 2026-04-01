package com.example.zzt.recycleview.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zzt.recycleview.R
import com.example.zzt.recycleview.adapter.AdapterV26
import com.example.zzt.recycleview.entity.ItemData

/**
 * @author: zeting
 * @date: 2023/8/30
 * 使用官方 ListAdapter + DiffUtil + ViewType 实现 分组(Header + Items) 展开/收起
 */
class ActRecycleViewV26 : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var adapter: AdapterV26

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_recycle_view_v26)

        rvList = findViewById(R.id.rv_list)

        adapter = AdapterV26 { headerPosition ->
            adapter.toggleGroup(headerPosition)
        }

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        // 直接构建扁平数据
        adapter.setData(buildMockData())
    }

    /**
     * 构建模拟数据：Header(header=1) + Content(header=0) 交替
     */
    private fun buildMockData(): List<ItemData> {
        val list = mutableListOf<ItemData>()
        for (i in 0..20) {
            // 分组头
            list.add(ItemData(id = i, title = "分组 $i", header = 1, headerIndex = i))
            // 分组内容
            for (k in 0..20) {
                list.add(ItemData(id = k, title = "Item $k", msg = "分组${i} 的第${k}条", header = 0, headerIndex = i))
            }
        }
        return list
    }
}
