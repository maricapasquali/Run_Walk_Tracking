package com.run_walk_tracking_gps.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.List;
import java.util.Map;

public interface SongDao {

    Map<Song, List<PlayList>>  getAll();

    boolean insertAll(SQLiteDatabase db, List<Song> songs);

    boolean delete(int id_song, int id_playlist);

}
