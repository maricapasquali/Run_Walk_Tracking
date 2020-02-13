package com.run_walk_tracking_gps.db.dao;

import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PlayListDao {

    Map<Integer, PlayList> getAll();

    List<Song>  getAllSong(int id_playlist);

    PlayList insert(PlayList playList);

    boolean updateName(String name, int id_playlist);

    boolean updateUse(int id_playlist);

    ArrayList<Song> updateSongs(List<Song> songs, int id_playlist);

    boolean delete(int id_playlist);

}
