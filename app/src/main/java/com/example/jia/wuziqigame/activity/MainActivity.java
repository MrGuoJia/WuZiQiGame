package com.example.jia.wuziqigame.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jia.wuziqigame.R;
import com.example.jia.wuziqigame.view.ChessView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ChessView view;
    private ImageView img_restart;
    private ImageView voice;
    private Boolean isFirst=true;
    private List<MediaPlayer> listPlayer=new ArrayList<>();
    private int MusicList[]= new int[]{R.raw.musci1,R.raw.music2};
    private int choose=-1;
    MediaPlayer mp=new MediaPlayer();
    AudioManager manager;
    private Button regret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListPlayer();
        initView();
        initPlayMusic(isFirst);//判断切换音乐

    }



    private void initListPlayer() {
        for(int i=0;i<2;i++){
            MediaPlayer mps=new MediaPlayer();
            listPlayer.add(mps);
        }
    }

    private void initPlayMusic(Boolean isFirst) {
        if(isFirst){
            if(mp!=null){
                mp.stop();
                mp.release();
            }
            mp =MediaPlayer.create(this,MusicList[0]);
            mp.start();
            mp.setLooping(true);
        }else{
            if(mp!=null){
                mp.stop();
                mp.release();
            }
            mp =MediaPlayer.create(this,MusicList[1]);
            mp.start();
            mp.setLooping(true);


        }
    }

    private void initView() {
        view= (ChessView) findViewById(R.id.wuZiQi);
        img_restart= (ImageView) findViewById(R.id.restart);
        voice= (ImageView) findViewById(R.id.voice);
        regret= (Button) findViewById(R.id.tv_regret);

        img_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("是否重新开始游戏");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.reStart();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
       voice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String items[]= {"静音","音乐量：50%","音乐量：100%","切换背景音乐"};
               AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
               builder.setTitle("音乐设置");
               builder.setSingleChoiceItems(items,choose,new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       choose=which;
                   }
               });
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            switch (choose){
                                case 0:
                                    setVolume(0.0f);//设置音量
                                //    Toast.makeText(MainActivity.this,items[choose],Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    setVolume(1.5f);
                                //    Toast.makeText(MainActivity.this,items[choose],Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    setVolume(3.0f);
                                //    Toast.makeText(MainActivity.this,items[choose],Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    isFirst=!isFirst;
                                    initPlayMusic(isFirst);
                                    Toast.makeText(MainActivity.this,"切换中...",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        //Toast.makeText(MainActivity.this,items[choose],Toast.LENGTH_SHORT).show();
                    }
                });
               builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
                builder.show();



           }
       });

        regret.setOnClickListener(new View.OnClickListener() { //悔棋
            @Override
            public void onClick(View v) {
                 view.regret();//悔棋
            }
        });

    }

    private void setVolume(float i) {
        manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int Max_Volume=manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的最大值
       // int Current_Volume=manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的当前值
        int setValue= (int) (Max_Volume*i);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC,setValue,AudioManager.FLAG_PLAY_SOUND);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPlayer != null) {
            for(int i=0;i<listPlayer.size();i++){
                listPlayer.get(i).stop();
                listPlayer.get(i).release();
            }
        }
    }
}
