package com.example.zt_rvtophead

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(
    private val data: List<NewsItem>,
    private val onClose: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_LOADING = 1
    }

    var showLoadingItem: Boolean = false

    override fun getItemCount(): Int {
        return data.size + if (showLoadingItem) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoadingItem && position == itemCount - 1) TYPE_LOADING else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
            NewsViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) {
            // 可选：设置加载动画或文字
        } else if (holder is NewsViewHolder) {
            val item = data[position]
            holder.newsTitle.text = item.title
            holder.newsContent.text = item.content
            holder.newsImage.setImageResource(item.imageUrl)
            // 新增：item点击事件
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                NewsDetailActivity.start(context, item)
            }
            holder.iv_close.setOnClickListener {
                onClose.invoke()
            }
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsContent: TextView = itemView.findViewById(R.id.newsContent)
        val iv_close: ImageView = itemView.findViewById(R.id.iv_close)
    }
}
