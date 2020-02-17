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
import com.run_walk_tracking_gps.db.dao.SqlLitePlayListDao;
import com.run_walk_tracking_gps.gui.components.dialog.InputDialog;
import com.run_walk_tracking_gps.model.MusicCoach;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

public class PlayListsAdapter extends BaseAdapter {

    private String TAG = PlayListsAdapter.class.getName();

    private Context context;
    private Map<Integer, PlayList> playlists;

    public PlayListsAdapter(Context context, Map<Integer, PlayList> playlists){
        this.context = context;
        this.playlists =  playlists;
    }

    public void update(final PlayList playList) {
        //Log.d(TAG, "BEFORE = " + playlists.toString());
        playlists.remove(playList.getId());
        /*Optional<Map.Entry<PlayList, Long>> entryToRemove = playlists.entrySet()
                                                                     .stream()
                                                                     .filter(e -> e.getKey().getId()==playList.getId())
                                                                     .findFirst();
        entryToRemove.ifPresent(playListLongEntry -> playlists.remove(playListLongEntry.getKey(), playListLongEntry.getValue()));


        Log.d(TAG, "ENTRY (search) = " + entryToRemove);*/
        if(playList.isUseLikePrimary())
            playlists.entrySet().forEach(entry -> entry.getValue().setUseLikePrimary(false));
        playlists.put(playList.getId(), playList);
        this.notifyDataSetChanged();

        Log.d(TAG, "AFTER = " + playlists.toString());

    }

    public void remove(final int id_playList) {
        /*Log.d(TAG, "BEFORE = " + playlists.toString());
        playlists.entrySet()
                 .stream()
                 .filter(e -> e.getKey().getId()==id_playList).findFirst()
                 .ifPresent(entry ->{
                     playlists.remove(entry.getKey());
                     Log.d(TAG, "AFTER = " + playlists.toString());
                     this.notifyDataSetChanged();
                 });*/

        if(playlists.entrySet().removeIf(entry -> entry.getValue().getId()==id_playList)){
            Log.d(TAG, "AFTER = " + playlists.toString());
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

        final Map.Entry<Integer, PlayList> element = (Map.Entry<Integer, PlayList>) getItem(position);
        duration.setText(Song.DurationUtilities.format(element.getValue().duration()));
        name.setText(element.getValue().getName());

        //Creating the instance of PopupMenu
        final PopupMenu popup = new PopupMenu(context, options);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu_playlist, popup.getMenu());
        //registering popup with OnMenuItemClickListener

        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(context,element + " - Click Option : " + item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()){
                case R.id.rename_playlist: {
                    InputDialog.Builder
                            .getInstance(context)
                            .setTitle(R.string.rename)
                            .setHint(R.string.playlist)
                            .setContent(((Map.Entry<Integer, PlayList>) getItem(position)).getValue().getName())
                            .setPositiveButton(R.string.ok, text -> {

                                PlayList p = ((Map.Entry<Integer, PlayList>) getItem(position)).getValue();
                                if (SqlLitePlayListDao.create(context).updateName(text, p.getId())) {
                                    p.setName(text);
                                    notifyDataSetChanged();
                                    Log.d(TAG, p.toString());
                                }

                            })
                            .create()
                            .show();
                }
                break;
                case R.id.delete_playlist: {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_playlist_mex)
                            .setPositiveButton(R.string.delete,
                                    (dialog, which) -> {
                                        PlayList p = ((Map.Entry<Integer, PlayList>) getItem(position)).getValue();
                                        if(SqlLitePlayListDao.create(context).delete(p.getId())){
                                            remove(p.getId());
                                            if(p.isUseLikePrimary()) MusicCoach.release();
                                        }

                                    })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();
                }
                break;
                case R.id.use_primary: {

                    PlayList p = ((Map.Entry<Integer, PlayList>) getItem(position)).getValue();
                    if(SqlLitePlayListDao.create(context).updateUse(p.getId())){
                        playlists.entrySet().forEach(e -> e.getValue().setUseLikePrimary(false));
                        p.setUseLikePrimary(true);
                        notifyDataSetChanged();
                        Log.d(TAG, p.toString());
                        MusicCoach.release();
                    }

                }
                break;
            }
            return true;
        });
        options.setOnClickListener(v -> popup.show());

        if(((Map.Entry<Integer, PlayList>) getItem(position)).getValue().isUseLikePrimary()){ // se playlist primaria
            popup.getMenu().removeItem(R.id.use_primary);
            convertView.setBackground(context.getDrawable(R.color.colorAccent));
        }


        return convertView;
    }

}
