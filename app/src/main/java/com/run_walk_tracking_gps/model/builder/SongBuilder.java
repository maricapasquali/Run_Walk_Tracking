package com.run_walk_tracking_gps.model.builder;

import com.run_walk_tracking_gps.model.Song;

public class SongBuilder {

    private Song song;

    private SongBuilder(){
        song = Song.create();
    }

    public static SongBuilder create(){
        return new SongBuilder();
    }

    public SongBuilder setTitle(String title){
        song.setTitle(title);
        return this;
    }

    public SongBuilder setArtist(String artist){
        song.setArtist(artist);
        return this;
    }

    public SongBuilder setDuration(long duration){
        song.setDuration(duration);
        return this;
    }

    public Song build() {
        return song;
    }
}
