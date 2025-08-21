package com.example.zt_rvtophead

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private val items: List<NewsItem>, var callback: () -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val content: TextView = itemView.findViewById(R.id.newsContent)
        val iv_close: ImageView = itemView.findViewById(R.id.iv_close)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.content.text = item.content
        Glide.with(holder.image.context).load(R.drawable.th).into(holder.image)
        // 新增：item点击事件
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            NewsDetailActivity.start(context, item)
        }
        holder.iv_close.setOnClickListener {
            callback.invoke()
        }
    }

    override fun getItemCount(): Int = items.size
}
