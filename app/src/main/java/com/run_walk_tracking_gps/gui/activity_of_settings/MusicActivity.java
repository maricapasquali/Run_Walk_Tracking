package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.adapter.listview.PlayListsAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.InputDialog;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.builder.PlayListBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class MusicActivity extends CommonActivity {

    private final static int REQUEST_NEW_PLAYLIST = 55;
    private static final int REQUEST_CHANGE_PLAYLIST = 77 ;
    private static final String TAG = MusicActivity.class.getName();

    private Switch use;
    private GridView playlistView;
    private FloatingActionButton create_playlist;

    private Map<PlayList, Long> playLists;
    private PlayListsAdapter playListsAdapter;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_music);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.music));

        use = findViewById(R.id.music_on_off);
        playlistView = findViewById(R.id.playlists);
        create_playlist = findViewById(R.id.create_playlist);

        use.setChecked(Preferences.Music.isActive(this));
        playLists = new LinkedHashMap<>();
        //for (int i=1; i < 20; i++) playLists.put(PlayListBuilder.create().setName("Corsa " + i).setUsePrimary(i==1).build(), (long)i*10000);
        playListsAdapter = new PlayListsAdapter(this, playLists);
        playlistView.setAdapter(playListsAdapter);

        setUse();
    }

    @SuppressLint("RestrictedApi")
    private void setUse(){
        if(use.isChecked()){
            playlistView.setVisibility(View.VISIBLE);
            create_playlist.setVisibility(View.VISIBLE);
        }else{
            playlistView.setVisibility(View.GONE);
            create_playlist.setVisibility(View.GONE);
        }
    }

    @Override
    protected void listenerAction() {

        use.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Preferences.Music.setActive(this, isChecked);
            setUse();
        });

        playlistView.setOnItemClickListener((parent, view, position, id) ->{
            PlayList playList = ((Map.Entry<PlayList, Long>)parent.getAdapter().getItem(position)).getKey();
            Toast.makeText(MusicActivity.this, "Position = "+position +", Item = " +
                    parent.getAdapter().getItem(position), Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(this, PlayListActivity.class)
                    .putExtra(KeysIntent.PLAYLIST, playList), REQUEST_CHANGE_PLAYLIST);
        });

        create_playlist.setOnClickListener(v ->{

            InputDialog.Builder.getInstance(this)
                    .setTitle(R.string.create)
                    .setHint(R.string.playlist)
                    .setPositiveButton(R.string.create, text -> {
                        PlayList play = PlayList.create();
                        play.setName(text);
                        startActivityForResult(
                                new Intent(MusicActivity.this, NewPlayListActivity.class)
                                        .putExtra(KeysIntent.PLAYLIST, play), REQUEST_NEW_PLAYLIST);
                    })
                    .create()
                    .show();

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_NEW_PLAYLIST:
                if(resultCode== Activity.RESULT_OK){
                    PlayList playList = data.getParcelableExtra(KeysIntent.PLAYLIST);
                    Log.d(TAG, "NEW PLAYLIST : "+playList.toString());
                    playListsAdapter.update(playList);
                }
                break;
            case REQUEST_CHANGE_PLAYLIST:
                if(resultCode == Activity.RESULT_OK){
                    PlayList playList = data.getParcelableExtra(KeysIntent.CHANGED_PLAYLIST);
                    if(playList!=null){
                        Log.d(TAG, "CHANGE PLAYLIST : "+playList.toString());
                        playListsAdapter.update(playList);
                    }

                    int id_playlist = data.getIntExtra(KeysIntent.DELETE_PLAYLIST, 0);
                    if(id_playlist>0) {
                        Log.d(TAG, "DELETE PLAYLIST : "+id_playlist);
                        playListsAdapter.remove(id_playlist);
                    }
                }
                break;
        }
    }

}
