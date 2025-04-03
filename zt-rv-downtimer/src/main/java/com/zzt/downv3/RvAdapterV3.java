package com.zzt.downv3;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.downtimer.R;
import com.zzt.entiy.ItemTitle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: zeting
 * @date: 2025/4/3
 */
public class RvAdapterV3 extends RecyclerView.Adapter<MyViewHolderV3> {

    private static final String TAG = RvAdapterV3.class.getSimpleName();

    List<ItemTitle> mList;
    private Map<Integer, CountDownTimer> timers = new HashMap<>();

    public RvAdapterV3(List<ItemTitle> mList) {
        this.mList = mList.isEmpty() ? new ArrayList<>() : mList;
    }

    @NonNull
    @Override
    public MyViewHolderV3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderV3(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_title_layout, parent, false));
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolderV3 holder) {
        super.onViewRecycled(holder);
        holder.clearTimer(); // Cancel the timer when the view is recycled
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderV3 holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        if (mList != null) {
            ItemTitle item = mList.get(pos);
            if (item != null) {
                holder.bind(item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.isEmpty() ? 0 : mList.size();
    }

}