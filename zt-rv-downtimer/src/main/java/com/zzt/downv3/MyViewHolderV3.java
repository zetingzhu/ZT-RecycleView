package com.zzt.downv3;

/**
 * @author: zeting
 * @date: 2025/4/3
 */

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.downtimer.R;
import com.zzt.entiy.ItemTitle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyViewHolderV3 extends RecyclerView.ViewHolder {

    private CountDownTimer currentTimer;
    private TextView mTvTitleName;
    private TextView tv_time;
    private ItemTitle currentItem;

    public MyViewHolderV3(@NonNull View itemView) {
        super(itemView);
        mTvTitleName = (TextView) itemView.findViewById(R.id.title_name);
        tv_time = itemView.findViewById(R.id.tv_time);
    }


    public void bind(ItemTitle item) {
        currentItem = item;

        mTvTitleName.setText(item.getTitle() + " > " + formatDate(item.getTime()));

        startOrUpdateTimer();
    }

    private void startOrUpdateTimer() {
        if (currentItem == null) {
            return;
        }

        if (currentTimer != null) {
            currentTimer.cancel();
        }

        long remaining = currentItem.getTime() - System.currentTimeMillis();
        if (remaining <= 0) {
            tv_time.setText("Expired");
            currentItem.setRemainingTime(0);
            return;
        }
        currentItem.setRemainingTime(remaining); // Update remaining time in the model

        currentTimer = new CountDownTimer(currentItem.getRemainingTime(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentItem.setRemainingTime(millisUntilFinished);
                String formattedTime = String.format("倒计时:%s  %02d:%02d:%02d",
                        currentItem.getRemainingTime(),
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                tv_time.setText(formattedTime);
            }

            @Override
            public void onFinish() {
                tv_time.setText("Expired");
                currentItem.setRemainingTime(0);
            }
        };
        currentTimer.start();
    }

    public void clearTimer() {
        if (currentTimer != null) {
            currentTimer.cancel();
            currentTimer = null;
        }
    }

    public String formatDate(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millis);
        return formatter.format(date);
    }

}