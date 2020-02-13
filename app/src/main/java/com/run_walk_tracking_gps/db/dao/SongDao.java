package com.run_walk_tracking_gps.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.run_walk_tracking_gps.model.Song;

import java.util.List;

public interface SongDao {

    boolean insertAll(SQLiteDatabase db, List<Song> songs);

    boolean delete(int id_song, int id_playlist);

}
