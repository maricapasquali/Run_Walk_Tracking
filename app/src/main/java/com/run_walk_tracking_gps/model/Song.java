package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.icu.util.MeasureUnit;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.run_walk_tracking_gps.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class Song implements Parcelable {

    private int id_song;

    private String title;
    private String artist;
    private long duration; // millisecondi

    private Song(){}

    // PARCELABLE

    protected Song(Parcel in) {
        id_song = in.readInt();
        title = in.readString();
        artist = in.readString();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_song);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    // END PARCELABLE


    public static Song create(){
        return new Song();
    }

    public int getId() {
        return id_song;
    }

    public void setId(int id) {
        this.id_song = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationFormat() {
        return Song.DurationUtilities.format(duration);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public static class DurationUtilities {

        private static final String TAG = DurationUtilities.class.getName();

        public static long parse(String duration){
            String[] s = duration.split(" ");
            Log.d(TAG, Arrays.toString(s));
            long h_mill = TimeUnit.HOURS.toMillis(Long.valueOf(s[0]));
            long min_mill = TimeUnit.MINUTES.toMillis(Long.valueOf(s[2]));
            long sec_mill = TimeUnit.SECONDS.toMillis(Long.valueOf(s[4]));
            return h_mill+min_mill+sec_mill;
        }

        public static String format(long millisec){
            StringBuilder s = new StringBuilder();

            List<Long> time = Measure.Utilities.time(millisec/1000);

            if(time.get(0)>0)
                s.append(time.get(0)).append(" ").append(Measure.Unit.HOURS.getString()).append(" ");

            if(time.get(1)>0)
                s.append(time.get(1)).append(" ").append(Measure.Unit.MINUTES.getString()).append(" ");

            if(time.get(2)>0)
                s.append(time.get(2)).append(" ").append(Measure.Unit.SECOND.getString());

            return s.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id_song == song.id_song &&
                duration == song.duration &&
                Objects.equals(title, song.title) &&
                Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_song, title, artist, duration);
    }

    @NonNull
    @Override
    public String toString() {
        return "Song : [ id_song = "+id_song+", Title = " +title +", Artist = " +artist + ", Duration = "+getDurationFormat()+" ]";
    }
}
