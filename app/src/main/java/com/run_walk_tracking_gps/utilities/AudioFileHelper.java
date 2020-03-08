package com.run_walk_tracking_gps.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.model.builder.SongBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AudioFileHelper {

    private static final int FILTER_DURATION = 30000;

    private static AudioFileHelper helper;

    private final Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private ContentResolver contentResolver;

    private AudioFileHelper(Context context){
        contentResolver = context.getContentResolver();
    }

    public static synchronized AudioFileHelper getInstance(Context context){
        if(helper == null)
            helper = new AudioFileHelper(context.getApplicationContext());
        return helper;
    }

    public static void release(){
        if(helper != null)
            helper = null;
    }

    public boolean isAudioExist(Uri path){
        final Cursor songCursor = contentResolver.query(songUri, null, MediaStore.Audio.Media.DATA + "=?", new String[]{path.toString()}, null);
        return (songCursor!=null && songCursor.moveToFirst());
    }


    public List<Song> getMusicMore30Sec(){
        return getMusic().stream().filter(s -> s.getDuration() >=FILTER_DURATION ).collect(Collectors.toList());
    }

    private List<Song> getMusic(){
        final Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        final List<Song> s = new ArrayList<>();

        if(songCursor!=null && songCursor.moveToFirst()){
            do{
                s.add(SongBuilder.create()
                        .setTitle(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))
                        .setArtist(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                        .setDuration(songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                        .setPath(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)))
                        .build());

            }while(songCursor.moveToNext());
            songCursor.close();
        }
        return s;
    }

}
