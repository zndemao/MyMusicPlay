package com.android.application.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import org.litepal.crud.DataSupport;

/**
 * Created by Lot on 2017/3/8.
 */

public class ScanMusic {
    private static final String TAG = "ScanMusic";

    /**
     * @param mContext
     * @return 多少条目
     */
    public int query(Context mContext) {

        //创建ArryList
        ArrayList<Music> arrayList;
        //实例化ArryList对象
        arrayList = new ArrayList<Music>();
        //创建一个扫描游标
        Cursor c = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (c != null) {

            //创建Model对象
            Music model;
            //循环读取
            //实例化Model对象

            while (c.moveToNext()) {

                model = new Music();
                //扫描本地文件，得到歌曲的相关信息
                String music_name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String music_singer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));


                //设置值到Model的封装类中
                model.setName(music_name);
                model.setAuthor(music_singer);
                model.setSourece(path);
                //将model值加入到数组中
                model.save();
                arrayList.add(model);

            }
            //打印出数组的长度
        }
        Log.d(TAG, "扫描到" + arrayList.size() + "首音乐");
//        List<Music> mus = DataSupport.findAll(Music.class);
//        Log.d(TAG, "query: 000");
//        for (Music mu : mus) {
//            Log.d(TAG, "query: "+mu.getName());
//            Log.d(TAG, "query: "+mu.getAuthor());
//            Log.d(TAG, "query: "+mu.getSourece());
//        }
       // findMusic();
        //得到一个数组的返回值
        return arrayList.size();

    }

    public List findMusic() {
        List<Music> musics = DataSupport.findAll(Music.class);
        ArrayList<Music> arrayListMusic = new ArrayList<Music>();
        Log.d(TAG, "query: 000");
        for (Music music : musics) {
//            Log.d(TAG, "query: "+mu.getName());
//            Log.d(TAG, "query: "+mu.getAuthor());
//            Log.d(TAG, "query: "+mu.getSourece());
            arrayListMusic.add(new Music(music.getName(), music.getAuthor(), music.getSourece()));
        }
        return arrayListMusic;
    }
//    public static ArrayList<LrcModel> redLrc(String path) {
//        ArrayList<LrcModel> alist = new ArrayList<LrcModel>();
//
//        File f = new File(path.replace(".mp3", ".lrc"));
//
//        try {
//            FileInputStream fs = new FileInputStream(f);
//            InputStreamReader inputStreamReader = new InputStreamReader(fs,
//                    "utf-8");
//            BufferedReader br = new BufferedReader(inputStreamReader);
//            String s = "";
//            while (null != (s = br.readLine())) {
//                if (!TextUtils.isEmpty(s)) {
//                    LrcModel lrcModle = new LrcModel();
//                    String lylrc = s.replace("[", "");
//                    String data_ly[] = lylrc.split("]");
//                    if (data_ly.length > 1) {
//                        String time = data_ly[0];
//                        lrcModle.setTime(LrcData(time));
//                        String lrc = data_ly[1];
//                        lrcModle.setLrc(lrc);
//                        alist.add(lrcModle);
//                    }
//
//                }
//
//            }
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return alist;
//
//    }
//
//    public static int LrcData(String time) {
//        time = time.replace(":", "#");
//        time = time.replace(".", "#");
//        String mTime[] = time.split("#");
//        int mtime = Integer.parseInt(mTime[0]);
//        int stime = Integer.parseInt(mTime[1]);
//        int mitime = Integer.parseInt(mTime[2]);
//        int ctime = (mtime * 60 + stime) * 1000 + mitime * 10;
//        return ctime;
//    }
}
