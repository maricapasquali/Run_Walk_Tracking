package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.SqlLitePlayListDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteSongDao;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class SongsAdapter extends ArrayAdapter<Song> {

    private PlayList playList;
    private OnClickDeleteSongListener listener;

    public SongsAdapter(@NonNull Context context, @NonNull PlayList playList, OnClickDeleteSongListener listener) {
        super(context, R.layout.custom_song_playlist, playList.songs());
        this.playList = playList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ListHolder viewHolder;

        if (convertView == null) {
            view = ((LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE))
                                                 .inflate(R.layout.custom_song_playlist, null);

            final TextView title = view.findViewById(R.id.title);
            final TextView artist = view.findViewById(R.id.artist);
            final ImageView delete = view.findViewById(R.id.delete);
            final ImageView preview = view.findViewById(R.id.preview);
            delete.setOnClickListener(v -> {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_song_mex)
                        .setPositiveButton(R.string.delete,
                                (dialog, which) -> {
                                    Song song = getItem(position);

                                    if(SqlLiteSongDao.create(getContext()).delete(song.getId(), playList.getId())){
                                        remove(song);
                                        listener.onClickDeleteSong(song.getId(), isEmpty());
                                    }
                                })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();

            });

            preview.setOnClickListener(v -> {
                Song song = getItem(position);
                if(song.getPathPreview()!=null){
                    if(MediaPlayerHelper.getInstance(getContext()).isPlaying()){
                        MediaPlayerHelper.getInstance(getContext()).stop();
                        ((ImageView)v).setImageDrawable(getContext().getDrawable(R.drawable.ic_play));
                    }else{
                        MediaPlayerHelper.getInstance(getContext()).preview(song.getPathPreview());
                        ((ImageView)v).setImageDrawable(getContext().getDrawable(R.drawable.ic_stop));
                    }
                }
            });

            viewHolder = new ListHolder(title, artist);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ListHolder) convertView.getTag();
        }

        viewHolder.title.setText(getItem(position).getTitle());
        viewHolder.artist.setText(getItem(position).getArtist());

        return view;
    }

    private static class ListHolder {

        private TextView title;
        private TextView artist;

        private ListHolder(TextView title, TextView artist){
            this.title = title;
            this.artist = artist;
        }
    }

    public interface OnClickDeleteSongListener{
        void onClickDeleteSong(int id_song, boolean isEmpty);
    }

}



