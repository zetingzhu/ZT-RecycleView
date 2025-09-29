package com.trade.zt_rv_group_list.v1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_rv_group_list.R
import com.trade.zt_rv_group_list.v1.adapter.ExpandableAdapter
import com.trade.zt_rv_group_list.v1.model.ChildItem
import com.trade.zt_rv_group_list.v1.model.GroupItem

class MainActivityV1 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpandableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_v1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)

        // 创建测试数据
        val groupItems = createSampleData()

        // 设置适配器
        adapter = ExpandableAdapter(groupItems)

        // 设置点击监听器
        adapter.onGroupClickListener = { groupItem, position ->
            Toast.makeText(this, "点击了组: ${groupItem.title}", Toast.LENGTH_SHORT).show()
        }

        adapter.onChildClickListener = { childDisplayItem, position ->
            Toast.makeText(
                this,
                "点击了子项: ${childDisplayItem.childItem.title}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 设置RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /**
     * 创建示例数据
     */
    private fun createSampleData(): MutableList<GroupItem> {
        return mutableListOf(
            GroupItem(
                id = 1,
                title = "水果类",
                children = mutableListOf(
                    ChildItem(1, "苹果", "红富士苹果，香甜可口"),
                    ChildItem(2, "香蕉", "进口香蕉，营养丰富"),
                    ChildItem(3, "橙子", "新鲜橙子，维C丰富"),
                    ChildItem(4, "葡萄", "无籽葡萄，酸甜适中")
                )
            ),
            GroupItem(
                id = 2,
                title = "蔬菜类",
                children = mutableListOf(
                    ChildItem(5, "白菜", "新鲜大白菜"),
                    ChildItem(6, "萝卜", "白萝卜，清脆爽口"),
                    ChildItem(7, "土豆", "优质土豆"),
                    ChildItem(8, "西红柿", "沙瓤西红柿")
                )
            ),
            GroupItem(
                id = 3,
                title = "肉类",
                children = mutableListOf(
                    ChildItem(9, "猪肉", "新鲜猪肉"),
                    ChildItem(10, "牛肉", "优质牛肉"),
                    ChildItem(11, "鸡肉", "土鸡肉")
                )
            ),
            GroupItem(
                id = 4,
                title = "饮料类",
                children = mutableListOf(
                    ChildItem(12, "可乐", "经典可口可乐"),
                    ChildItem(13, "雪碧", "清爽雪碧"),
                    ChildItem(14, "果汁", "100%纯果汁"),
                    ChildItem(15, "矿泉水", "天然矿泉水"),
                    ChildItem(16, "茶饮", "各种茶饮料")
                )
            ),
            GroupItem(
                id = 5,
                title = "零食类",
                children = mutableListOf(
                    ChildItem(17, "薯片", "香脆薯片"),
                    ChildItem(18, "饼干", "各种口味饼干"),
                    ChildItem(19, "巧克力", "丝滑巧克力")
                )
            )
        )
    }
}