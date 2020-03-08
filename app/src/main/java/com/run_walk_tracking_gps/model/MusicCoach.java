package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.util.ArrayList;

public class MusicCoach {

    private static final String TAG = MusicCoach.class.getName() ;
    private static final float DOWN_VOLUME_DEFAULT = 0.2f;

    private static MusicCoach handler;

    private Context context;
    private boolean isActive;
    private int songPlaying = 0;
    private ArrayList<Song> songs = new ArrayList<>();
    private MediaPlayerHelper mediaPlayerHelper;

    private MusicCoach(Context context){
        this.context = context;
        this.resetPrimaryPlayList();
        this.isActive = Preferences.Music.isActive(context);
        this.mediaPlayerHelper = MediaPlayerHelper.getInstance(context, true);
        this.mediaPlayerHelper.setOnCompleteSong(mp -> {
            this.songPlaying = (songPlaying + 1)%songs.size();
            this.mediaPlayerHelper.reset();
            this.next();
        });
    }

    public static synchronized MusicCoach getInstance(Context context){
        if(handler == null)
            handler = new MusicCoach(context.getApplicationContext());
        return handler;
    }

    public static void release(){
        if(handler != null) {
            handler = null;
            MediaPlayerHelper.release();
        }
    }

    public void toggleStartAndStop(OnStartOrStopListener onStartOrStopListener) {
        if (this.isActive()) {
            if (this.mediaPlayerHelper.isPlaying()) {
                this.stop();
                setActive(false);
                if(onStartOrStopListener!=null) onStartOrStopListener.onStop();
            } else {
                setActive(true);
                this.start();
                if(onStartOrStopListener!=null) onStartOrStopListener.onStart();
            }
        }else{
            setActive(true);
            this.start();
            if(onStartOrStopListener!=null) onStartOrStopListener.onStart();
        }
    }

    private void next() {
        this.mediaPlayerHelper.startMedia(this.songs.get(songPlaying).getPath());
        Log.d(TAG, "Index =" + songPlaying + ", Song = " + songs.get(songPlaying));
    }

    public void start(){
        if(this.isActive()) {
            Log.d(TAG, "START MUSIC COACH");
            this.next();
        }else{
            release();
        }
    }

    public void stop(){
        if(this.isActive()) {
            this.mediaPlayerHelper.stopMedia();
            Log.d(TAG, "STOP MUSIC COACH");
        }else{
            release();
        }
    }

    private void setActive(boolean isActive){
        Preferences.Music.setActive(context,isActive);
        this.isActive = isActive;
    }

    public void downVolume() {
        if(this.isActive()){
            mediaPlayerHelper.downVolume(DOWN_VOLUME_DEFAULT);
        }else{
            release();
        }
    }

    public void restoreVolume() {
        if(this.isActive()){
            this.mediaPlayerHelper.restoreVolume();
        }else{
            release();
        }
    }

    public boolean isPlaying(){
        return this.mediaPlayerHelper.isPlaying();
    }

    public boolean isActive(){
        return this.isActive && !isEmpty();
    }

    public boolean isEmpty() {
        return this.songs.isEmpty();
    }

    private void resetPrimaryPlayList(){
        this.songs.clear();
        this.songs.addAll(DaoFactory.getInstance(context)
                                    .getPlayListDao()
                                    .getSongsFromPrimaryPlayList());
        Log.d(TAG, "Songs = " + this.songs);
    }

    public interface OnStartOrStopListener{
        void onStop();
        void onStart();
    }

}
