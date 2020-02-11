package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.components.dialog.InputDialog;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

public class PlayListsAdapter extends BaseAdapter {

    private String TAG = PlayListsAdapter.class.getName();

    private Context context;
    private Map<PlayList, Long> playlists;

    public PlayListsAdapter(Context context, Map<PlayList, Long> playlists){
        this.context = context;
        this.playlists =  playlists;
    }

    public void update(final PlayList playList) {
        final Optional<PlayList> play = playlists.entrySet().stream().map(Map.Entry::getKey).filter(p -> p.getId()==playList.getId()).findFirst();
        play.ifPresent(p -> playlists.remove(p));

        if(playList.isUseLikePrimary())  playlists.entrySet().forEach(e -> e.getKey().setUseLikePrimary(false));
        playlists.put(playList, playList.duration());
        this.notifyDataSetChanged();
    }

    public void remove(final int id_playList) {
        Optional<PlayList> play = playlists.entrySet().stream().map(Map.Entry::getKey).filter(p -> p.getId()==id_playList).findFirst();
        if(play.isPresent()){
            playlists.remove(play.get());
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int position) {
        return new ArrayList<>(playlists.entrySet()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_playlist, null);

        final TextView name = convertView.findViewById(R.id.name);
        final TextView duration = convertView.findViewById(R.id.duration);
        final ImageView options = convertView.findViewById(R.id.options);


        final Map.Entry<PlayList, Long> element = (Map.Entry<PlayList, Long>) getItem(position);
        duration.setText(Song.DurationUtilities.format(element.getValue()));
        name.setText(element.getKey().getName());


        //Creating the instance of PopupMenu
        final PopupMenu popup = new PopupMenu(context, options);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu_playlist, popup.getMenu());
        //registering popup with OnMenuItemClickListener

        View finalConvertView = convertView;
        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(context,element + " - Click Option : " + item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()){
                case R.id.rename_playlist:
                    InputDialog.Builder.getInstance(context)
                               .setTitle(R.string.rename)
                               .setHint(R.string.playlist)
                               .setContent(((Map.Entry<PlayList, Double>) getItem(position)).getKey().getName())
                               .setPositiveButton(R.string.ok, text -> {
                                            ((Map.Entry<PlayList, Double>) getItem(position)).getKey().setName(text);
                                            notifyDataSetChanged();
                                            Log.d(TAG, ((Map.Entry<PlayList, Double>) getItem(position)).getKey().toString() );
                                            // TODO: 09/02/2020  DATABASE
                               })
                               .create()
                               .show();
                    break;
                case R.id.delete_playlist: {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_playlist_mex)
                            .setPositiveButton(R.string.delete,
                                    (dialog, which) -> {
                                        remove(((Map.Entry<PlayList, Long>) getItem(position)).getKey().getId());

                                        // TODO: 09/02/2020  DATABASE
                                    })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();
                }
                break;
                case R.id.use_primary:
                {
                    playlists.entrySet().forEach(e -> e.getKey().setUseLikePrimary(false));
                    ((Map.Entry<PlayList, Double>) getItem(position)).getKey().setUseLikePrimary(true);
                    notifyDataSetChanged();
                    Log.d(TAG, ((Map.Entry<PlayList, Double>) getItem(position)).getKey().toString());
                    // TODO: 09/02/2020  DATABASE
                }
                break;
            }
            return true;
        });
        options.setOnClickListener(v -> popup.show());

        if(((Map.Entry<PlayList, Double>) getItem(position)).getKey().isUseLikePrimary()){ // se playlist primaria
            popup.getMenu().removeItem(R.id.use_primary);
            convertView.setBackground(context.getDrawable(R.color.colorAccent));
        }
        //else view.setBackground(context.getDrawable(R.color.colorPrimaryDark));

        return convertView;
    }

}
