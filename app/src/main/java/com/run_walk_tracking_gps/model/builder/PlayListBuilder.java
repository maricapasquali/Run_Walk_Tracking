package com.run_walk_tracking_gps.model.builder;

import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.List;

public class PlayListBuilder {

    private PlayList playList;

    private PlayListBuilder(){
        playList = PlayList.create();
    }

    public static PlayListBuilder create(){
        return new PlayListBuilder();
    }

    public PlayListBuilder setName(final String name){
        playList.setName(name);
        return this;
    }

    public PlayListBuilder setSongs(final List<Song> songs){
        playList.replaceAll(songs);
        return this;
    }

    public PlayListBuilder setUsePrimary(final boolean usePrimary){
        playList.setUseLikePrimary(usePrimary);
        return this;
    }

    public PlayList build() {
        return playList;
    }
}
