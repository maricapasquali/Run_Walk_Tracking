package com.run_walk_tracking_gps.db.dao;

import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PlayListDao {

    Map<Integer, PlayList> getAll();

    List<Song> getSongsFromPrimaryPlayList();

    List<Song> getAllSongs(int id_playlist);

    boolean insert(PlayList playList); // return boolean

    boolean updateName(String name, int id_playlist);

    boolean updateUsePrimary(int id_playlist);

    boolean updateSongs(List<Song> songs, int id_playlist); // return boolean

    boolean delete(int id_playlist);

}
