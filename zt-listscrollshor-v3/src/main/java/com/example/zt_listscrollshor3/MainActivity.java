package com.example.zt_listscrollshor3;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zt_listscrollshor3.adapter.HeaderAdapter;
import com.example.zt_listscrollshor3.adapter.FixedColumnAdapter;
import com.example.zt_listscrollshor3.adapter.ContentAdapter;

import org.jspecify.annotations.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFixedColumn;    // 左侧固定列内容
    private RecyclerView rvHeader;         // 右侧横向表头
    private RecyclerView rvContent;        // 右侧内容（垂直方向）

    // 用于同步横向滚动的变量
    private int lastScrollX = 0;
    private boolean isSyncingScroll = false; // 防止递归触发
    private boolean isSyncingVerticalScroll = false; // 防止递归触发垂直同步

    // 只使用弱引用列表，避免内存泄漏
    private final List<WeakReference<RecyclerView>> weakRowRecyclerViews = new ArrayList<>();

    private List<List<String>> contentList; // <-- 提升为成员变量

    private Handler pollHandler = new Handler(Looper.getMainLooper());
    private int pollRowIndex = 0; // 当前轮询的行
    private boolean isPolling = false;

    private static final String TAG = "ZTDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 初始化各区域RecyclerView
        rvFixedColumn = findViewById(R.id.rvFixedColumn);
        rvHeader = findViewById(R.id.rvHeader);
        rvContent = findViewById(R.id.rvContent);

        // 2. 设置布局管理器并优化性能
        LinearLayoutManager fixedColumnLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager headerLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager contentLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // 启用预取以提高滚动性能
        fixedColumnLayoutManager.setItemPrefetchEnabled(true);
        headerLayoutManager.setItemPrefetchEnabled(true);
        contentLayoutManager.setItemPrefetchEnabled(true);

        // 设置预取项目数量
        fixedColumnLayoutManager.setInitialPrefetchItemCount(5);
        headerLayoutManager.setInitialPrefetchItemCount(5);
        contentLayoutManager.setInitialPrefetchItemCount(3);

        rvFixedColumn.setLayoutManager(fixedColumnLayoutManager);
        rvHeader.setLayoutManager(headerLayoutManager);
        rvContent.setLayoutManager(contentLayoutManager);

        // 设置固定大小可以提高性能
        rvFixedColumn.setHasFixedSize(true);
        rvHeader.setHasFixedSize(true);
        rvContent.setHasFixedSize(true);

        // ====== 示例数据填充 ======
        // 假设有5列（含固定列）和20行
        List<String> headerList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            headerList.add("表头" + i + " > " + randomStr());
        }
        List<String> fixedColumnList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            fixedColumnList.add("固定" + i);
        }
        contentList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                row.add("R" + i + "C" + j + " > " + randomStr());
            }
            contentList.add(row);
        }

        // ====== 计算每一列最大宽度 ======
        List<Integer> columnWidths = calcColumnWidths(headerList, contentList);

        // ====== 设置Adapter ======
        rvHeader.setAdapter(new HeaderAdapter(headerList, columnWidths));
        rvFixedColumn.setAdapter(new FixedColumnAdapter(fixedColumnList));
        rvContent.setAdapter(new ContentAdapter(this, contentList, columnWidths, new ContentAdapter.OnRowBindListener() {
            @Override
            public void onRowBind(RecyclerView rowRecyclerView) {
                // 使用弱引用收集所有横向RecyclerView用于同步，避免内存泄漏
                boolean isNewView = true;
                for (WeakReference<RecyclerView> weakRef : weakRowRecyclerViews) {
                    RecyclerView rv = weakRef.get();
                    if (rv == rowRecyclerView) {
                        isNewView = false;
                        break;
                    }
                }

                if (isNewView) {
                    // 清理已经被回收的引用
                    for (int i = weakRowRecyclerViews.size() - 1; i >= 0; i--) {
                        if (weakRowRecyclerViews.get(i).get() == null) {
                            weakRowRecyclerViews.remove(i);
                        }
                    }

                    // 添加新的RecyclerView弱引用
                    weakRowRecyclerViews.add(new WeakReference<>(rowRecyclerView));

                    // 设置初始滚动位置
                    rowRecyclerView.scrollToPosition(0);
                }

                // 无论是否已存在，都同步横向滚动到lastScrollX
                rowRecyclerView.post(() -> {
                    if (rowRecyclerView.isAttachedToWindow()) {
                        int currentX = rowRecyclerView.computeHorizontalScrollOffset();
                        int dx = lastScrollX - currentX;
                        if (dx != 0) {
                            rowRecyclerView.scrollBy(dx, 0);
                        }
                    }
                });

                // 监听横向滚动，使用更高效的方式
                rowRecyclerView.clearOnScrollListeners();
                rowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dx != 0 && !isSyncingScroll) {
                            syncHorizontalScroll(recyclerView);
                        }
                    }
                });
            }
        }));
        Log.d(TAG, "setAdapter for rvContent");

        // 表头横向滚动同步内容
        rvHeader.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0) {
                    syncHorizontalScroll(recyclerView);
                }
            }
        });

        // 5. 垂直滚动同步（左侧固定列与内容区域联动）
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int first = ((LinearLayoutManager) rvContent.getLayoutManager()).findFirstVisibleItemPosition();
                Log.d(TAG, "rvContent onScrolled: firstVisible=" + first + ", dx=" + dx + ", dy=" + dy);
                if (isSyncingVerticalScroll) return;
                isSyncingVerticalScroll = true;
                int offset = rvContent.getChildAt(0) != null ? rvContent.getChildAt(0).getTop() : 0;
                ((LinearLayoutManager) rvFixedColumn.getLayoutManager()).scrollToPositionWithOffset(first, offset);
                isSyncingVerticalScroll = false;
            }
        });
        rvFixedColumn.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int first = ((LinearLayoutManager) rvFixedColumn.getLayoutManager()).findFirstVisibleItemPosition();
                Log.d(TAG, "rvFixedColumn onScrolled: firstVisible=" + first + ", dx=" + dx + ", dy=" + dy);
                if (isSyncingVerticalScroll) return;
                isSyncingVerticalScroll = true;
                int offset = rvFixedColumn.getChildAt(0) != null ? rvFixedColumn.getChildAt(0).getTop() : 0;
                ((LinearLayoutManager) rvContent.getLayoutManager()).scrollToPositionWithOffset(first, offset);
                isSyncingVerticalScroll = false;
            }
        });

        // 6. 性能优化：使用RecyclerView复用，避免嵌套滑动冲突
        // 具体Adapter和同步逻辑请根据实际数据结构实现

        // 启动轮询修改某一行
        startPollingRowUpdate();
    }

    // 轮询定时修改某一行的部分值
    private void startPollingRowUpdate() {
        if (isPolling) return; // 避免重复启动
        isPolling = true;
        pollHandler.post(pollRunnable);
        Log.d(TAG, "轮询更新已启动");
    }

    private void stopPollingRowUpdate() {
        if (!isPolling) return; // 避免重复停止
        isPolling = false;
        pollHandler.removeCallbacks(pollRunnable);
        Log.d(TAG, "轮询更新已停止");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPollingRowUpdate(); // 在界面可见时启动轮询
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPollingRowUpdate(); // 在界面不可见时停止轮询，节省资源
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源，避免内存泄漏
        stopPollingRowUpdate();
        pollHandler.removeCallbacksAndMessages(null);
        weakRowRecyclerViews.clear();
    }

    /**
     * 计算每一列最大宽度（像素），包含表头和所有行内容
     */
    private List<Integer> calcColumnWidths(List<String> headerList, List<List<String>> contentList) {
        List<Integer> widths = new ArrayList<>();
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.table_text_size)); // 需在dimens.xml定义table_text_size
        int padding = getResources().getDimensionPixelSize(R.dimen.table_cell_padding); // 需在dimens.xml定义table_cell_padding

        int columnCount = headerList.size();
        for (int col = 0; col < columnCount; col++) {
            int maxWidth = (int) paint.measureText(headerList.get(col)) + padding * 2;
            for (List<String> row : contentList) {
                if (col < row.size()) {
                    int w = (int) paint.measureText(row.get(col)) + padding * 2;
                    if (w > maxWidth) maxWidth = w;
                }
            }
            widths.add(maxWidth);
        }
        return widths;
    }

    // 横向同步滚动方法（用绝对位置同步，防止跳跃）
    private void syncHorizontalScroll(RecyclerView source) {
        if (isSyncingScroll) return;
        isSyncingScroll = true;

        try {
            int scrollX = source.computeHorizontalScrollOffset();
            lastScrollX = scrollX;

            // 清理失效的弱引用
            for (int i = weakRowRecyclerViews.size() - 1; i >= 0; i--) {
                if (weakRowRecyclerViews.get(i).get() == null) {
                    weakRowRecyclerViews.remove(i);
                }
            }

            // 同步所有横向RecyclerView
            for (WeakReference<RecyclerView> weakRef : weakRowRecyclerViews) {
                RecyclerView rv = weakRef.get();
                if (rv == null || rv == source || !rv.isAttachedToWindow()) {
                    continue;
                }

                int currentX = rv.computeHorizontalScrollOffset();
                int dx = scrollX - currentX;
                if (dx != 0) {
                    rv.scrollBy(dx, 0);
                }
            }

            // 同步表头
            if (rvHeader != source && rvHeader.isAttachedToWindow()) {
                int headerCurrentX = rvHeader.computeHorizontalScrollOffset();
                int dx = scrollX - headerCurrentX;
                if (dx != 0) {
                    rvHeader.scrollBy(dx, 0);
                }
            }
        } finally {
            isSyncingScroll = false;
        }
    }

    // 随机修改第 row 行的若干列，并刷新该行
    private void updateRowValues(int row, List<Integer> columnsToUpdate) {
        Log.d(TAG, "updateRowValues: row=" + row + ", columns=" + columnsToUpdate);
        if (row < 0 || row >= contentList.size()) return;

        // 获取行数据并更新
        List<String> rowData = contentList.get(row);
        for (int col : columnsToUpdate) {
            if (col >= 0 && col < rowData.size()) {
                rowData.set(col, "新值" + randomStr());
            }
        }

        // 使用主线程更新UI
        rvContent.post(() -> {
            // 检查Activity是否已销毁
            if (isFinishing() || isDestroyed()) return;

            // 查找对应的ViewHolder
            RecyclerView.ViewHolder vh = rvContent.findViewHolderForAdapterPosition(row);
            if (vh instanceof ContentAdapter.RowViewHolder) {
                ContentAdapter.RowViewHolder rowVH = (ContentAdapter.RowViewHolder) vh;
                if (rowVH.rowAdapter != null) {
                    // 只更新变化的列，提高性能
                    rowVH.rowAdapter.setRow(rowData, columnsToUpdate);
                }
            } else {
                // 如果ViewHolder不可见，通知适配器更新该项
                // 这样当它滚动回可见区域时会显示最新数据
                rvContent.getAdapter().notifyItemChanged(row);
            }
        });
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPolling) return;

            try {
                // 随机选择1-3列进行更新
                int row = pollRowIndex;
                List<Integer> cols = new ArrayList<>();
                int colCount = 1 + new Random().nextInt(3); // 1到3列
                for (int i = 0; i < colCount; i++) {
                    cols.add(new Random().nextInt(Math.min(10, contentList.get(0).size())));
                }

                Log.d(TAG, "pollRunnable: pollRowIndex=" + pollRowIndex + ", 更新" + colCount + "列");
                updateRowValues(row, cols);

                // 更新下一行索引
                pollRowIndex = (pollRowIndex + 1) % contentList.size();
            } catch (Exception e) {
                Log.e(TAG, "轮询更新出错: " + e.getMessage(), e);
            } finally {
                // 确保继续轮询
                if (isPolling) {
                    pollHandler.postDelayed(this, 1000L);
                }
            }
        }
    };

    public String randomStr() {
        // 生成长度5-15的随机字符串
        int minLength = 1;
        int maxLength = 10;
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;

        String randomString = random.ints(48, 122 + 1)  // ASCII范围：'0'到'z'
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 过滤掉特殊字符
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return randomString.toString();
    }
}