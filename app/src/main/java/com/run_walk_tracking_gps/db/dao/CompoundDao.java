package com.run_walk_tracking_gps.db.dao;

import com.run_walk_tracking_gps.model.Song;

import java.util.List;

public interface CompoundDao {

    boolean reOrderSong(int id_playlist, List<Song> reOrdered);

}
