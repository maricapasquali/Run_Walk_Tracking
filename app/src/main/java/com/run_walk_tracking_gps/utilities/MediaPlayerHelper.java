package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;

public class MediaPlayerHelper {

    private static final String TAG = MediaPlayerHelper.class.getName();

    private static final int PREVIEW_INTERVAL = 1000;
    private static final int PREVIEW_TIME = 30000;

    private static MediaPlayerHelper helper;

    private boolean isPlaying = false;
    private Context context;
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener completionListener;
    private Uri soundUriPreviewPlaying;
    private CountDownTimer countDownTimer;

    private boolean wake_up_mode;

    private MediaPlayerHelper(Context context){
        this(context, false);
    }

    private MediaPlayerHelper(Context context, boolean wake_up_mode){
        this.context = context;
        this.mediaPlayer = null;
        this.countDownTimer = null;
        this.soundUriPreviewPlaying = Uri.EMPTY ;
        this.wake_up_mode = wake_up_mode;
    }

    public static synchronized MediaPlayerHelper getInstance(Context context, boolean wake_up_mode){
        if(helper==null)
            helper = new MediaPlayerHelper(context, wake_up_mode);
        return helper;
    }

    public static synchronized MediaPlayerHelper getInstance(Context context){
        if(helper==null)
            helper = new MediaPlayerHelper(context);
        return helper;
    }

    public static void release(){
        if(helper != null) helper = null;
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
        this.countDownTimer = new CountDownTimer(PREVIEW_TIME, PREVIEW_INTERVAL){

            @Override
            public void onTick(long millisUntilFinished) {
                MediaPlayerHelper.this.startMedia(soundUri);
                Log.d(TAG, "Sec = " + String.valueOf(millisUntilFinished/PREVIEW_INTERVAL));
            }

            @Override
            public void onFinish() {
                MediaPlayerHelper.this.stopMedia();
                if(onStartAndEndPreviewListener != null)
                    onStartAndEndPreviewListener.onStop();
            }
        };
        this.countDownTimer.start();
    }

    public void stopPreview(){
        if(this.countDownTimer!=null){
            this.countDownTimer.cancel();
            stopMedia();
        }
    }

    public Uri getSoundUri() {
        return this.soundUriPreviewPlaying;
    }

    public boolean isPlaying(){
        return this.mediaPlayer != null && this.mediaPlayer.isPlaying();
    }

    public void startMedia(Uri soundUri){
        try {
            if(this.mediaPlayer == null || !isPlaying){
                if(this.mediaPlayer == null){
                    this.mediaPlayer = new MediaPlayer();
                    this.mediaPlayer.setOnCompletionListener(completionListener);
                    if(wake_up_mode)
                        this.mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
                }
                this.mediaPlayer.setDataSource(context, soundUri);
                this.mediaPlayer.prepareAsync();
                this.mediaPlayer.setOnPreparedListener(player -> {
                    player.start();
                    this.soundUriPreviewPlaying = soundUri;
                    this.isPlaying = true;
                });
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMedia() {
        if (this.mediaPlayer != null) {
            this.reset();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            this.soundUriPreviewPlaying = Uri.EMPTY;
        }
    }

    public void reset() {
        if (this.mediaPlayer != null){
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.isPlaying = false;
        }
    }

    public void downVolume(float volume){
        this.mediaPlayer.setVolume(volume, volume);
    }

    public void restoreVolume() {
        this.mediaPlayer.setVolume(1,1);
    }

    public void setOnCompleteSong(MediaPlayer.OnCompletionListener completionListener){
        this.completionListener = completionListener;
    }

    public interface OnStartAndEndPreviewListener {
        void onStart();
        void onStop();
    }

}
