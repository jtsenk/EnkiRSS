package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ViewFragmentStateAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.frags.DownloadsFragment;
import com.jtsenkbeil.enki.enkirss.feature.frags.ShowsFragment;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class ShowsActivity extends AppCompatActivity implements View.OnTouchListener {

    private TabLayout tabL;
    private ViewPager vPager;
    private ArrayList<Pair<String,Fragment>> vList = new ArrayList<>();
    private Button settingsBtn;
    private Button addBtn;
    private Intent intent;
    private TextView swipeT;
    private GestureDetector gestureDetector;
    private float sumX;
    private float sumY;
    ViewFragmentStateAdapter adapter;

    public static final int FEED_OK = 171717;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FEED_OK) {
                Bundle bundler = msg.getData();
                String s = bundler.getString("newFeed");
                Utils.toastLong("New Feed Added: " + s);
            }
            super.handleMessage(msg);
        }
    };

    public static Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        settingsBtn = findViewById(R.id.shows_activity_settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ShowsActivity.this, SettingsActivity.class);
                startActivity(intent);
                //close this activity so we don't go back to bad configs :JTS
                ShowsActivity.this.finish();
            }
        });

        addBtn = findViewById(R.id.shows_activity_add_feed_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch the Add RSS Feed activity :JTS
                intent = new Intent(ShowsActivity.this, AddFeedActivity.class);
                startActivity(intent);
                ShowsActivity.this.finish();
            }
        });

        tabL = findViewById(R.id.shows_pager_tabl);
        vPager = findViewById(R.id.shows_vpager);

        vList.add(new Pair<String, Fragment>("Shows", new ShowsFragment() ) );
        vList.add(new Pair<String, Fragment>("Downloads", new DownloadsFragment() ) );

        adapter = new ViewFragmentStateAdapter(this.getSupportFragmentManager(), vList);
        vPager.setAdapter(adapter);
        tabL.setupWithViewPager(vPager);

        //set the gesture detector to the swipe textview :JTS
        swipeT = findViewById(R.id.shows_swipe_tv);
        gestureDetector = new GestureDetector(this, new simpleGestureListener());
        swipeT.setOnTouchListener(this);
        swipeT.setFocusable(true);
        swipeT.setClickable(true);
        swipeT.setLongClickable(true);

        //debug code to figure out where the files are saved/accessed :JTS
        //File df = getFilesDir();
        //Utils.toastShort(df.getPath());
        //Utils.logD("Files Directory",df.getPath());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //reset the vList
        adapter.updateShowFrag(new Pair<String, Fragment>("Shows", new ShowsFragment() ) );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class simpleGestureListener extends GestureDetector.SimpleOnGestureListener{
        /***** onDown->onShowPress->onLongPress*****/
        boolean isToast = false;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Utils.logD("Gesture","onSingleTapUp");
            return super.onSingleTapUp(e);
        }
        @Override
        public void onLongPress(MotionEvent e) {
            Utils.logD("Gesture","onLongPress");
            super.onLongPress(e);
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Utils.logD("Gesture","onScroll");
            Utils.logD("DLListAdapter::simpleGestureListener","distanceX:"+distanceX);
            Utils.logD("DLListAdapter::simpleGestureListener","distanceY:"+distanceY);
            sumX+=distanceX;
            sumY+=distanceY;
            Utils.logD("DLListAdapter::simpleGestureListener","sumX: " + sumX);
            Utils.logD("DLListAdapter::simpleGestureListener","sumY: " + sumY);
            //we will keep isToast around, just to not fire the debug logging every single time :JTS
            if(!isToast){
                //keep both around for debugging the gesture (even though we really only care about right) :JTS
                if(sumX<0){
                    if(Math.abs(sumX)>500){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","Swiped from left to right");
                        //launch the Add RSS Feed activity :JTS
                        intent = new Intent(ShowsActivity.this, AddFeedActivity.class);
                        startActivity(intent);
                        //finish this activity to force showList reload? :JTS
                        //ShowsActivity.this.finish();
                    }
                }
                if(sumX>0){
                    if(Math.abs(sumX)>500){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","Swiped from right to left");
                    }
                }
                //we don't care about vertical motion at all here, so below is commented out :JTS
                /*
                if(sumY<0){
                    if(Math.abs(sumY)>1000){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","You scroll from top to bottom");
                    }
                }
                if(sumY>0){
                    if(Math.abs(sumY)>1000){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","You scroll from bottom to top");
                    }
                }
                */
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Utils.logD("Gesture","onFling");

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Utils.logD("Gesture","onShowPress");
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Utils.logD("Gesture","onDown");
            isToast=false;
            sumX=0;
            sumY=0;
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Utils.logD("Gesture","onDoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Utils.logD("Gesture","onDoubleTapEvent");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Utils.logD("Gesture", "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(e);
        }
    }//end simpleGestureListener inner class
}
