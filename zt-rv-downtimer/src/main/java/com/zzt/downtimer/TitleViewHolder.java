package com.zzt.downtimer;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zzt.entiy.ItemTitle;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TitleViewHolder extends RecyclerListAdapter.ViewHolder<ItemTitle> {
    private ImageView mIvTitleImage;
    private TextView mTvTitleName;
    private TextView tv_time;

    public TitleViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_title_layout, parent, false));
        mTvTitleName = (TextView) itemView.findViewById(R.id.title_name);
        mIvTitleImage = (ImageView) itemView.findViewById(R.id.title_image);
        tv_time = itemView.findViewById(R.id.tv_time);
    }

    @Override
    public void bind(ItemTitle item, int position) {
        mTvTitleName.setText(item.getTitle() + " > " + formatDate(item.getTime()));

        display(item);
    }

    public String formatDate(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millis);
        return formatter.format(date);
    }

    //用来判断是倒计时还是正计时
    public boolean isCountdown;
    //倒计时/正计时时间
    public long delay;
    final Handler timerHandler = new Handler(Looper.getMainLooper());

    private Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            delay -= 1000;
            if (delay == 0) {
                isCountdown = false;
            } else {
                isCountdown = true;
            }
            updateTimerState();
        }
    };


    //启动倒计时
    public void startCountdown() {
        timerHandler.postDelayed(countdownRunnable, 1000);
    }

    //结束倒计时
    public void endCountDown() {
        timerHandler.removeCallbacks(countdownRunnable);
    }

    //更新UI
    public void updateTimerState() {
        long time = delay / 1000; //变成秒
        long temp = time % (24 * 3600);
        long day = time / (24 * 3600); //天
        long hour = temp / 3600; //小时
        long minute = temp % 3600 / 60; //分钟
        long second = temp % 60; //秒
        if (isCountdown) {
            if (day == 0) {
                tv_time.setText("反馈剩余时间:" + hour + "时" + minute + "分" + second + "秒");
            } else {
                tv_time.setText("反馈剩余时间:" + day + "天" + hour + "时" + minute + "分" + second + "秒");
            }

            startCountdown();
        } else {
            tv_time.setText("倒计时结束");
            endCountDown();
        }
    }


    public void display(ItemTitle itemTitle) {
        long time = itemTitle.getTime();
        delay = time - System.currentTimeMillis();
        isCountdown = delay > 0;
        updateTimerState();
    }

    /**
     * 滑出屏幕时: 移除倒计时
     */
    public void onRecycled() {
        // 终止计时
        endCountDown();
    }
}
