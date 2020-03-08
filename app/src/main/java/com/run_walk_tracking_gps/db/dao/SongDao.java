package com.run_walk_tracking_gps.db.dao;

import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.List;
import java.util.Map;

public interface SongDao {

    Map<Song, List<PlayList>>  getAll();

    boolean delete(int id_song, int id_playlist);

}
