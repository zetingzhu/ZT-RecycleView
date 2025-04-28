package com.zzt.gemin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.zt_rv_include_rv.R

/**
 * @author: zeting
 * @date: 2025/4/28
 *
 */
class MainAdapter(private val dataList: List<MainListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 定义不同的视图类型
    private val VIEW_TYPE_TITLE = 0
    private val VIEW_TYPE_HORIZONTAL_LIST = 1
    private val VIEW_TYPE_PRODUCT = 2

    // 根据数据项的类型返回不同的视图类型
    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is MainListItem.TitleItem -> VIEW_TYPE_TITLE
            is MainListItem.HorizontalListItem -> VIEW_TYPE_HORIZONTAL_LIST
            is MainListItem.ProductItem -> VIEW_TYPE_PRODUCT
        }
    }

    // 创建不同的 ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                val view = inflater.inflate(R.layout.gemin_item_title, parent, false)
                TitleViewHolder(view)
            }
            VIEW_TYPE_HORIZONTAL_LIST -> {
                val view = inflater.inflate(R.layout.gemin_item_horizontal_list, parent, false)
                HorizontalListViewHolder(view)
            }
            VIEW_TYPE_PRODUCT -> {
                val view = inflater.inflate(R.layout.gemin_item_product, parent, false)
                ProductViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // 绑定数据到 ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataList[position]) {
            is MainListItem.TitleItem -> (holder as TitleViewHolder).bind(item.title)
            is MainListItem.HorizontalListItem -> (holder as HorizontalListViewHolder).bind(item.items)
            is MainListItem.ProductItem -> (holder as ProductViewHolder).bind(item.product)
        }
    }

    override fun getItemCount(): Int = dataList.size

    // 为不同类型的项定义 ViewHolder
    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(title: String) {
            titleTextView.text = title
        }
    }

    class HorizontalListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val horizontalRecyclerView: RecyclerView =
            itemView.findViewById(R.id.horizontalRecyclerView)
        // 将 horizontalAdapter 提升为 ViewHolder 的属性
        private var horizontalAdapter: HorizontalAdapter? = null

        fun bind(items: List<HorizontalItem>) {
            // 优化：只在需要时创建新的 Adapter
            if (horizontalAdapter == null) {
                horizontalAdapter = HorizontalAdapter(items)
                horizontalRecyclerView.adapter = horizontalAdapter
                horizontalRecyclerView.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                horizontalRecyclerView.setHasFixedSize(true)
                horizontalRecyclerView.isNestedScrollingEnabled = false
            } else {
                // 否则，更新 Adapter 中的数据
                horizontalAdapter?.updateData(items)
            }
        }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = "￥${product.price}"
            descriptionTextView.text = product.description
        }
    }
}
