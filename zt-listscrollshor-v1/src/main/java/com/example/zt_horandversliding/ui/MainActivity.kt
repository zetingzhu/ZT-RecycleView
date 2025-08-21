package com.example.zt_horandversliding.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zt_horandversliding.R
import com.example.zt_horandversliding.model.TableColumn
import com.example.zt_horandversliding.model.TableRow

/**
 * 这里我需要实现一个功能，就是能够横线和纵向联动的列表，并且横线每一列宽度需要对齐，能够同步滑动，还有一个表头，也需要同步横向滑动
 */
class MainActivity : AppCompatActivity() {

    private lateinit var linkedTableView: LinkedTableView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initTableView()
    }

    private fun initTableView() {
        linkedTableView = findViewById(R.id.linked_table_view)

        // 设置表格列
        val columns = listOf(
            TableColumn("id", "ID", 100),
            TableColumn("name", "姓名", 200),
            TableColumn("age", "年龄", 100),
            TableColumn("gender", "性别", 100),
            TableColumn("address", "地址", 500),
            TableColumn("phone", "电话", 500),
            TableColumn("email", "邮箱", 500),
            TableColumn("email1", "邮箱1", 500),
            TableColumn("email2", "邮箱2", 500),
            TableColumn("email3", "邮箱3", 500),
            TableColumn("email4", "邮箱4", 500),
            TableColumn("email5", "邮箱5", 500),
        )
        linkedTableView.setColumns(columns)

        // 设置表格数据
        val rows = mutableListOf<TableRow>()
        for (i in 1..50) {
            val cells = mapOf(
                "id" to i.toString(),
                "name" to "用户$i",
                "age" to "${20 + i % 20}",
                "gender" to if (i % 2 == 0) "男" else "女",
                "address" to "北京市朝阳区某某街道${i}号",
                "phone" to "1381234${5678 + i}",
                "email" to "user$i@example.com",
                "email1" to "user$i@example.com",
                "email2" to "user$i@example.com",
                "email3" to "user$i@example.com",
                "email4" to "user$i@example.com",
                "email5" to "user$i@example.com",
            )
            rows.add(TableRow(i.toString(), cells))
        }
        linkedTableView.setData(rows)
    }
}
