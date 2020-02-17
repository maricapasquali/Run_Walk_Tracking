package com.run_walk_tracking_gps.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.run_walk_tracking_gps.db.dao.SongDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteSongDao;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.AudioFileHelper;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class CheckSongService extends Service {

    private static final String TAG = CheckSongService.class.getName();
    private AudioFileHelper audioFileHelper;
    private SongDao songDao;
    private boolean success = false;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = getApplicationContext();
        audioFileHelper = AudioFileHelper.getInstance(context);
        songDao = SqlLiteSongDao.create(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(()->{
            Looper.prepare();
            Log.d(TAG, "Start control songs");
            final Map<Song, List<PlayList>>  songPlayListMap = songDao.getAll();
            songPlayListMap.forEach((k, v) ->{
                if(!audioFileHelper.isAudioExist(k.getPath())){
                    v.forEach(p -> success = songDao.delete(k.getId(), p.getId()));
                }
            });

            Looper.loop();
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioFileHelper.release();

        Log.d(TAG, "Finish control song : " +success);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
