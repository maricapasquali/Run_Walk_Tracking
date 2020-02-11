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
import com.run_walk_tracking_gps.model.Song;

import java.util.List;

import androidx.annotation.NonNull;

public class SongsAdapter extends ArrayAdapter<Song> {

    public SongsAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, R.layout.custom_song_playlist, objects);
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

            });
            preview.setOnClickListener(v -> {

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
}



