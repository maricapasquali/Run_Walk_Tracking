package com.run_walk_tracking_gps.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public class PlayList implements Parcelable{

    private static final String DEFAULT_NAME = "PlayList";
    private int id_playlist;

    private String name;
    private ArrayList<Song> songs;
    private boolean useLikePrimary;
    private String create_date;

    private PlayList(){
        name = DEFAULT_NAME;
        songs = new ArrayList<>();
        useLikePrimary = false;
    }

    private PlayList(PlayList playList){
        this.id_playlist = playList.id_playlist;
        this.name = playList.name;
        this.useLikePrimary = playList.useLikePrimary;
        this.songs = (ArrayList<Song>)playList.songs.clone();
        this.create_date = playList.create_date;
    }

    public PlayList clone() {
        return new PlayList(this);
    }


    // PARCELABLE

    protected PlayList(Parcel in) {
        id_playlist = in.readInt();
        name = in.readString();
        songs = new ArrayList<>();
        in.readTypedList(songs, Song.CREATOR);
        useLikePrimary = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_playlist);
        dest.writeString(name);
        dest.writeTypedList(songs);
        dest.writeByte((byte) (useLikePrimary ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlayList> CREATOR = new Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel in) {
            return new PlayList(in);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };

    // END PARCELABLE


    public static PlayList create(){
        return new PlayList();
    }

    public int getId() {
        return id_playlist;
    }

    public void setId(final int id){
        this.id_playlist = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        if(name!=null && !name.isEmpty())
            this.name = name;
    }

    public void replaceAll(final List<Song> songs){
        this.songs.clear();
        this.addAll(songs);
    }

    public void addAll(final List<Song> songs){
        this.songs.addAll(songs);
    }

    public void add(final Song song){
        this.songs.add(song);
    }

    public List<Song> songs() {
        return songs;
    }

    public long duration() {
        if(songs.isEmpty()) return 0;
        return songs.stream().map(Song::getDuration).reduce(0L, Long::sum);
    }

    public boolean isUseLikePrimary() {
        return useLikePrimary;
    }

    public void setUseLikePrimary(final boolean useLikePrimary) {
        this.useLikePrimary = useLikePrimary;
    }

    public String getCreationDate() {
        return create_date;
    }

    public void setCreationDate(String date) {
        this.create_date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayList playList = (PlayList) o;
        return id_playlist == playList.id_playlist &&
                useLikePrimary == playList.useLikePrimary &&
                Objects.equals(name, playList.name) &&
                Objects.equals(songs, playList.songs) &&
                Objects.equals(create_date, playList.create_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_playlist, name, songs, useLikePrimary, create_date);
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayList [ id_playlist = "+id_playlist+", Name = " + name + ", Songs = "+ songs + ", Primary = " + useLikePrimary+" ]";
    }
}
