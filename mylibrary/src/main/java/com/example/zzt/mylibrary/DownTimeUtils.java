package com.example.zzt.mylibrary;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * @author: zeting
 * @date: 2025/4/3
 */
public class DownTimeUtils {
    private static final String TAG = DownTimeUtils.class.getSimpleName();

    public static void logTime() {
        long millis = 60 * 60 * 1000L;
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        String timeStrV3 = String.format("剩余多少秒:%s > %02d : %02d : %02d", String.valueOf(millis), hours, minutes, seconds);

        Log.d(TAG, timeStrV3);

        Log.d(TAG, "toHours :" + hours);
        Log.d(TAG, "toMinutes :" + TimeUnit.MILLISECONDS.toMinutes(millis));
        Log.d(TAG, "toSeconds :" + TimeUnit.MILLISECONDS.toSeconds(millis));

        Log.d(TAG, "HOURS-MILLISECONDS :" + TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(millis)));
        Log.d(TAG, "MINUTES-MILLISECONDS :" + TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }

}
