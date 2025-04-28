package com.zzt.deepseek

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */

import android.os.Bundle
import android.widget.Toast
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
        setContentView(R.layout.deep_activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val verticalRecyclerView = findViewById<RecyclerView>(R.id.verticalRecyclerView)
        verticalRecyclerView.layoutManager = LinearLayoutManager(this)

        // 创建模拟数据
        val verticalItems = createMockData()

        verticalRecyclerView.adapter =
            VerticalAdapter(verticalItems) { verticalPos, horizontalPos ->
                val item = verticalItems[verticalPos].horizontalItems[horizontalPos]
                Toast.makeText(
                    this,
                    "点击了: ${item.name} (位置: 垂直$verticalPos, 水平$horizontalPos)",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun createMockData(): List<VerticalItem> {
        return listOf(
            VerticalItem(
                "热门电影", listOf(
                    HorizontalItem("1", "复仇者联盟", R.drawable.ic_movie),
                    HorizontalItem("2", "阿凡达", R.drawable.ic_movie),
                    HorizontalItem("3", "泰坦尼克号", R.drawable.ic_movie),
                    HorizontalItem("4", "星际穿越", R.drawable.ic_movie),
                    HorizontalItem("5", "盗梦空间", R.drawable.ic_movie)
                )
            ),
            VerticalItem(
                "热门电视剧", listOf(
                    HorizontalItem("6", "权力的游戏", R.drawable.ic_tv),
                    HorizontalItem("7", "老友记", R.drawable.ic_tv),
                    HorizontalItem("8", "生活大爆炸", R.drawable.ic_tv),
                    HorizontalItem("9", "绝命毒师", R.drawable.ic_tv),
                    HorizontalItem("10", "纸牌屋", R.drawable.ic_tv)
                )
            ),
            VerticalItem(
                "热门动漫", listOf(
                    HorizontalItem("11", "进击的巨人", R.drawable.ic_anime),
                    HorizontalItem("12", "鬼灭之刃", R.drawable.ic_anime),
                    HorizontalItem("13", "火影忍者", R.drawable.ic_anime),
                    HorizontalItem("14", "海贼王", R.drawable.ic_anime),
                    HorizontalItem("15", "咒术回战", R.drawable.ic_anime)
                )
            ),
            VerticalItem(
                "热门电影", listOf(
                    HorizontalItem("1", "复仇者联盟", R.drawable.ic_movie),
                    HorizontalItem("2", "阿凡达", R.drawable.ic_movie),
                    HorizontalItem("3", "泰坦尼克号", R.drawable.ic_movie),
                    HorizontalItem("4", "星际穿越", R.drawable.ic_movie),
                    HorizontalItem("5", "盗梦空间", R.drawable.ic_movie)
                )
            ),
            VerticalItem(
                "热门电视剧", listOf(
                    HorizontalItem("6", "权力的游戏", R.drawable.ic_tv),
                    HorizontalItem("7", "老友记", R.drawable.ic_tv),
                    HorizontalItem("8", "生活大爆炸", R.drawable.ic_tv),
                    HorizontalItem("9", "绝命毒师", R.drawable.ic_tv),
                    HorizontalItem("10", "纸牌屋", R.drawable.ic_tv)
                )
            ),
            VerticalItem(
                "热门动漫", listOf(
                    HorizontalItem("11", "进击的巨人", R.drawable.ic_anime),
                    HorizontalItem("12", "鬼灭之刃", R.drawable.ic_anime),
                    HorizontalItem("13", "火影忍者", R.drawable.ic_anime),
                    HorizontalItem("14", "海贼王", R.drawable.ic_anime),
                    HorizontalItem("15", "咒术回战", R.drawable.ic_anime)
                )
            )
        )
    }
}