package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

public class MediaPlayerHelper {

    private static final String TAG = MediaPlayerHelper.class.getName();

    private static final int PREVIEW_INTERVAL = 1000;
    private static final int PREVIEW_TIME = 30000;

    private boolean isPlaying = false;
    private static MediaPlayerHelper helper;

    private Context context;
    private MediaPlayer mediaPlayer;

    private CountDownTimer countDownTimer;

    private MediaPlayerHelper(Context context){
        this.context = context;
        mediaPlayer = null;
        countDownTimer = null;
    }

    public static synchronized MediaPlayerHelper getInstance(Context context){
        if(helper==null)
            helper = new MediaPlayerHelper(context);
        return helper;
    }

    public void preview(Uri soundUri){
        stop();

        countDownTimer = new CountDownTimer(PREVIEW_TIME, PREVIEW_INTERVAL){

            @Override
            public void onTick(long millisUntilFinished) {
                MediaPlayerHelper.this.startMedia(soundUri);
                Log.e(TAG, "Sec = " + String.valueOf(millisUntilFinished/PREVIEW_INTERVAL));
            }

            @Override
            public void onFinish() {
                MediaPlayerHelper.this.stopMedia();
            }
        };
        countDownTimer.start();
        isPlaying = true;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void stop(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
            stopMedia();
        }
    }

    private void stopMedia(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    private void startMedia(Uri soundUri){
        try {
            if(mediaPlayer==null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, soundUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
