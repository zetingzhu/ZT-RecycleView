package com.example.zt_horandversliding.adapter

import android.graphics.Paint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_horandversliding.R
import com.example.zt_horandversliding.model.TableColumn
import com.example.zt_horandversliding.model.TableRow
import kotlin.math.max

/**
 * 表格内容适配器
 */
class TableAdapter(
    private val columns: List<TableColumn>,
    private val rows: List<TableRow>,
    private val headerAdapter: TableHeaderAdapter? = null,
    private val isFixedColumn: Boolean = false
) : RecyclerView.Adapter<TableAdapter.RowViewHolder>() {
    
    // 用于测量文本宽度
    private val paint = Paint()
    
    // 在适配器中添加一个属性来存储Resources.DisplayMetrics.density
    private var density: Float = 1.0f
    
    init {
        // 设置文本大小，与TextView一致
        paint.textSize = 14f * density  // 使用简单的方式设置文本大小
        
        // 初始化时计算所有单元格的宽度
        if (!isFixedColumn) {
            calculateAllCellWidths()
        }
    }
    
    /**
     * 设置屏幕密度，应在onCreateViewHolder中调用
     */
    private fun setDensity(context: android.content.Context) {
        if (density == 1.0f) {  // 只在第一次设置
            density = context.resources.displayMetrics.density
            // 更新文本大小
            paint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                14f,
                context.resources.displayMetrics
            )
        }
    }
    
    /**
     * 计算所有单元格的宽度，并更新到headerAdapter
     */
    private fun calculateAllCellWidths() {
        // 如果是固定列，不需要计算宽度
        if (isFixedColumn || headerAdapter == null) return
        
        // 收集每列的所有单元格内容
        val cellContents = mutableMapOf<String, MutableList<String>>()
        
        // 初始化每列的内容列表
        columns.forEach { column ->
            cellContents[column.id] = mutableListOf()
        }
        
        // 收集所有单元格内容
        for (row in rows) {
            for (column in columns) {
                val cellText = row.cells[column.id] ?: ""
                cellContents[column.id]?.add(cellText)
            }
        }
        
        // 将收集到的内容传递给headerAdapter进行宽度计算
        headerAdapter.recalculateWidths(cellContents)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        // 设置density
        setDensity(parent.context)
        
        val layoutId = if (isFixedColumn) {
            R.layout.item_table_fixed_cell
        } else {
            R.layout.item_table_row
        }
        
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val row = rows[position]
        holder.bind(row)
    }

    override fun getItemCount(): Int = rows.size

    inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cellContainer: LinearLayout? = itemView.findViewById(R.id.container_cells)
        private val fixedCellTextView: TextView? = itemView.findViewById(R.id.text_fixed_cell_content)

        fun bind(row: TableRow) {
            if (isFixedColumn) {
                // 固定列模式 - 只显示一个单元格
                if (columns.isNotEmpty() && fixedCellTextView != null) {
                    val column = columns[0]
                    val cellText = row.cells[column.id] ?: ""
                    fixedCellTextView.text = cellText
                    
                    // 设置单元格宽度
                    val params = itemView.layoutParams
                    params.width = column.width
                    itemView.layoutParams = params
                }
            } else {
                // 滚动内容模式 - 显示多个单元格
                cellContainer?.let { container ->
                    // 清除之前的单元格
                    container.removeAllViews()
    
                    // 为每一列创建单元格
                    for (column in columns) {
                        val cellView = LayoutInflater.from(itemView.context)
                            .inflate(R.layout.item_table_cell, container, false)
                        
                        // 设置单元格宽度 - 使用headerAdapter中计算的宽度或默认宽度
                        val params = cellView.layoutParams
                        params.width = headerAdapter?.getCalculatedWidth(column.id) ?: column.width
                        cellView.layoutParams = params
    
                        // 设置单元格内容
                        val textView = cellView.findViewById<TextView>(R.id.text_cell_content)
                        val cellText = row.cells[column.id] ?: ""
                        textView.text = cellText
    
                        // 添加到容器
                        container.addView(cellView)
                    }
                }
            }
        }
    }
}