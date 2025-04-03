package com.zzt.util;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author: zeting
 * @date: 2025/4/3
 */
public class DownTimeUtils {
    private static final String TAG = DownTimeUtils.class.getSimpleName();

    public static void main(String[] args) {
        long millis = 60 * 60 * 1000L;
        logTime(millis);
        logD(TAG, convertSecondsToHMmSs(millis / 1000L));
    }

    public static void logTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        String timeStrV3 = String.format("剩余多少秒:%s > %02d : %02d : %02d", String.valueOf(millis), hours, minutes, seconds);

        logD(TAG, timeStrV3);

        logD(TAG, "toHours :" + hours);
        logD(TAG, "toMinutes :" + TimeUnit.MILLISECONDS.toMinutes(millis));
        logD(TAG, "toSeconds :" + TimeUnit.MILLISECONDS.toSeconds(millis));

        logD(TAG, "HOURS-MILLISECONDS :" + TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        logD(TAG, "MINUTES-MILLISECONDS :" + TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static void logD(String tag, String... str) {
        System.out.println(tag + " : " + Arrays.toString(str));
    }

}
