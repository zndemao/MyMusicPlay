package com.android.application;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.help.MyToast;
import com.android.application.music.Music;
import com.android.application.music.MusicEditor;
import com.android.application.music.MusicPlay;
import com.android.application.music.MusicPlayAuxiliary;
import com.android.application.music.PlayMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";
    private int tempPlayMode = 2;
    Toast toast;
    static int musicProgress = 0; //音乐播放的进度

    Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (MusicPlayAuxiliary.getPlayCache()) {
                Log.d("run bug", "run: ");
                seekBar.setProgress(MusicPlayAuxiliary.getMusicPlayLength());
                startText.setText(timeParse(MusicPlayAuxiliary.getMusicPlayLength()));
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            }
        }
    };

//    public static Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Runnable updateThread = new Runnable() {
//                // 获得歌曲现在播放位置并设置成播放进度条的值
//                if (MusicPlayAuxiliary.getPlayCache()) {
//                    seekBar.setProgress(MusicPlayAuxiliary.getMusicProgress());
//                    // 每次延迟100毫秒再启动线程
//                    handler.postDelayed(handler, 100);
//                }
//            }
////            switch (msg.what) {
////                case 1://更新进度
////                    seekBar.setProgress(MusicPlayAuxiliary.getMusicProgress());
////                    Log.d(TAG, "handleMessage: ");
////                    break;
////            }
////            super.handleMessage(msg);
//        }
//    };

    @BindView(R.id.ib_back)
    ImageButton back;//返回
    @BindView(R.id.tv_PlayMusicName)
    TextView playMusicName;//音乐的名字
    @BindView(R.id.startText)
    TextView startText;//音乐播放了多久
    @BindView(R.id.endText)
    TextView endText;//音乐的总长度
    //    @BindView(R.id.seekBar_length)
//     SeekBar seekBar;//音乐的播放进度
    @BindView(R.id.ib_playOrStop)//音乐的播放或者暂停
            ImageButton musicCache;
    @BindView(R.id.im_playState)
    ImageButton playState;//音乐显示的播放模式
    private Music mMusic;//在共享参数中的音乐对象
    private boolean play;//音乐是否在播放
    private PlayMode playMode;//播放模式
    private static SeekBar seekBar;

    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }

    @OnClick(R.id.im_playState)
    void playState() {
        tempPlayMode += 1;
        tempPlayMode = tempPlayMode % 4;
//        Log.d(TAG, "playState: " + tempPlayMode);
        switch (tempPlayMode) {
            case 0:
                playMode = PlayMode.SINGLE_LOOP;
                playState.setImageResource(R.drawable.ic_repeat_one_black_48dp);
                toast.setText("单曲循环");
                toast.show();
                break;
            case 1:
                playMode = PlayMode.LIST_LOOP;
                playState.setImageResource(R.drawable.ic_loop_black_48dp);
                toast.setText("列表循环");
                toast.show();
                break;
            case 2:
                playMode = PlayMode.RANDOM_LOOP;
                playState.setImageResource(R.drawable.ic_shuffle_black_48dp);
                toast.setText("随机播放");
                toast.show();
                break;
            case 3:
                playMode = PlayMode.ORDER_LOOP;
                playState.setImageResource(R.drawable.ic_playlist_play_black_48dp);
                toast.setText("顺序播放");
                toast.show();
                break;
        }
        MusicEditor.setPlayMode(this, playMode);
    }

    //暂停或者继续播放
    @OnClick(R.id.ib_playOrStop)
    void playOrStop() {
        play = MusicPlayAuxiliary.getPlayCache();
        Log.d(TAG, "playOrStop: " + play);
//        if (!play) {
        play = MusicPlayAuxiliary.play(this, mMusic);
//        }
        if (play) {
            musicCache.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {
            musicCache.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
//        Toast.makeText(this, "play stop", Toast.LENGTH_SHORT).show();
    }

    //上一曲
    @OnClick(R.id.ib_upMusic)
    void upMusic() {
        MusicPlayAuxiliary.upMusic();
        initReFresh();
    }

    //下一曲
    @OnClick(R.id.ib_downMusic)
    void downMusic() {
        playMode = MusicEditor.getPlayMode(this);//获取播放模式
        MusicPlayAuxiliary.nextMusic(playMode);
//        Toast.makeText(this, "下一曲", Toast.LENGTH_SHORT).show();
        initReFresh();
    }

    //list
//    @OnClick(R.id.ib_playList)
//    void playList() {
//        Toast.makeText(this, "list", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paly_activity);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        seekBar = (SeekBar) findViewById(R.id.seekBar_length);

        ButterKnife.bind(this);
        initUI();
//        initProgress();
    }

    /**
     * 对进度条的显示
     */
    private void initProgress() {
        Log.d(TAG, "initProgress: ");
        final long duration = MusicPlayAuxiliary.getDuration();
        Log.d(TAG, "initProgress: " + duration);
        seekBar.setMax((int) MusicPlayAuxiliary.getDuration());//设置进度条的长度为音乐的长度
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.d(TAG, "onStopTrackingTouch: 手动滑动调节");
                int progress = seekBar.getProgress();
                //改变播放进度
//                seekBar.setProgress(progress);
//                Log.d(TAG, "onStopTrackingTouch: "+progress);
                MusicPlayAuxiliary.mobileProgressPlay(progress);
            }
        });
        handler.post(updateThread);
//        new Thread(){
//            @Override
//            public void run(){
//                while(MusicPlayAuxiliary.getPlayCache()){
//                    try {
//                        sleep(1000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, "run: ");
//                    Message m = new Message();
//                    m.what = 1;
//                    handler.sendMessage(m);
//                }
//            }
//        }.start();
    }

    private void initUI() {
        mMusic = MusicEditor.read(this);
        Log.d(TAG, "initUI: end");
        //对页面进行刷新
//        MusicPlay.mediaPlayer.setOnCompletionListener(this);
//        MusicPlay.onCompletion(this);
        initUIMode();
        Log.d(TAG, "initUI: initUIMode end");
        initReFresh();
        Log.d(TAG, "initUI: initReFresh end");
//        finish();
//        startActivity(new Intent(this, PlayActivity.class));
    }

    private void initUIMode() {
        playMode = MusicEditor.getPlayMode(this);
        playMode(playMode);
    }

    private void playMode(PlayMode mode) {
        Log.d(TAG, "playMode: mode第一次执行时候出错");
        switch (mode) {
            case SINGLE_LOOP:
                playState.setImageResource(R.drawable.ic_repeat_one_black_48dp);
//                MyToast.makeText(this,"单曲循环");
                break;
            case LIST_LOOP:
                playState.setImageResource(R.drawable.ic_loop_black_48dp);
//                MyToast.makeText(this,"列表循环");
                break;
            case RANDOM_LOOP:
                playState.setImageResource(R.drawable.ic_shuffle_black_48dp);
//                MyToast.makeText(this,"随机播放");
                break;
            case ORDER_LOOP:
                playState.setImageResource(R.drawable.ic_playlist_play_black_48dp);
                //                MyToast.makeText(this,"顺序播放");
                break;
        }
    }

    //对页面进行更新
    private void initReFresh() {
        Log.d(TAG, "initReFresh: 初始化ui");
        mMusic = MusicEditor.read(this);//获取在共享参数中的音乐对象
        playMusicName.setText(mMusic.getName());
        //只获取音乐的播放状态，不调用play方法对音乐进行操作
        play = MusicPlayAuxiliary.getPlayCache();//获取播放状态
        if (play) {
            musicCache.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {//ic_play_arrow_black_36dp
            musicCache.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
        //获取播放音乐的时长
        endText.setText(timeParse(MusicPlayAuxiliary.getDuration()));
//        Log.d("**************", "initReFresh: ");
        //刷新音乐的长度
        initProgress();
//        Log.d("**************", "initReFresh: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MusicPlay.Destroy();
    }

    private static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += 0;
        }
        time += second;
        return time;
    }

    //播放完毕时
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Log.d(TAG, "onCompletion: ");
//    }
    private static void mySleep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
