package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

public class NewPlayListAdapter extends BaseAdapter {

    private final static String TAG = NewPlayListAdapter.class.getName();

    private Context context;
    private Map<Song, Boolean> chosenSong;

    private boolean isSearch = false;

    public NewPlayListAdapter(@NonNull Context context, Map<Song, Boolean> songs) {
        this.context = context;
        this.chosenSong = songs;
    }

    public NewPlayListAdapter(@NonNull Context context, Map<Song, Boolean> songs, final Boolean isSearch) {
        this(context, songs);
        this.isSearch = isSearch;
    }

    public void update(final Map<Song, Boolean> songs) {
        this.chosenSong.putAll(songs);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chosenSong.size();
    }

    @Override
    public Object getItem(int position) {
        return new ArrayList<>(chosenSong.entrySet()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_new_playlist_song, parent, false);

        if(isSearch) convertView.setBackgroundColor(context.getColor(R.color.colorPrimary));

        final TextView title = convertView.findViewById(R.id.title);
        final TextView artist = convertView.findViewById(R.id.artist);
        final TextView duration = convertView.findViewById(R.id.duration);
        final ImageView preview = convertView.findViewById(R.id.preview);
        final CheckBox check = convertView.findViewById(R.id.check);

        final Map.Entry<Song, Boolean> element = (Map.Entry<Song, Boolean>)getItem(position);

        title.setText(element.getKey().getTitle());
        artist.setText(element.getKey().getArtist());
        duration.setText(element.getKey().getDurationFormat());
        check.setChecked(element.getValue());

        preview.setOnClickListener(v -> {
            // TODO: 09/02/2020  PREVIEW MUSIC ( 30 SEC )
            final Song song = ((Map.Entry<Song, Boolean>) getItem(position)).getKey();
            if(song.getPathPreview()!=null) {
                if(MediaPlayerHelper.getInstance(context).isPlaying()){
                    MediaPlayerHelper.getInstance(context).stop();
                    ((ImageView)v).setImageDrawable(context.getDrawable(R.drawable.ic_play));
                }else{
                    MediaPlayerHelper.getInstance(context).preview(song.getPathPreview());
                    ((ImageView)v).setImageDrawable(context.getDrawable(R.drawable.ic_stop));
                }
            }
            Log.d(TAG, song.getPathPreview().toString());
        });

        check.setOnCheckedChangeListener((buttonView, isChecked) ->
                ((Map.Entry<Song, Boolean>) getItem(position)).setValue(isChecked));

        return convertView;
    }

    public List<Song> getChosenSong(){
        return chosenSong.entrySet()
                         .stream()
                         .filter(Map.Entry::getValue)
                         .map(Map.Entry::getKey)
                         .collect(Collectors.toList());
    }

}
