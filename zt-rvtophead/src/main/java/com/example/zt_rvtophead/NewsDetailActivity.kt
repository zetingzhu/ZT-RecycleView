package com.example.zt_rvtophead

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class NewsDetailActivity : AppCompatActivity() {
    var item: NewsItem? = null

    companion object {
        @JvmStatic
        fun start(context: Context, item: NewsItem) {
            val starter = Intent(context, NewsDetailActivity::class.java)
                .putExtra("item", item)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item = intent.getSerializableExtra("item", NewsItem::class.java)
        } else {
            item = intent.getSerializableExtra("item") as NewsItem
        }
        item?.let {
            findViewById<TextView>(R.id.detailTitle).text = it.title
            findViewById<TextView>(R.id.detailContent).text = it.content
            Glide.with(this).load(it.imageUrl).into(findViewById<ImageView>(R.id.detailImage))
            findViewById<TextView>(R.id.tv_desc).text = it.desc
        }
    }
}
