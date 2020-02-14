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

    private static MediaPlayerHelper helper;

    private Context context;
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener completionListener;

    private Uri soundUriPreviewPlaying;

    private CountDownTimer countDownTimer;

    private MediaPlayerHelper(Context context){
        this.context = context;
        mediaPlayer = null;
        countDownTimer = null;
        soundUriPreviewPlaying = Uri.EMPTY ;
    }

    public static synchronized MediaPlayerHelper getInstance(Context context){
        if(helper==null)
            helper = new MediaPlayerHelper(context);
        return helper;
    }

    public void togglePreview(Uri pathPreview) {
        togglePreview(pathPreview, null);
    }

    public void togglePreview(Uri pathPreview, OnStartAndEndPreviewListener onStartAndEndPreviewListener) {
        if(isPlaying()){
            stopPreview();
            if(onStartAndEndPreviewListener != null)
                onStartAndEndPreviewListener.onStop();
        }else{
            preview(pathPreview, onStartAndEndPreviewListener);
            if(onStartAndEndPreviewListener != null)
                onStartAndEndPreviewListener.onStart();
        }
    }

    public void preview(Uri soundUri, OnStartAndEndPreviewListener onStartAndEndPreviewListener){
        //stopPreview();

        countDownTimer = new CountDownTimer(PREVIEW_TIME, PREVIEW_INTERVAL){

            @Override
            public void onTick(long millisUntilFinished) {
                MediaPlayerHelper.this.startMedia(soundUri);
                Log.e(TAG, "Sec = " + String.valueOf(millisUntilFinished/PREVIEW_INTERVAL));
            }

            @Override
            public void onFinish() {
                MediaPlayerHelper.this.stopMedia();
                if(onStartAndEndPreviewListener != null)
                    onStartAndEndPreviewListener.onStop();
            }
        };
        countDownTimer.start();
    }

    public void stopPreview(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
            stopMedia();
        }
    }

    public Uri getSoundUri() {
        return soundUriPreviewPlaying;
    }

    public boolean isPlaying(){
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void startMedia(Uri soundUri){
        try {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, soundUri);
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.prepare();
                mediaPlayer.start();

                soundUriPreviewPlaying = soundUri;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

            soundUriPreviewPlaying = Uri.EMPTY;
        }
    }

    public void downVolume(float volume){
        mediaPlayer.setVolume(volume, volume);
    }

    public void restoreVolume() {
        mediaPlayer.setVolume(1,1);
    }

    public void setOnCompleteSong(MediaPlayer.OnCompletionListener completionListener){
        this.completionListener = completionListener;
    }

    public  interface OnStartAndEndPreviewListener {
        void onStart();
        void onStop();
    }
}
