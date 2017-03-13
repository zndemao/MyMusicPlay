package com.android.application.music;

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.application.MainActivity;
import com.android.application.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private static final String TAG = "MusicAdapter";
    public static List<Music> mMusic;
    public static int musicSize;
    public static int musicPosition;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View musicView;
        TextView no;
        TextView name;
        TextView author;
        ImageView setting;

        public ViewHolder(View itemView) {
            super(itemView);
            musicView = itemView;
            no = (TextView) itemView.findViewById(R.id.tv_musicListNO);
            name = (TextView) itemView.findViewById(R.id.tv_musicListName);
            author = (TextView) itemView.findViewById(R.id.tv_musicListAuthor);
            setting = (ImageView) itemView.findViewById(R.id.im_musicListSetting);
        }
    }

    public MusicAdapter(List<Music> listMusic) {
        mMusic = listMusic;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tem, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //设置点击事件
        holder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放音乐
                int position = holder.getAdapterPosition();
                Music music = mMusic.get(position);
//                String sourece = music.getSourece();
                MusicPlayAuxiliary.play(parent.getContext(),music);
                Toast.makeText(parent.getContext(),music.getName()+"", Toast.LENGTH_SHORT).show();
//                new MainActivity().refreshBar();

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
                //将播放的位置给music供其在列表播放/循环时候使用
                musicPosition = position;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what= MainActivity.UPDATE;
                        MainActivity.handler.sendMessage(message);
                    }
                }).start();
            }
        });
        holder.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击对音乐进行操作，删除收藏等
                Toast.makeText(parent.getContext(), "Image setting", Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music music = mMusic.get(position);
        holder.no.setText(position + "");
        holder.name.setText(music.getName());
        holder.author.setText(music.getAuthor());
    }

    @Override
    public int getItemCount() {
        musicSize = mMusic.size();
        return mMusic.size();
    }
    public static int getSetMusicPositionSet() {
//        musicPosition += 1;
        return musicPosition;
    }
}
