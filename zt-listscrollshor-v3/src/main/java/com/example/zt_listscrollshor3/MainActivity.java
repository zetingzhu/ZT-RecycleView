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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFixedColumn;    // 左侧固定列内容
    private RecyclerView rvHeader;         // 右侧横向表头
    private RecyclerView rvContent;        // 右侧内容（垂直方向）

    // 用于同步横向滚动的变量
    private int lastScrollX = 0;
    private final List<RecyclerView> rowRecyclerViews = new ArrayList<>();
    private boolean isSyncingScroll = false; // 防止递归触发
    private boolean isSyncingVerticalScroll = false; // 防止递归触发垂直同步

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @Override
            public @NonNull WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        // 1. 初始化各区域RecyclerView
        rvFixedColumn = findViewById(R.id.rvFixedColumn);
        rvHeader = findViewById(R.id.rvHeader);
        rvContent = findViewById(R.id.rvContent);

        // 2. 设置布局管理器
        rvFixedColumn.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvHeader.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

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
                // 收集所有横向RecyclerView用于同步
                if (!rowRecyclerViews.contains(rowRecyclerView)) {
                    rowRecyclerViews.add(rowRecyclerView);
                    // 设置初始滚动位置（用scrollTo保证绝对位置）
                    rowRecyclerView.scrollToPosition(0);
                }
                // 无论是否已存在，都同步横向滚动到lastScrollX
                rowRecyclerView.post(() -> {
                    int currentX = rowRecyclerView.computeHorizontalScrollOffset();
                    int dx = lastScrollX - currentX;
                    if (dx != 0) {
                        rowRecyclerView.scrollBy(dx, 0);
                    }
                });
                // 监听横向滚动
                rowRecyclerView.clearOnScrollListeners();
                rowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dx != 0) {
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
        isPolling = true;
        pollHandler.post(pollRunnable);
    }

    private void stopPollingRowUpdate() {
        isPolling = false;
        pollHandler.removeCallbacks(pollRunnable);
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
        int scrollX = source.computeHorizontalScrollOffset();
        lastScrollX = scrollX;
        // 同步所有横向RecyclerView和表头
        for (RecyclerView rv : rowRecyclerViews) {
            if (rv != source) {
                rv.scrollBy(scrollX - rv.computeHorizontalScrollOffset(), 0);
            }
        }
        if (rvHeader != source) {
            rvHeader.scrollBy(scrollX - rvHeader.computeHorizontalScrollOffset(), 0);
        }
        isSyncingScroll = false;
    }

    // 随机修改第 row 行的若干列，并刷新该行
    private void updateRowValues(int row, List<Integer> columnsToUpdate) {
        Log.d(TAG, "updateRowValues: row=" + row + ", columns=" + columnsToUpdate);
        if (row < 0 || row >= contentList.size()) return;
        List<String> rowData = contentList.get(row);
        for (int col : columnsToUpdate) {
            if (col >= 0 && col < rowData.size()) {
                rowData.set(col, "新值" + randomStr());
            }
        }
        rvContent.post(() -> {
            RecyclerView.ViewHolder vh = rvContent.findViewHolderForAdapterPosition(row);
            if (vh instanceof ContentAdapter.RowViewHolder) {
                ContentAdapter.RowViewHolder rowVH = (ContentAdapter.RowViewHolder) vh;
                if (rowVH.rowAdapter != null) {
                    rowVH.rowAdapter.setRow(rowData, columnsToUpdate);
                }
            }
        });
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPolling) return;
            int row = pollRowIndex;
            List<Integer> cols = new ArrayList<>();
            cols.add(1);
            cols.add(2);
            Log.d(TAG, "pollRunnable: pollRowIndex=" + pollRowIndex);
            updateRowValues(row, cols);
            pollRowIndex = (pollRowIndex + 1) % contentList.size();
            pollHandler.postDelayed(this, 1000L);
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