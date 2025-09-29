package com.trade.zt_listscrollshor_v4

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_listscrollshor_v4.adapter.ChildAdapter
import com.trade.zt_listscrollshor_v4.adapter.MHeadAdapter
import com.trade.zt_listscrollshor_v4.entiy.ChildObj
import com.trade.zt_listscrollshor_v4.entiy.HeadObj
import com.trade.zt_listscrollshor_v4.entiy.SubItemObj
import com.trade.zt_listscrollshor_v4.util.SyncRecycleViewScrollUtil

class MainActivity : AppCompatActivity() {
    private var rv_head: RecyclerView? = null
    private var rv_child: RecyclerView? = null
    private var btn_refresh: Button? = null
    private var childAdapter: ChildAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        rv_head = findViewById(R.id.rv_head)
        rv_child = findViewById(R.id.rv_child)
        btn_refresh = findViewById(R.id.btn_refresh)

        btn_refresh?.setOnClickListener {
            refreshChildData()
        }

        // 设置头部横向列表
        val headList: MutableList<HeadObj> = createHeadData()
        rv_head?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_head?.adapter = MHeadAdapter(headList)

        // 设置垂直列表（包含横向列表）
        val childList: MutableList<ChildObj> = createChildData()

        rv_child?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_child?.setHasFixedSize(true);
        childAdapter = ChildAdapter(childList, rv_head)
        rv_child?.adapter = childAdapter
    }

    /**
     * 刷新垂直列表数据
     */
    private fun refreshChildData() {
        // 创建新的测试数据
        val newChildList = createUpdatedChildData()

        // 更新适配器数据
        childAdapter?.updateData(newChildList)

        Toast.makeText(this, "数据已刷新", Toast.LENGTH_SHORT).show()
    }

    /**
     * 创建更新后的测试数据
     */
    private fun createUpdatedChildData(): MutableList<ChildObj> {
        val list = mutableListOf<ChildObj>()

        // 创建不同的数据以便观察变化
        for (i in 1..8) {
            val childObj = ChildObj("更新后的列表项 $i")

            // 为每个垂直项创建横向子项
            val subItems = mutableListOf<SubItemObj>()
            for (j in 1..5) {
                subItems.add(
                    SubItemObj(
                        "更新子项 $j",
                        "更新内容 $j-$i"
                    )
                )
            }

            childObj.subItems = subItems
            list.add(childObj)
        }

        return list
    }

    /**
     * 创建头部测试数据
     */
    private fun createHeadData(): MutableList<HeadObj> {
        val list = mutableListOf<HeadObj>()
        for (i in 1..10) {
            list.add(HeadObj("头部标题 $i"))
        }
        return list
    }

    /**
     * 创建垂直列表测试数据（包含横向子项）
     */
    private fun createChildData(): MutableList<ChildObj> {
        val list = mutableListOf<ChildObj>()

        for (i in 1..15) {
            val childObj = ChildObj("垂直列表项 $i")

            // 为每个垂直项创建横向子项
            val subItems = mutableListOf<SubItemObj>()
            for (j in 1..10) {
                subItems.add(
                    SubItemObj(
                        "子项 $j",
                        "内容 $j-$i"
                    )
                )
            }

            childObj.subItems = subItems
            list.add(childObj)
        }

        return list
    }
}