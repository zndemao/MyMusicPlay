package com.android.application.music;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.application.MainActivity;
import com.android.application.content.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Lot on 2017/3/4.
 */

public class MusicPlay {
    private static final String TAG = "MusicPlay";
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean musicStatus = false;
    public static PlayMode playMode = PlayMode.LIST_LOOP;
//    static Context context;


//    public MusicPlay() {
//        this.mediaPlayer = new MediaPlayer();
//        initMusic("");
//    }

    public MusicPlay() {
        mediaPlayer = new MediaPlayer();//构建MediaPlayer对象
        Log.d(TAG, "MusicPlay: ");
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ");
                playMode = MusicEditor.getPlayMode(MyApplication.getContext());
                //进行音乐播放情况的判断
                switch (playMode) {
                    case SINGLE_LOOP://单曲循环
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        break;
                    case LIST_LOOP://list 循环
                        Music read_ = MusicEditor.read(MyApplication.getContext());//获取播放的对象
                        Log.d(TAG, "onCompletion: " + read_.getSourece());
                        List<Music> mMusic_ = MusicAdapter.mMusic;//
                        int temp_ = 0;
                        for (Music m : mMusic_) {
                            temp_ += 1;
                            if (m.getSourece().equals(read_.getSourece())) {
                                break;
                            }
                        }
                        Log.d(TAG, "onCompletion: " + temp_);
                        Music mus_;
                        if (temp_ == mMusic_.size()) {
                            Log.d(TAG, "nextMusic: " + temp_ + "=" + mMusic_.size());
                            mus_ = mMusic_.get(0);
                        } else {
                            mus_ = mMusic_.get(temp_);
                        }
                        Log.d(TAG, "onCompletion: "+mus_.getSourece());
                        if (!MusicPlayAuxiliary.getPlayCache()) {
                            return;
                        }
                        MusicPlayAuxiliary.play(MyApplication.getContext(), mus_);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what= MainActivity.UPDATE;
                                MainActivity.handler.sendMessage(message);
                            }
                        }).start();
                        break;
                    case RANDOM_LOOP://随机播放
                        int musicSize = MusicAdapter.musicSize;
                        Random random = new Random();
                        int i = random.nextInt(musicSize);
                        Music music = MusicAdapter.mMusic.get(i);
                        if (!MusicPlayAuxiliary.getPlayCache()) {
                            return;
                        }
                        MusicPlayAuxiliary.play(MyApplication.getContext(), music);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = MainActivity.UPDATE;
                                MainActivity.handler.sendMessage(message);
                            }
                        }).start();
                        break;
                    case ORDER_LOOP://顺序播放
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
                        Music mus;//=mMusic.get(0);
                        if (temp == mMusic.size()) {
//                            Log.d(TAG, "nextMusic: " + temp + "=" + mMusic.size());
//                            mus = mMusic.get(0);
                            return;
                        } else {
                            mus = mMusic.get(temp);
                        }
                        Log.d(TAG, "onCompletion: "+mus.getSourece());
                        if (!MusicPlayAuxiliary.getPlayCache()) {
                            return;
                        }
                        MusicPlayAuxiliary.play(MyApplication.getContext(), mus);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what= MainActivity.UPDATE;
                                MainActivity.handler.sendMessage(message);
                            }
                        }).start();
                        break;
                }
            }
        });
    }

    /**
     * @return 当前播放状态
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * @param fileString 音乐位置
     */
    public void initMusic(String fileString) {
        try {
            File file = new File(fileString);
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();

        } catch (Exception e) {
            Log.d(TAG, "initPlay: 播放音乐出错");
            e.printStackTrace();
        }
    }

    public void start(String fileString) {
        Log.d(TAG, "start: ");
        try {
            mediaPlayer.setDataSource(fileString);//设置文件路径
            mediaPlayer.prepare();//准备
            mediaPlayer.start();//开始播放
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        } else {
//            mediaPlayer.pause();
//        }
    }

    /**
     * 暂停
     */
    public int pause() {
        Log.d(TAG, "pause: ");
        int playLength = mediaPlayer.getCurrentPosition(); //播放了多久
        mediaPlayer.pause();
        return playLength;
    }
    public long getMusicLength() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        return currentPosition;
    }

    public void resumePlay(int playLength) {
        Log.d(TAG, "resumePlay: " + playLength);
        mediaPlayer.seekTo(playLength);
        mediaPlayer.start();
    }

    public void Destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * 销毁重新播放
     */
    public void release() {
        mediaPlayer.release();
    }

    /**
     * @return 音乐的长度
     */
    public static int getLength() {
        return mediaPlayer.getDuration();
    }



}
