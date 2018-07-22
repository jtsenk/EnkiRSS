package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.audio.AudioOb;
import com.jtsenkbeil.enki.enkirss.feature.audio.BaseAudioOb;
import com.jtsenkbeil.enki.enkirss.feature.audio.MusicController;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.frags.DownloadsFragment;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

//adapted largely from PlayView class provided by instructor :JTS
public class AudioActivity extends AppCompatActivity implements MusicController.IPlayerStatus {

    //private final View view;
    private MusicController controller;
    private TextView name;
    private ImageView back15;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView totalTime;
    private ImageView playBt;
    private ImageView foward15;
    private TextView seekTime;
    private ImageView next;
    private ImageView last;
    private TextView show;

    private ArrayList<BaseAudioOb> contentList = new ArrayList<BaseAudioOb>();
    private Bundle bundle;
    private Ki ki;
    private Cursor curs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get static contentList from downloadfragment -- there must be a batter way to do this... :JTS
        contentList = DownloadsFragment.contentList;
        setContentView(R.layout.activity_audio);

        //don't need this, because I've got a static ref to MainActivity :JTS
        //this.mContext = (MainActivity)MainActivity.mainContext;

        //get bundle for contentlist
        //bundle = getIntent().getBundleExtra("bundle");

        //call initInfo to set contentList
        initInfo();

        //set views, etc.
        seekTime = findViewById(R.id.play_play_seektime);
        name = findViewById(R.id.main_player_title);
        back15 = findViewById(R.id.main_play__last15);
        foward15 = findViewById(R.id.main_play_next15);
        playBt = findViewById(R.id.main_play_play);
        progressBar = findViewById(R.id.pb_play_loading);
        seekBar = findViewById(R.id.main_play_seekbar);
        totalTime = findViewById(R.id.main_play_totaltime);
        next = findViewById(R.id.main_play__next);
        last = findViewById(R.id.main_play_last);
        show = findViewById(R.id.main_player_show_title);

        //set OnClickListeners
        back15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.play(seekBar.getProgress() - 15000);
                Utils.logD("getProgress",String.valueOf(seekBar.getProgress()) );
            }
        });

        foward15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.play(seekBar.getProgress() + 15000);
                Utils.logD("getProgress",String.valueOf(seekBar.getProgress()) );
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.playPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.playNext();
            }
        });

        playBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!controller.isPlaying){
                    Utils.logD("Music","Play");
                    Utils.toastShort("Play");
                    controller.play();
                }else{
                    controller.pause();
                }
            }
        });

        controller = MusicController.getInstance(MainActivity.mainContext);
        //controller.setPlayList(this.getContent());
        controller.addListener("PlayView",this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int tempProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekTime.setText(Utils.secToTime(progress));
                if(fromUser){
                    tempProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                controller.play(tempProgress);
            }
        });
    }

    public void update(){

    }

    @Override
    public void onLoading() {
        prepareStatus();
    }

    private void prepareStatus() {
        playBt.setBackgroundResource(R.drawable.playscreen_play_pause);
        playBt.setVisibility(INVISIBLE);
        progressBar.setVisibility(VISIBLE);
    }

    private void pauseStatus(){
        playBt.setBackgroundResource(R.drawable.playscreen_play_pause);
        playBt.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
    }

    private void startStatus(){
        playBt.setBackgroundResource(R.drawable.playscreen_play_play);
        playBt.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
    }

    @Override
    public void onProgress(int i) {
        seekBar.setProgress(i);
    }

    @Override
    public void onError(String error) {
        Utils.toastShort(error);
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onUpdateCache(int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        startStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(int i) {
        Utils.toastShort("started");
        seekBar.setMax(i);
        totalTime.setText(Utils.secToTime(i));
        name.setText(controller.getTitle());
        show.setText(controller.getShow());
        pauseStatus();
    }

    @Override
    public void onInitComplete() {

    }

    private void initInfo() {

        //new strategy: read everything from the DL table in the DB
        ki = new Ki();
        curs = ki.getTable("tbl_dl");
        while (curs.moveToNext() ) {

            File file = new File( curs.getString(curs.getColumnIndex("path")) );
            AudioOb a = new AudioOb();
            a.setURL(Uri.fromFile(file).toString());
            a.setName(curs.getString(curs.getColumnIndex("title")));
            a.setShow(curs.getString(curs.getColumnIndex("show")));
            Utils.logD("AudioActivity::initInfo","url= " + Uri.fromFile(file).toString());
            Utils.logD("AudioActivity::initInfo","does file exist? " + file.exists());
            contentList.add(a);
        }
        ki.closeDown();

        /*
        AudioOb a1 = new AudioOb();
        File file = new File(bundle.getString("filepath"));
        a1.setURL(Uri.fromFile(file).toString());
        Utils.logD("AudioActivity::initInfo","url= " + Uri.fromFile(file).toString());
        Utils.logD("AudioActivity::initInfo","does file exist? " + file.exists());
        a1.setName(bundle.getString("title"));
        a1.setShow(bundle.getString("show"));
        contentList.add(a1);
        */

        /*
        AudioOb m1 = new AudioOb();
        m1.setURL("http://other.web.rh01.sycdn.kuwo.cn/resource/n3/77/1/1061700123.mp3");
        m1.setName("One Moment");
        //m1.setURL("http://www.jtsenkbeil.com/audio/SunIsShining.mp3");
        //m1.setName("Sun Is Shining");

        AudioOb m2 = new AudioOb();
        m2.setURL("http://other.web.re01.sycdn.kuwo.cn/resource/n2/41/79/3442130618.mp3");
        m2.setName("I will remember you");
        //m2.setURL("http://www.jtsenkbeil.com/audio/LivelyUpYourself.mp3");
        //m2.setName("Lively Up Yourself");


        AudioOb m3 = new AudioOb();
        m3.setURL("http://other.web.ra01.sycdn.kuwo.cn/resource/n2/128/72/74/2639398911.mp3");
        m3.setName("Young for you");
        //m3.setURL("http://www.jtsenkbeil.com/audio/AfricanHerbsman.mp3");
        //m3.setName("African Herbsman");

        contentList.add(m1);
        contentList.add(m2);
        contentList.add(m3);
        */
    }

    public ArrayList<BaseAudioOb> getContent(){
        return contentList;
    }

    @Override
    protected void onDestroy() {
        MusicController controller = MusicController.getInstance(this);
        controller.destroy();
        super.onDestroy();
    }
}

