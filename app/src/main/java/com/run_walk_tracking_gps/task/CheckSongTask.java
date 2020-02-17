package com.run_walk_tracking_gps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.run_walk_tracking_gps.db.dao.SongDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteSongDao;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.AudioFileHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CheckSongTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = CheckSongTask.class.getName();
    private AudioFileHelper audioFileHelper;
    private SongDao songDao;

    private CheckSongTask(Context context){
        audioFileHelper = AudioFileHelper.getInstance(context);
        songDao = SqlLiteSongDao.create(context);
    }

    public static CheckSongTask create(Context context) {
        return new CheckSongTask(context.getApplicationContext());
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG, "Start control songs");
        final AtomicBoolean success = new AtomicBoolean(false);
        final Map<Song, List<PlayList>> songPlayListMap = songDao.getAll();
        songPlayListMap.forEach((k, v) ->{
            if(!audioFileHelper.isAudioExist(k.getPath())){
                v.forEach(p -> success.set(songDao.delete(k.getId(), p.getId())));
            }
        });
        return success.get();
    }

    @Override
    protected void onPostExecute(Boolean aBool) {
        super.onPostExecute(aBool);

        AudioFileHelper.release();
        Log.d(TAG, "Finish control song : " +aBool);
    }
}
