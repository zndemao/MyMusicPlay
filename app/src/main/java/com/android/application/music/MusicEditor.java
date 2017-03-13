package com.android.application.music;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lot on 2017/3/10.
 */

public class MusicEditor {
    public static void save(Context context, Music music) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Music", Context.MODE_PRIVATE).edit();
        editor.putString("name", music.getName());
        editor.putString("author", music.getAuthor());
        editor.putString("path", music.getSourece());
        editor.apply();
    }

    public static Music read(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String author = preferences.getString("author", "");
        String path = preferences.getString("path", "");
//        preferences.
        return new Music(name, author, path);
    }

    /**
     * 设置播放模式
     * @param context
     * @param mode
     */
    public static void setPlayMode(Context context, PlayMode mode) {
        SharedPreferences.Editor playMode = context.getSharedPreferences("mode", Context.MODE_PRIVATE).edit();
        int tempMode = mode(mode);
        playMode.putInt("playMode", tempMode);
        playMode.apply();
    }

    /**
     * @param context
     * @return  返回播放模式
     */
    public static PlayMode getPlayMode(Context context) {
        SharedPreferences mode = context.getSharedPreferences("mode", Context.MODE_PRIVATE);
        int tempMode = mode.getInt("playMode", 2);
        PlayMode playMode = PlayMode.RANDOM_LOOP;
        switch (tempMode) {
            case 0:
                playMode = PlayMode.SINGLE_LOOP;
                break;
            case 1:
                playMode = PlayMode.LIST_LOOP;
                break;
            case 2:
                playMode = PlayMode.RANDOM_LOOP;
                break;
            case 3:
                playMode = PlayMode.ORDER_LOOP;
                break;
        }
        return playMode;
    }

    public static int mode(PlayMode mode) {
        int tempMode = 2;
        switch (mode) {
            case SINGLE_LOOP://单曲循环
                tempMode = 0;
                break;
            case LIST_LOOP://list 循环
                tempMode = 1;
                break;
            case RANDOM_LOOP://随机播放
                tempMode = 2;
                break;
            case ORDER_LOOP://顺序播放
                tempMode = 3;
                break;
        }
        return tempMode;
    }
}
