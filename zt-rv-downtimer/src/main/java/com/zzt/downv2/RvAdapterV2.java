package com.zzt.downv2;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class RvAdapterV2 extends RecyclerView.Adapter<RvAdapterV2.MyHolder> {

    private static final String TAG = RvAdapterV2.class.getSimpleName();

    List<ItemTitle> mList;
    private Map<Integer, CountDownTimer> timers = new HashMap<>();

    public RvAdapterV2(List<ItemTitle> mList) {
        this.mList = mList.isEmpty() ? new ArrayList<>() : mList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_title_layout, parent, false));
    }

    @Override
    public void onViewRecycled(@NonNull MyHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        if (mList != null) {
            ItemTitle item = mList.get(pos);
            if (item != null) {
                holder.mTvTitleName.setText(item.getTitle() + " > " + formatDate(item.getTime()));

                // 停止之前的倒计时
                if (timers.containsKey(position)) {
                    timers.get(position).cancel();
                }

                long remainingTime = item.getTime() - System.currentTimeMillis();
                CountDownTimer timer = new CountDownTimer(remainingTime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

//                        long time = millisUntilFinished / 1000; //变成秒
//                        long temp = time % (24 * 3600);
//                        long day = time / (24 * 3600); //天
//                        long hour = temp / 3600; //小时
//                        long minute = temp % 3600 / 60; //分钟
//                        long second = temp % 60; //秒
//                        String timeStr = "";
//                        if (day == 0) {
//                            timeStr = "反馈剩余时间:" + hour + "时" + minute + "分" + second + "秒";
//                        } else {
//                            timeStr = "反馈剩余时间:" + day + "天" + hour + "时" + minute + "分" + second + "秒";
//                        }


                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


//String timeStrV2 = String.format("%02d:%02d",
//        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
//        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));


                        String timeStrV3 = String.format("剩余多少秒:%s > %02d : %02d : %02d", String.valueOf(millisUntilFinished), hours, minutes, seconds);
                        holder.tv_time.setText(timeStrV3);
                    }

                    @Override
                    public void onFinish() {
                        holder.tv_time.setText("00:00");
                    }
                }.start();
                timers.put(position, timer);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.isEmpty() ? 0 : mList.size();
    }

    public void stopAllTimers() {
        if (timers != null) {
            for (CountDownTimer timer : timers.values()) {
                if (timer != null) {
                    timer.cancel();
                }
            }
            timers.clear();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitleName;
        private TextView tv_time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitleName = (TextView) itemView.findViewById(R.id.title_name);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    public String formatDate(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millis);
        return formatter.format(date);
    }

}