package com.android.application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.music.Music;
import com.android.application.music.MusicAdapter;
import com.android.application.music.MusicEditor;
import com.android.application.music.MusicPlay;
import com.android.application.music.MusicPlayAuxiliary;
import com.android.application.music.ScanMusic;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    //    MusicPlay musicPlay;
    private DrawerLayout mDrawerLayout;
    private List<Music> music = new ArrayList<>();
    private MusicAdapter musicAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;//刷新
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private static Music mMusic1;//正在播放音乐的对象
    private static ImageButton musicStart;//音乐播放或者暂停
    private static TextView musicName;//正在播放的音乐名称
    private static TextView musicAuthor;//正在播放的音乐的歌手
    private static boolean play;//音乐是否在播放
    public static Context content;
    public static final int UPDATE = 1;
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    mMusic1 = MusicEditor.read(content);
                    musicName.setText(mMusic1.getName());
                    musicAuthor.setText(mMusic1.getAuthor());
                    boolean play = MusicPlayAuxiliary.getPlayCache();//只获取音乐的播放状态，不调用play方法对音乐进行操作
                    if (play) {
                        musicStart.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    } else {//ic_play_arrow_black_36dp
                        musicStart.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //音乐比方结束的监听
        MusicPlay.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Log.d(TAG, "onCompletion: ");
            }
        });
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        MusicPlay.onCompletion1(this);
//        MusicPlay.on(this);
        content = this;
        //获取权限
        initAuthority();
        //设置标题栏
        Toolbar titleToolbar = (Toolbar) findViewById(R.id.tb_titleToolbar);
        setSupportActionBar(titleToolbar);
        //初始化UI
        initUI();
        //初始化播放栏
        initUIBar();
    }

    /**
     * 初始化播放栏
     */
    private void initUIBar() {
        musicName = (TextView) findViewById(R.id.tv_musicName);
        musicAuthor = (TextView) findViewById(R.id.tv_musicAuthor);
        musicStart = (ImageButton) findViewById(R.id.ib_musicStartOrStop);
        mMusic1 = MusicEditor.read(MainActivity.this);
        refreshBar();

    }

    //更新底部播放栏
    public void refreshBar() {
//        mMusic1 = MusicEditor.read(this);
        musicName.setText(mMusic1.getName());
        musicAuthor.setText(mMusic1.getAuthor());
//        boolean play = MusicPlayAuxiliary.play(MainActivity.this, mMusic1);
        if (play) {
            musicStart.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {
            musicStart.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
    }

    /**
     * 获取读取文件的权限
     */
    private void initAuthority() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
//            musicPlay = new MusicPlay();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    musicPlay = new MusicPlay();
                } else {
                    Toast.makeText(this, "拒绝访问权限，无法运行", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 初始化DrawerLayout控件，
     */
    private void initUI() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_allMusic);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_allMusic:
                        Toast.makeText(MainActivity.this, "All Music", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_listMusic:
                        Toast.makeText(MainActivity.this, "List Music", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        //初始化RecyclerView
        initUIRecycler();
        //初始化播放的事件
        initUIPlay();
    }

    /**
     * 初始化播放的事件
     */
    private void initUIPlay() {
        ImageView musicImage = (ImageView) findViewById(R.id.iv_musicImage);
        ImageButton musicStartOrStop = (ImageButton) findViewById(R.id.ib_musicStartOrStop);
        musicImage.setOnClickListener(this);
        musicStartOrStop.setOnClickListener(this);
    }

    private void initUIRecycler() {
        //假数据
//        List<Music> musics = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Music music = new Music("南方姑娘", "怕上火", "");
//            musics.add(music);
//        }
//        new ScanMusic().query(MainActivity.this);

        initMusicData();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(music);
        recyclerView.setAdapter(musicAdapter);
        //刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMusic();
            }
        });

    }

    private void initMusicData() {
        music.clear();
        music = new ScanMusic().findMusic();
    }

    private void refreshMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMusicData();
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        musicAdapter = new MusicAdapter(music);
                        recyclerView.setAdapter(musicAdapter);
                        musicAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
//                Message message = new Message();
//                handler.sendMessage(message);
//                Log.d(TAG, "run: ");
            }
        }).start();
    }

    /**
     * 菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 菜单触摸
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_scanningLocalMusic://扫描本地音乐
                ScanMusic scanMusic = new ScanMusic();
                int ret = scanMusic.query(MainActivity.this);
                refreshMusic();
                Toast.makeText(this, "扫描到" + ret + "首音乐", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_scanningSpecifyFileMusic://扫描指定文件夹里的音乐
                Toast.makeText(this, "find specify file", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_musicImage://跳转到播放详情页面
                startActivity(new Intent(this, PlayActivity.class));
                break;
            case R.id.ib_musicStartOrStop://点击改变播放或暂停状态
//                MusicPlay musicPlay = new MusicPlay();
//                musicPlay.start("");
                if (MusicPlayAuxiliary.returnPlayCache() != null) {//在第一次播放时，mediaPlayer未进行创建，music为null，故进行非空判断
                    mMusic1 = MusicPlayAuxiliary.returnPlayCache();
                }
                play = MusicPlayAuxiliary.play(MainActivity.this, mMusic1);
                if (play) {
                    musicStart.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                } else {
                    musicStart.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                }
                //设置播放图片

                Toast.makeText(this, "stop or start", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MusicPlay.Destroy();
    }

    public void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();
    }

    //活动从后台回到前台
    @Override
    protected void onRestart() {
        mMusic1 = MusicEditor.read(this);
        play = MusicPlayAuxiliary.getPlayCache();
        refreshBar();
        Log.d(TAG, "onRestart: " + mMusic1.getName());
        //会到前台发handler更新界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what= MainActivity.UPDATE;
                MainActivity.handler.sendMessage(message);
            }
        }).start();

        super.onRestart();
    }

//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Log.d(TAG, "onCompletion: main");
//    }
//
//    //音乐播放结束

}
