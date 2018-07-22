package com.jtsenkbeil.enki.enkirss.feature.adapt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.activity.AudioActivity;
import com.jtsenkbeil.enki.enkirss.feature.audio.MusicController;
import com.jtsenkbeil.enki.enkirss.feature.frags.DownloadsFragment;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.jtsenkbeil.enki.enkirss.feature.audio.MusicController.position;

public class DownloadsListAdapter extends BaseAdapter implements View.OnTouchListener {

    private final Context context;
    private GestureDetector gestureDetector;
    private final LayoutInflater inflater;
    private ArrayList<String> list;
    private ArrayList<String> shList;
    private int sumX;
    private int sumY;
    private MusicController controller;
    private Intent intent;

    public DownloadsListAdapter(Context context, ArrayList<String> list, ArrayList<String> shList, MusicController controller) {
        this.context = context;
        this.list = list;
        this.shList = shList;
        this.controller = controller;
        intent = null;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.downloads_item, parent, false);
        
        //set an OnTouchListener for each of the download list items (make a left swipe open the delete dialog) :JTS
        //gestureDetector = new GestureDetector(parent.getContext(), new simpleGestureListener(position));
        //convertView.setOnTouchListener(this);
        //convertView.setFocusable(true);
        //convertView.setClickable(true);
        //convertView.setLongClickable(true);

        TextView tv = (TextView)convertView.findViewById(R.id.downloads_item_tv);
        tv.setText(list.get(position));
        TextView stv = convertView.findViewById(R.id.downloads_item_show_tv);
        stv.setText(shList.get(position));
        Utils.logD("getE", list.get(position));
        Utils.logD("getS", shList.get(position));
        return convertView;
    }

    public void resetList(ArrayList dList, ArrayList shList) {
        this.list = dList;
        this.shList = shList;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class simpleGestureListener extends GestureDetector.SimpleOnGestureListener{
        /***** onDown->onShowPress->onLongPress*****/
        boolean isToast = false;
        int position;
        AdapterView adpv;

        public simpleGestureListener(int position) {
            super();
            this.position = position;
            adpv = null;
        }

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
                //keep this around for debugging the gesture (even though we really only care about left swiping)
                if(sumX<0){
                    if(Math.abs(sumX)>30){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","Swiped from left to right");
                    }
                }
                //net motion is left, call the delete download dialog
                if(sumX>0){
                    if(Math.abs(sumX)>30){
                        isToast = true;
                        Utils.logD("DLListAdapter::simpleGestureListener","Swiped from right to left");
                        //fire the delete dialog
                        Utils.logD("DLListAdapter::simpleGestureListener","Firing Delete Dialog");
                        //TODO: implement delete function here
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
            //implement the onItemClick here to keep the audio player working

            if (position < list.size()) {
                controller.setPlayList(DownloadsFragment.contentList);
                if (position == controller.position) {
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","position == controller.position");
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","position: " + position);
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","controller.position: " + controller.position);
                    if (controller.isPlaying) {
                        controller.pause();
                        Utils.logD("DowloadListAdapted::onSingleTapConfirmed","...is playing");
                        intent = new Intent(MainActivity.mainContext, AudioActivity.class);
                        context.startActivity(intent);
                    } else {
                        Utils.logD("DownloadListAdapted::onSingleTapConfirmed","...is NOT playing");
                        controller.play();
                        intent = new Intent(MainActivity.mainContext, AudioActivity.class);
                        context.startActivity(intent);
                    }
                } else {
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","position != controller.position");
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","position: " + position);
                    Utils.logD("DownloadListAdapted::onSingleTapConfirmed","controller.position: " + controller.position);
                    controller.position = position;
                    intent = new Intent(MainActivity.mainContext, AudioActivity.class);
                    context.startActivity(intent);
                    controller.play();
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    }//end simpleGestureListener inner class

}//end DownloadListAdapter outer class
