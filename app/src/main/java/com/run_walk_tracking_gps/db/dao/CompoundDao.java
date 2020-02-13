package com.run_walk_tracking_gps.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.run_walk_tracking_gps.model.Song;

import java.util.List;

public interface CompoundDao {

    boolean insertAll(SQLiteDatabase db, int id_playlist, List<Song> songs);

    boolean reOrderSong(int id_playlist, List<Song> reOrdered);

    void delete(SQLiteDatabase db, int id_playlist);
}
