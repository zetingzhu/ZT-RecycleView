package com.zzt.gemin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gemin_activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        var mainRecyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView)
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        // 准备数据
        val dataList = listOf(
            MainListItem.TitleItem("热门推荐"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(1, "商品 A", "https://via.placeholder.com/150"),
                    HorizontalItem(2, "商品 B", "https://via.placeholder.com/150"),
                    HorizontalItem(3, "商品 C", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(101, "商品 X", 25.99, "这是一个很棒的商品。")
            ),
            MainListItem.TitleItem("猜你喜欢"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(4, "商品 D", "https://via.placeholder.com/150"),
                    HorizontalItem(5, "商品 E", "https://via.placeholder.com/150"),
                    HorizontalItem(6, "商品 F", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(102, "商品 Y", 19.99, "另一个好商品。")
            ),
            MainListItem.TitleItem("热门推荐"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(1, "商品 A", "https://via.placeholder.com/150"),
                    HorizontalItem(2, "商品 B", "https://via.placeholder.com/150"),
                    HorizontalItem(3, "商品 C", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(101, "商品 X", 25.99, "这是一个很棒的商品。")
            ),
            MainListItem.TitleItem("猜你喜欢"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(4, "商品 D", "https://via.placeholder.com/150"),
                    HorizontalItem(5, "商品 E", "https://via.placeholder.com/150"),
                    HorizontalItem(6, "商品 F", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(102, "商品 Y", 19.99, "另一个好商品。")
            ), MainListItem.TitleItem("热门推荐"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(1, "商品 A", "https://via.placeholder.com/150"),
                    HorizontalItem(2, "商品 B", "https://via.placeholder.com/150"),
                    HorizontalItem(3, "商品 C", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(101, "商品 X", 25.99, "这是一个很棒的商品。")
            ),
            MainListItem.TitleItem("猜你喜欢"),
            MainListItem.HorizontalListItem(
                listOf(
                    HorizontalItem(4, "商品 D", "https://via.placeholder.com/150"),
                    HorizontalItem(5, "商品 E", "https://via.placeholder.com/150"),
                    HorizontalItem(6, "商品 F", "https://via.placeholder.com/150")
                )
            ),
            MainListItem.ProductItem(
                Product(102, "商品 Y", 19.99, "另一个好商品。")
            )
        )

        val adapter = MainAdapter(dataList)
        mainRecyclerView.adapter = adapter
    }
}
