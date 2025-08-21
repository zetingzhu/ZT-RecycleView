package com.example.zt_horandversliding.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_horandversliding.R
import com.example.zt_horandversliding.adapter.TableAdapter
import com.example.zt_horandversliding.adapter.TableHeaderAdapter
import com.example.zt_horandversliding.model.TableColumn
import com.example.zt_horandversliding.model.TableRow
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView

/**
 * 联动表格视图，实现表头和内容的横向联动滚动，支持左侧固定列
 */
class LinkedTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    // 用于延迟操作的Handler
    private val handler = Handler(Looper.getMainLooper())

    // 表头部分
    private lateinit var headerScrollView: LinkedScrollView
    private lateinit var headerRecyclerView: RecyclerView
    private lateinit var headerAdapter: TableHeaderAdapter
    
    // 固定表头部分（左上角）
    private lateinit var fixedHeaderView: TextView
    
    // 固定列部分
    private lateinit var fixedColumnRecyclerView: RecyclerView
    private lateinit var fixedColumnAdapter: TableAdapter
    
    // 内容部分
    private lateinit var contentScrollView: LinkedScrollView
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var contentAdapter: TableAdapter
    
    // 垂直滚动容器
    private lateinit var verticalScrollView: NestedScrollView

    // 列信息
    private val columns = mutableListOf<TableColumn>()
    private var fixedColumnIndex = 0 // 默认固定第一列
    
    // 行数据
    private val rows = mutableListOf<TableRow>()

    init {
        orientation = VERTICAL
        initView()
    }

    private fun initView() {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_linked_table, this, true)

        // 初始化固定表头（左上角）
        fixedHeaderView = findViewById(R.id.fixed_header)
        
        // 初始化表头
        headerScrollView = findViewById(R.id.header_scroll_view)
        headerRecyclerView = findViewById(R.id.header_recycler_view)
        headerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        headerAdapter = TableHeaderAdapter(columns)
        headerRecyclerView.adapter = headerAdapter
        
        // 初始化固定列
        fixedColumnRecyclerView = findViewById(R.id.fixed_column_recycler_view)
        fixedColumnRecyclerView.layoutManager = LinearLayoutManager(context)
        
        // 初始化垂直滚动容器
        verticalScrollView = findViewById(R.id.vertical_scroll_view)

        // 初始化内容
        contentScrollView = findViewById(R.id.content_scroll_view)
        contentRecyclerView = findViewById(R.id.content_recycler_view)
        contentRecyclerView.layoutManager = LinearLayoutManager(context)
        
        // 设置滚动联动
        headerScrollView.addLinkedScrollView(contentScrollView)
        contentScrollView.addLinkedScrollView(headerScrollView)
        
        // 设置垂直滚动联动
        verticalScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // 同步固定列的垂直滚动位置
            fixedColumnRecyclerView.scrollBy(0, scrollY - fixedColumnRecyclerView.computeVerticalScrollOffset())
        }
        
        // 设置固定列的滚动监听
        fixedColumnRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != 0) {
                    // 同步内容区域的垂直滚动位置
                    verticalScrollView.scrollTo(0, fixedColumnRecyclerView.computeVerticalScrollOffset())
                }
            }
        })
    }

    /**
     * 设置表格列信息
     */
    fun setColumns(columns: List<TableColumn>) {
        this.columns.clear()
        this.columns.addAll(columns)
        
        // 设置固定表头文本
        if (columns.isNotEmpty()) {
            fixedHeaderView.text = columns[fixedColumnIndex].title
        }
        
        // 创建不包含固定列的列列表
        val scrollableColumns = ArrayList(columns)
        if (scrollableColumns.isNotEmpty()) {
            scrollableColumns.removeAt(fixedColumnIndex)
        }
        
        // 重新创建表头适配器
        headerAdapter = TableHeaderAdapter(scrollableColumns)
        headerRecyclerView.adapter = headerAdapter
        
        // 如果已有数据，则更新内容适配器
        if (rows.isNotEmpty()) {
            updateContentAdapter()
        }
    }

    /**
     * 设置表格数据
     */
    fun setData(rows: List<TableRow>) {
        this.rows.clear()
        this.rows.addAll(rows)
        
        // 更新内容适配器
        updateContentAdapter()
    }
    
    /**
     * 更新内容适配器
     */
    private fun updateContentAdapter() {
        if (columns.isEmpty()) return
        
        // 创建固定列的列列表
        val fixedColumns = listOf(columns[fixedColumnIndex])
        
        // 创建不包含固定列的列列表
        val scrollableColumns = ArrayList(columns)
        scrollableColumns.removeAt(fixedColumnIndex)
        
        // 创建固定列适配器
        fixedColumnAdapter = TableAdapter(fixedColumns, rows, null, true)
        fixedColumnRecyclerView.adapter = fixedColumnAdapter
        
        // 创建内容适配器
        contentAdapter = TableAdapter(scrollableColumns, rows, headerAdapter, false)
        contentRecyclerView.adapter = contentAdapter
        
        // 延迟一帧，确保布局完成后再同步滚动位置
        handler.post {
            // 确保表头和内容的滚动位置一致
            headerScrollView.scrollTo(contentScrollView.scrollX, 0)
            contentScrollView.scrollTo(headerScrollView.scrollX, 0)
        }
    }

    /**
     * 添加表格数据
     */
    fun addData(rows: List<TableRow>) {
        if (rows.isEmpty()) return
        
        val startPosition = this.rows.size
        this.rows.addAll(rows)
        
        // 如果是首次添加数据，需要创建适配器
        if (contentRecyclerView.adapter == null) {
            updateContentAdapter()
        } else {
            // 否则只需通知适配器数据变化
            contentAdapter.notifyItemRangeInserted(startPosition, rows.size)
            fixedColumnAdapter.notifyItemRangeInserted(startPosition, rows.size)
            
            // 重新计算所有单元格宽度
            handler.post {
                // 创建新的内容适配器以重新计算宽度
                updateContentAdapter()
            }
        }
    }

    /**
     * 清除表格数据
     */
    fun clearData() {
        this.rows.clear()
        if (::contentAdapter.isInitialized) {
            contentAdapter.notifyDataSetChanged()
        }
        if (::fixedColumnAdapter.isInitialized) {
            fixedColumnAdapter.notifyDataSetChanged()
        }
    }
    
    /**
     * 设置固定列的索引
     */
    fun setFixedColumnIndex(index: Int) {
        if (index >= 0 && index < columns.size) {
            this.fixedColumnIndex = index
            
            // 更新固定表头和适配器
            if (columns.isNotEmpty()) {
                fixedHeaderView.text = columns[fixedColumnIndex].title
            }
            
            if (rows.isNotEmpty()) {
                updateContentAdapter()
            }
        }
    }
}
