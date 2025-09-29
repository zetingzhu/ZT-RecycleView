package com.trade.zt_listscrollshor_v4.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trade.zt_listscrollshor_v4.R
import com.trade.zt_listscrollshor_v4.entiy.ChildObj

/**
 * 垂直列表适配器，每个项目包含一个横向RecyclerView
 */
class ChildAdapter : RecyclerView.Adapter<ChildAdapter.ViewHolder> {
    var dataList: MutableList<ChildObj>

    //    var syncRvScrollUtil: SyncRecycleViewScrollUtil? = null
    private var headRv: RecyclerView? = null

    private val observerList = HashSet<RecyclerView>()
    private var firstPos = -1
    private var firstOffset = -1

    constructor(
        dataList: MutableList<ChildObj>,
        headRv: RecyclerView?
    ) : super() {
        this.dataList = dataList
        this.headRv = headRv
//        syncRvScrollUtil = SyncRecycleViewScrollUtil()
//        syncRvScrollUtil?.initRecyclerView(headRv)
        //设置头部recycle
        initRecyclerView(headRv);
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.child_item_layout, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvChildTitle.text = data.title

        holder.rvSubItems.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.rvSubItems.setHasFixedSize(true);
        // 首次创建适配器
        holder.rvSubItems.adapter = SubItemAdapter(data.subItems)

        initRecyclerView(holder.rvSubItems)
    }

    override fun getItemCount(): Int = dataList.size

    /**
     * 更新数据列表
     * @param newData 新的数据列表
     */
    fun updateData(newData: MutableList<ChildObj>) {
        this.dataList = newData
        notifyDataSetChanged()
    }

    /**
     * 添加数据项
     * @param item 要添加的数据项
     */
    fun addItem(item: ChildObj) {
        this.dataList.add(item)
        notifyItemInserted(dataList.size - 1)
    }

    /**
     * 在指定位置添加数据项
     * @param position 添加位置
     * @param item 要添加的数据项
     */
    fun addItem(position: Int, item: ChildObj) {
        if (position >= 0 && position <= dataList.size) {
            this.dataList.add(position, item)
            notifyItemInserted(position)
        }
    }

    /**
     * 移除指定位置的数据项
     * @param position 要移除的位置
     */
    fun removeItem(position: Int) {
        if (position >= 0 && position < dataList.size) {
            this.dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChildTitle: TextView = itemView.findViewById(R.id.tv_child_title)
        val rvSubItems: RecyclerView = itemView.findViewById(R.id.rv_sub_items)
    }


    //多条recycleview联动
    fun initRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView == null) {
            return
        }
        recyclerView.setHasFixedSize(true)
        //为每一个recycleview创建layoutManager
        val layoutManager = recyclerView.getLayoutManager() as LinearLayoutManager?
        //todo
        // 通过移动layoutManager来实现列表滑动  此行是让新加载的item条目保持跟已经滑动的recycleview位置保持一致
        // 也就是上拉加载更多的时候  保证新加载出来的item 跟已经滑动的item位置保持一致
        if (layoutManager != null && firstPos > 0 && firstOffset > 0) {
            layoutManager.scrollToPositionWithOffset(firstPos + 1, firstOffset)
        }
        // 添加所有的 recyclerView
        observerList.add(recyclerView)
        //当触摸条目的时候 停止滑动
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                when (motionEvent.getAction()) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> for (rv in observerList) {
                        rv.stopScroll()
                    }
                }
                return false
            }
        })
        //添加当前滑动recycleview的滑动监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.getLayoutManager() as LinearLayoutManager?
                //获取显示第一个item的位置
                val firstPos1 = linearLayoutManager!!.findFirstVisibleItemPosition()
                val firstVisibleItem = linearLayoutManager.getChildAt(0)
                if (firstVisibleItem != null) {
                    //获取第一个item的偏移量
                    val firstRight = linearLayoutManager.getDecoratedRight(firstVisibleItem)
                    //遍历其它的所有的recycleview条目
                    for (rv in observerList) {
                        if (recyclerView !== rv) {
                            val layoutManager = rv.getLayoutManager() as LinearLayoutManager?
                            if (layoutManager != null) {
                                firstPos = firstPos1
                                firstOffset = firstRight
                                //通过当前显示item的位置和偏移量的位置来置顶recycleview 也就是同步其它item的移动距离
                                layoutManager.scrollToPositionWithOffset(firstPos + 1, firstRight)
                            }
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }
}