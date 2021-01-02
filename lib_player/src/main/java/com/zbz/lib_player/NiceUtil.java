package com.zbz.lib_player;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import java.util.Formatter;
import java.util.Locale;

/**
 * 工具类
 */
public class NiceUtil {
    /**
     * Get activity from context object
     *
     * @param context something
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 保存播放位置，以便下次播放时接着上次的位置继续播放.
     *
     * @param context
     * @param url     视频链接url
     */
    public static void savePlayPosition(Context context, String url, long position) {
        context.getSharedPreferences("NICE_VIDEO_PALYER_PLAY_POSITION",
                Context.MODE_PRIVATE)
                .edit()
                .putLong(url, position)
                .apply();
    }

    /**
     * 取出上次保存的播放位置
     *
     * @param context
     * @param url     视频链接url
     * @return 上次保存的播放位置
     */
    public static long getSavedPlayPosition(Context context, String url) {
        return context.getSharedPreferences("NICE_VIDEO_PALYER_PLAY_POSITION",
                Context.MODE_PRIVATE)
                .getLong(url, 0);
    }
}
