package com.jtsenkbeil.enki.enkirss.feature.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jtsenkbeil.enki.enkirss.feature.audio.MusicController;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MusicService 2014/7/31
 * Add this Service to AndroidManifest
 * @author Yan
 */
public class MusicService extends Service {

    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private String url = "null";
    private String lastURL = "null";
    public int time; // 播放进度
    private int msg;// 播放控制消息
    private Timer timer;
    private boolean stopByPhoneStatus = false;

    private AudioManager audioManager;
    private int audioFocusResult;
    private MyOnAudioFocusChangeListener audioFocusChangeListener;

    public static final int notifyLoading = 1234;
    public static final int notifyProgress = 1111;
    public static final int initComplete = 2222;
    public static final int notifyStartPlay = 3333;
    public static final int notifyPause = 4444;
    public static final int notifyResume = 5555;
    public static final int notifyCache = 6666;
    public static final int notifyComplete = 7777;
    public static final int notifySeekComplete = 8888;
    public static final int notifyPrepared = 9999;
    public static final int notifyError = 0000;

    public static final int PLAY_INIT = 1;// initial
    public static final int PLAY_MSG = 2;// start to play
    public static final int PAUSE_MSG = 3;// pause
    public static final int CONTINUE_MSG = 4;// continue
    public static final int SEEKTO_MSG = 5;// start from a purticular time

    public static final String MusicServiceStatus = "cs.gsu.edu"; // broadcast register Action

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        MobliePhoneStateListener phoneStateListener = new MobliePhoneStateListener();
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        audioFocusChangeListener = new MyOnAudioFocusChangeListener();
        audioManager = (AudioManager) getApplicationContext().getSystemService(
                Context.AUDIO_SERVICE);
        audioFocusResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(intent, flags, startId);
        else{
            msg = intent.getIntExtra("MSG", 0); // 播放信息
            url = intent.getStringExtra("url");
            time = intent.getIntExtra("SeekTime", 0);
            initListener();

            switch (msg) {
                case PLAY_INIT:
                    break;
                case PLAY_MSG:
                    if (url != null) {
                        preparePlay();
                    } else {
                        notifyError("NULL_URL");
                    }
                    break;
                case PAUSE_MSG:
                    pause();
                    break;

                case SEEKTO_MSG:
                    if (time != 0) {
                        seekTo(time);
                    } else {
                        notifyError("DEFULT_ERROR");
                    }
                    break;
                default:
                    notifyError("DEFULT_ERROR");
                    break;
            }

            return super.onStartCommand(intent, flags, startId);
        }
    }

    private void seekTo(int playTime) {
        if (timer != null) {
            timer.cancel();
        }
        if (!lastURL.equals(url)) {
            preparePlay();
        } else {
            mediaPlayer.seekTo(playTime);
        }
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        super.onDestroy();
    }

    private void pause() {
        mediaPlayer.pause();
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        timer.cancel();
        notifyPause();
    }

    protected void startPlay() {
        if(audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            audioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (time != 0) {
            mediaPlayer.seekTo(time);
            time = 0;
        } else {
            mediaPlayer.start();
            if (url != null) {
                lastURL = url;
            }
            startTimer();
            notifyStartPlay();
        }
    }

    private void preparePlay() {
        if (lastURL == null || lastURL.equals(url)) {
            startPlay();
            startTimer();
            notifyResume();
        } else if (url != null) {
            try {
                notifyLoading();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                initCacheListener();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                notifyError("System_ERROR" + e.toString());
            } catch (SecurityException e) {
                e.printStackTrace();
                notifyError("System_ERROR"+e.toString());
            } catch (IllegalStateException e) {
                e.printStackTrace();
                notifyError("System_ERROR"+e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                notifyError("System_ERROR"+e.toString());
            }
        }
    }

    private void initListener() {
        // 设置音乐播放完成时的监听器
//		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				notifyComplete();
//			}
//		});
        // 设置音乐播放错误时的监听
        mediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                notifyError("DEFULT_ERROR");
                return true;
            }
        });
        // 播放结束监听
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                notifyComplete();
            }
        });
        // seek结束监听
        mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                startPlay();
                // notifySeekComplete();
            }
        });
        // finish loading, start to play
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer arg0) {
                startPlay();
                notifyPrepared();
            }
        });
    }

    private void initCacheListener() {
        // CacheProgerss
        mediaPlayer
                .setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
                        notifyCache(arg1);
                        if (arg1 == 100) {
                            // mediaPlayer.setOnBufferingUpdateListener(null);
                        }
                    }
                });
    }

    private void notifyLoading() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyLoading);
        sendNotify(intent);
    }

    private void initComplete(int totalTime) {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", initComplete);
        intent.putExtra("initComplete", totalTime);
        sendNotify(intent);
    }

    private void notifyStartPlay() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyStartPlay);
        intent.putExtra("TotalTime", mediaPlayer.getDuration());
        sendNotify(intent);
    }

    private void notifyResume() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyResume);
        sendNotify(intent);
    }

    private void notifyPause() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyPause);
        sendNotify(intent);
    }

    protected void notifyCache(int progress) {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyCache);
        intent.putExtra("notifyCache", progress);
        sendNotify(intent);
    }

    protected void notifyComplete() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyComplete);
        sendNotify(intent);
    }

    protected void notifySeekComplete() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifySeekComplete);
        sendNotify(intent);
    }

    protected void notifyPrepared() {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyPrepared);
        sendNotify(intent);
    }

    private void notifyError(String defultError) {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyError);
        intent.putExtra("notifyError", defultError);
        sendNotify(intent);
    }

    private void notifyProgress(int progress) {
        Intent intent = new Intent();
        intent.putExtra("MusicServiceStatus", notifyProgress);
        intent.putExtra("notifyProgress", progress);
        sendNotify(intent);
    }

    private void sendNotify(Intent intent) {
        intent.setAction(MusicServiceStatus);
        sendBroadcast(intent);
    }

    // proceed every one sec.
    private class updateTask extends TimerTask {
        public void run() {
            if (mediaPlayer != null) {
                notifyProgress(mediaPlayer.getCurrentPosition());
            }
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new updateTask(), 0, 1000);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class MobliePhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时 */
                    if (stopByPhoneStatus) {
                        mediaPlayer.start();
                        notifyResume();
                        stopByPhoneStatus = false;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    if (MusicController.isPlaying) {
                        stopByPhoneStatus = true;
                        mediaPlayer.pause();
                        notifyPause();
                    }
                    break;
                default:
                    break;

            }
        }
    }

    private class MyOnAudioFocusChangeListener implements OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            audioFocusResult = focusChange;
            Log.i("AudioFocus", "focusChange=" + focusChange);
        }
    }
}
