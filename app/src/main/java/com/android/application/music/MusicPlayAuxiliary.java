package com.android.application.music;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.android.application.MainActivity;
import com.android.application.content.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 音乐播放辅助
 * Created by Lot on 2017/3/9.
 */

public class MusicPlayAuxiliary {
    public static final String TAG = "MusicPlayAuxiliary";
    public static MusicPlay musicPlay = new MusicPlay();
    public static String pathCache = "";//音乐暂存,被播放的隐喻我路径
    public static boolean playCache = false;//播放状态，默认false，没有播放
    public static int musicPlayLength = 0;//音乐播放了多长
    public static Music music;//music播放对象，他应该属于这个类，每一次调用play方法时候，都会对music进行对象修改

    //创建一个集合用于存放播放列表，供上一曲时使用
    public static List<Music> upMusic = new ArrayList<>();//未进行测试，效果未知
    //添加了几个
    public static int addUpMusicNumber = 0;

    /**
     * 传入你要播放的音乐，此方法会进行判断 播放 或 者暂停播放。
     *
     * @param musicNew 播放的音乐完整信息
     * @return 音乐播放的结果， 播放/false 暂停
     */
    public static boolean play(Context context, Music musicNew) {
        music = musicNew;
        Log.d(TAG, "play: ");
        if (pathCache.equals(music.getSourece())) {//如果要播放的音乐和正在播放的音乐一样，那就暂停
            if (playCache) {
                Log.d(TAG, "play: " + musicPlayLength);
                musicPlayLength = musicPlay.pause();
                playCache = false;
                Log.d(TAG, "play: 暂停");
            } else {
                Log.d(TAG, "play: " + musicPlayLength);
                musicPlay.resumePlay(musicPlayLength);
                playCache = true;
                Log.d(TAG, "play: 播放");
            }
        } else {
            musicPlay.release();
            musicPlay = new MusicPlay();
            musicPlay.start(music.getSourece());
            playCache = true;
            Log.d(TAG, "play: 第一次播放");

            //如果有音乐播放就把音乐放到upMuscic中.只在执行第一次播放的时候添加
            addUpMusic(music);
        }
        pathCache = music.getSourece();
        //以共享参数的方式进行存储
        MusicEditor.save(context, music);

        return playCache;//音乐的播放状态
    }

    private static final String TAG1 = "upMusic";

    /**
     * 如果有音乐播放就把音乐放到upMuscic中
     *
     * @return 正在播放的音乐对象
     */
    public static int addUpMusic(Music m) {//如果要播放的音乐和正在播放的音乐一样，那就暂停
        upMusic.add(m);
        addUpMusicNumber += 1;
        Log.d(TAG1, "addUpMusic: " + m.getSourece());
//        Log.d(TAG1, "addUpMusic: addUpMusicNumber=" + addUpMusicNumber);
//        Log.d(TAG1, "addUpMusic: upMusic.size=" + upMusic.size());
        return addUpMusicNumber;
    }

    /**
     * 点击上一曲时候吧upMusic里的音乐对象删除
     */
    public static void deleteUpMusic(int UpMusicNumber) {
        upMusic.remove(UpMusicNumber);
        Log.d(TAG1, "addUpMusic: upMusic.size=" + upMusic.size());
    }

    /**
     * 点击上一曲时候调用此方法
     */
    public static void upMusic() {
        if (upMusic.size() == 1) {//如果等于1和0说明到大顶部不能执行
            Log.d(TAG1, "upMusic: 已经到顶部" + upMusic.size());
            return;
        }
        if (upMusic.size() == 0) {//如果等于1和0说明到大顶部不能执行
            Log.d(TAG1, "upMusic: 还没有播放音乐" + upMusic.size());
            return;
        }
        //去播放addUpMusic-1的音乐，并把add从音乐list中删除
        music = upMusic.get(addUpMusicNumber - 2);//修改正在播放的音乐对象

        musicPlay.release();
        musicPlay = new MusicPlay();
        musicPlay.start(MusicPlayAuxiliary.music.getSourece());
        playCache = true;
        Log.d(TAG1, "upMusic: 上一曲");
        MusicEditor.save(MyApplication.getContext(), music);
        addUpMusicNumber = addUpMusicNumber - 1;//将音乐从list中删除，并修改addUpMusicNumber的值
        deleteUpMusic(addUpMusicNumber);//将音乐从list中删除
        Log.d(TAG1, "upMusic: 将音乐从list中删除");
        for (Music m : upMusic) {
            Log.d(TAG, "upMusic: " + m.getName());
        }
    }

    /**
     * 获取音乐播放的状态
     *
     * @return 返回音乐的播放状态， true 播放/false 暂停
     */
    public static boolean getPlayCache() {
        return playCache;
    }

    /**
     * 获取音乐播放的对象
     *
     * @return 返回播放music对象
     */
    public static Music returnPlayCache() {
        return music;
    }

    /**
     * @return 音乐的长度，可以播放多久
     */
    public static long getDuration() {
        return MusicPlay.getLength();
    }

    public static void nextMusic(PlayMode playMode) {
        switch (playMode) {
            case SINGLE_LOOP://单曲循环
//                musicPlay.resumePlay(0);
//                playCache = true;
//                break;
            case LIST_LOOP://list 循环
            case ORDER_LOOP://与列表循环一样
                Music read = MusicEditor.read(MyApplication.getContext());
                Log.d(TAG, "onCompletion: " + read.getSourece());
                List<Music> mMusic = MusicAdapter.mMusic;
                int temp = 0;
                for (Music m : mMusic) {
                    temp += 1;
                    if (m.getSourece().equals(read.getSourece())) {
                        break;
                    }
                }
                Log.d(TAG, "onCompletion: " + temp);
                Music mus;
                if (temp == mMusic.size()) {
                    Log.d(TAG, "nextMusic: " + temp + "=" + mMusic.size());
                    mus = mMusic.get(0);
                } else {
                    mus = mMusic.get(temp);
                }
                Log.d(TAG, "onCompletion: " + mus.getSourece());
                MusicPlayAuxiliary.play(MyApplication.getContext(), mus);
                break;
            case RANDOM_LOOP://随机播放
                int musicSize = MusicAdapter.musicSize;//音乐的总数
                Random random = new Random();
                int tempRandom = random.nextInt(musicSize);
                Music music = MusicAdapter.mMusic.get(tempRandom);
                //产生随机执行第一次播放
                musicPlay.release();
                musicPlay = new MusicPlay();
                musicPlay.start(music.getSourece());
                playCache = true;
                Log.d(TAG, "play: 第一次播放");

                addUpMusic(music);
                //保存音乐状态
                MusicEditor.save(MyApplication.getContext(), music);

//                MusicPlayAuxiliary.play(MyApplication.getContext(), music);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Message message = new Message();
//                        message.what= MainActivity.UPDATE;
//                        MainActivity.handler.sendMessage(message);
//                    }
//                }).start();
                break;
//            case ORDER_LOOP://顺序播放
////                int musicPosition = MusicAdapter.musicPosition;
////                if (musicPosition == MusicAdapter.musicSize) {//如果正在播放的音乐事最后一个，那就暂停
////                    return;//那就不处理啦
////                }
////                Log.d(TAG, "onCompletion: "+musicPosition);
////                Music music_ = MusicAdapter.mMusic.get(musicPosition+1);
////                MusicPlayAuxiliary.play(MyApplication.getContext(), music_);
////                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        Message message = new Message();
////                        message.what= MainActivity.UPDATE;
////                        MainActivity.handler.sendMessage(message);
////                    }
////                }).start();
//                break;
        }
    }

    /**
     * 移动进度条进行播放
     *
     * @param progress 进度
     */
    public static void mobileProgressPlay(long progress) {
        musicPlay.resumePlay((int) progress);
    }

    /**
     * 获取进度
     */
    public static int getMusicProgress() {
        musicPlayLength = musicPlay.pause();
        return musicPlayLength;
    }
    public static int getMusicPlayLength(){
        int musicLength =(int) musicPlay.getMusicLength();
        return musicLength;
    }
}
