package com.jtsenkbeil.enki.enkirss.feature.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * add MusicService to minifests
 * initMusicReceiver
 * UnRegisterReceiver
 * setPlayList
 * addListener(name,IPlayerStatus)
 * deleteListener(name)
 * clearListener()
 * @author Yan
 *
 */
public class MusicController {

    private static MusicController controller;

    private PlayerReceiver playerReceiver;
    private Context mContext;
    private IPlayerStatus playerStatus;
    public static boolean isPlaying =false;
    public static int position = 0;
    private Map<String, IPlayerStatus> listenerMap = new HashMap<String, IPlayerStatus>();
    public static final String PLAY_NEXT =  "MusicController.broadcast.next";
    public ArrayList<BaseAudioOb> playList = new ArrayList<BaseAudioOb>();

    public static MusicController getInstance(Context mContext){
        if (null==controller){
            controller = new MusicController(mContext);
        }
        return controller;
    }

    private MusicController(Context context) {
        mContext = context;
        initMusicReceiver();
    }

    public ArrayList<BaseAudioOb> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<BaseAudioOb> playList) {
        this.playList = playList;
        position = 0;
    }

    public void initMusicReceiver(){
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.MusicServiceStatus);
        mContext.registerReceiver(playerReceiver, filter);
        //System.out.println("MusicController广播注册");
    }
    //name 使用调用此方法的类名
    public void addListener(String name, IPlayerStatus listener){
        if(listenerMap!=null&listenerMap.containsKey(name)){
            listenerMap.remove(name);
            listenerMap.put(name, listener);
        }else{
            listenerMap.put(name, listener);
        }
    }
    public void deleteListener(String name){
        if(listenerMap!=null&listenerMap.containsKey(name)){
            listenerMap.remove(name);
        }else{
            return;
        }
    }
    public void clearListener(){
        if(listenerMap!=null){
            listenerMap.clear();
        }
    }
//	public void setListener(IPlayerStatus listener) {
//		playerStatus = listener;
//	}

    /**
     * Broadcast Receiver used to receive msg from service according to the action
     */
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listenerMap == null || listenerMap.size()==0) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(MusicService.MusicServiceStatus)) {
                switch (intent.getIntExtra("MusicServiceStatus", 0000)) {
                    case MusicService.notifyLoading:
                        onLoading();
                        break;
                    case MusicService.notifyProgress:
                        onProgress(intent.getIntExtra("notifyProgress", 0));
                        break;
                    case MusicService.initComplete:
                        onInitComplete();
                        //playerStatus.onInitComplete();
                        break;
                    case MusicService.notifyStartPlay:
                        onStart(intent.getIntExtra("TotalTime", 0));
                        //playerStatus.onStart(intent.getIntExtra("TotalTime", 0));
                        break;
                    case MusicService.notifyPause:
                        onPause();
                        //playerStatus.onPause();
                        break;
                    case MusicService.notifyResume:
                        isPlaying = true;
                        onResume();
                        //playerStatus.onResume();
                        break;
                    case MusicService.notifyCache:
                        onUpdateCache(intent.getIntExtra("notifyCache", 0));
                        //playerStatus.onUpdateCache(intent.getIntExtra("notifyCache", 0));
                        break;
                    case MusicService.notifyComplete:
                        onComplete();
                        //playerStatus.onComplete();
                        break;
                    case MusicService.notifySeekComplete:
                        onSeekComplete();
                        //playerStatus.onSeekComplete();
                        break;
                    case MusicService.notifyPrepared:
                        onPrepared();
                        //playerStatus.onPrepared();
                        break;
                    case MusicService.notifyError:
                        isPlaying = false;
                        onError("default");
                        //playerStatus.onError("default");
                        break;
                    default:
                        onError("default");
                        //playerStatus.onError("default");
                }
            }
        }

        private void onLoading() {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onLoading();
            }
        }

        private void onError(String string) {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onError(string);
            }
        }

        private void onPrepared() {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onPrepared();
            }
        }

        private void onSeekComplete() {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onSeekComplete();
            }
        }

        private void onComplete() {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onComplete();
            }
        }

        private void onUpdateCache(int intExtra) {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onUpdateCache(intExtra);
            }
        }

        private void onResume() {
            isPlaying = true;
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onResume();
            }
        }
        private void onPause() {
            isPlaying = false;
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onPause();
            }
        }
        private void onStart(int intExtra) {
            isPlaying = true;
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onStart(intExtra);
            }
        }
        private void onInitComplete() {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onInitComplete();
            }
        }
        private void onProgress(int i) {
            for(Entry<String, IPlayerStatus> entry : listenerMap.entrySet()){
                entry.getValue().onProgress(i);
            }
        }
    }

    public void play() {
        if (getPlayList() == null) {
            playerStatus.onError("null_list");
        } else {
            String url = getPlayList()
                    .get(position).getURL();

            Intent playIntent = new Intent(mContext, MusicService.class);
            playIntent.putExtra("MSG", MusicService.PLAY_MSG);
            playIntent.putExtra("url", url);
            mContext.startService(playIntent);
        }
    }

    public void play(int seekTime) {
        if (getPlayList() == null) {
            playerStatus.onError("null_list");
        } else {
            String url = getPlayList()
                    .get(position).getURL();

            Intent playIntent = new Intent(mContext, MusicService.class);
            playIntent.putExtra("MSG", MusicService.SEEKTO_MSG);
            playIntent.putExtra("url", url);
            playIntent.putExtra("SeekTime", seekTime);
            mContext.startService(playIntent);
        }
    }

    public void pause() {
        Intent pauseIntent = new Intent(mContext, MusicService.class);
        pauseIntent.putExtra("MSG", MusicService.PAUSE_MSG);
        mContext.startService(pauseIntent);
    }

    public boolean containListener(String name) {
        if(listenerMap.containsKey(name)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * cancel all receiver
     */
    public void unRegisterReceiver() {
        if (playerReceiver != null) {
            mContext.unregisterReceiver(playerReceiver);
            //System.out.println("MusicController广播注销");
            playerReceiver = null;
        }
    }

    public void playPrevious() {
        position = position - 1;
        if (position >= 0) {
            play();
        } else {
            position+=1;
            Toast.makeText(mContext,"This is the first one.", Toast.LENGTH_LONG).show();
        }
    }

    public void playNext() {
        position = position + 1;
        if (position < getPlayList().size()) {
            play();
        } else {
            position-=1;
            Toast.makeText(mContext,"This is the last one",Toast.LENGTH_LONG).show();
        }
    }

    public void destroy() {
        Intent stopIntent = new Intent(mContext, MusicService.class);
        mContext.stopService(stopIntent);
    }

    public String getTitle() {
        AudioOb playingA = (AudioOb)getPlayList().get(position);
        return playingA.getName();
    }

    public String getShow() {
        AudioOb playingA = (AudioOb)getPlayList().get(position);
        return playingA.getShow();
    }

    public interface IPlayerStatus {
        public void onLoading();
        public void onProgress(int i);
        public void onError(String error);
        public void onPrepared();
        public void onSeekComplete();
        public void onComplete();
        public void onUpdateCache(int i);
        public void onPause();
        public void onResume();
        public void onStart(int i);
        public void onInitComplete();

    }

}
