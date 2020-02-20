package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLitePlayListDao;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.util.ArrayList;

public class MusicCoach {

    private static final String TAG = MusicCoach.class.getName() ;
    private static final float DOWN_VOLUME_DEFAULT = 0.2f;

    private static MusicCoach handler;

    private Context context;
    private int songPlaying = 0;
    private ArrayList<Song> songs = new ArrayList<>();
    private MediaPlayerHelper mediaPlayerHelper;

    private MusicCoach(Context context){
        this.context = context;
        this.resetPrimaryPlayList();
        this.mediaPlayerHelper = MediaPlayerHelper.getInstance(context);
        this.mediaPlayerHelper.setOnCompleteSong(mp -> {
            this.songPlaying = (songPlaying+1)%songs.size();
            this.stop();
            this.next();
        });
    }

    public static MusicCoach create(Context context){
        if(handler == null)
            handler = new MusicCoach(context.getApplicationContext());
        return handler;
    }

    public static void release(){
        if(handler != null) handler = null;
    }

    public void toggleStartAndStop(OnStartOrStopListener onStartOrStopListener) {
        if (this.isActive()) {
            if (this.mediaPlayerHelper.isPlaying()) {
                this.stop();
                setActive(false);
                onStartOrStopListener.onStop();

            } else {
                setActive(true);
                this.start();
                onStartOrStopListener.onStart();
            }
        }else{
            setActive(true);
            this.start();
            onStartOrStopListener.onStart();
        }
    }

    public void start(){
        if(this.isActive()) {
            Log.d(TAG, "START");
            this.next();
        }
    }

    public void stop(){
        if(this.isActive()) {
            this.mediaPlayerHelper.stopMedia();
            Log.d(TAG, "STOP");
        }
    }

    public void setActive(boolean isActive){
        Preferences.Music.setActive(context,isActive);
    }

    public void downVolume() {
        if(this.isActive()){
            mediaPlayerHelper.downVolume(DOWN_VOLUME_DEFAULT);
        }
    }

    public void restoreVolume() {
        if(this.isActive()){
            mediaPlayerHelper.restoreVolume();
        }
    }

    public boolean isPlaying(){
        return this.mediaPlayerHelper.isPlaying();
    }

    public boolean isActive(){
        return Preferences.Music.isActive(context) && !isEmpty();
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }

    private void resetPrimaryPlayList(){
        this.songs.clear();
        this.songs.addAll(SqlLitePlayListDao.create(context).getSongsFromPrimaryPlayList());
        Log.d(TAG, "Songs = " + this.songs);
    }

    private void next() {
        this.mediaPlayerHelper.startMedia(this.songs.get(songPlaying).getPath());
        Log.d(TAG, "Index =" + songPlaying + ", Song = " + songs.get(songPlaying));
    }

    public interface OnStartOrStopListener{
        void onStop();
        void onStart();
    }

}
