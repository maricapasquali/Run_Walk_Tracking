package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.adapter.listview.SongsAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.InputDialog;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class PlayListActivity extends CommonActivity {

    private static final String TAG = PlayListActivity.class.getName();
    private static final int REQUEST_ADD_SONG = 66;
    private List<Song> songs = new ArrayList<>();

    private DragSortListView songsView;

    private TextView num_song;
    private TextView create_date;
    private TextView duration;

    private FloatingActionButton add_song;

    private SongsAdapter adapter;

    private PlayList oldPlayList;
    private PlayList newPlayList;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        num_song = findViewById(R.id.num_songs);
        create_date = findViewById(R.id.creation_date);
        duration = findViewById(R.id.duration);
        songsView = findViewById(R.id.list_songs);

        add_song = findViewById(R.id.add_song);

        if (getIntent() != null) {

            oldPlayList = getIntent().getParcelableExtra(KeysIntent.PLAYLIST);
            if(oldPlayList!=null){
                getSupportActionBar().setTitle(oldPlayList.getName());
                newPlayList = oldPlayList.clone();

                if(oldPlayList.songs().size()> 0) {
                    num_song.setText(String.valueOf(oldPlayList.songs().size()));
                    duration.setText(Song.DurationUtilities.format(oldPlayList.duration()));
                    create_date.setText(oldPlayList.getCreationDate());
                    songs.addAll(oldPlayList.songs());
                }
            }
        }

        adapter = new SongsAdapter(this, songs);
        Factory.CustomDragSortController customDragSortController = Factory.CustomDragSortController.create(songsView);
        songsView.setAdapter(adapter);
        songsView.setFloatViewManager(customDragSortController);
        songsView.setOnTouchListener(customDragSortController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu_playlist, menu);
        if(oldPlayList.isUseLikePrimary()) menu.findItem(R.id.use_primary).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.rename_playlist:
                InputDialog.Builder.getInstance(this)
                           .setTitle(R.string.rename)
                           .setHint(R.string.playlist)
                           .setContent(oldPlayList.getName())
                           .setPositiveButton(R.string.ok, text -> {
                                // TODO: 09/02/2020  DATABASE
                                newPlayList.setName(text);
                                getSupportActionBar().setTitle(newPlayList.getName());
                           })
                           .create()
                           .show();
                break;
            case R.id.delete_playlist: {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_playlist_mex)
                        .setPositiveButton(R.string.delete,
                                (dialog, which) -> {
                                    // TODO: 09/02/2020  DATABASE
                                    setResult(RESULT_OK, new Intent().putExtra(KeysIntent.DELETE_PLAYLIST, oldPlayList.getId()));
                                    finish();
                                })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
            break;
            case R.id.use_primary: {
                // TODO: 09/02/2020  DATABASE
                newPlayList.setUseLikePrimary(true);
                item.setVisible(false);
            }
            break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void listenerAction() {
        DragSortListView.DropListener onDrop = (from, to) -> {
            if (from != to) {
                Song item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);

                Log.d(TAG, songs.toString());
                // TODO: 09/02/2020  DATABASE
                newPlayList.replaceAll(songs);

                Log.d(TAG, adapter.getItem(to).toString());
            }
        };

        songsView.setDropListener(onDrop);

        add_song.setOnClickListener(V ->{
            startActivityForResult(new Intent(this, AddSongToPlayListActivity.class)
                    .putExtra(KeysIntent.PLAYLIST, oldPlayList), REQUEST_ADD_SONG);
        });
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "Change PLAYLITS = " +  !oldPlayList.equals(newPlayList));

        if(oldPlayList.equals(newPlayList)) setResult(Activity.RESULT_CANCELED);
        else setResult(Activity.RESULT_OK, new Intent().putExtra(KeysIntent.CHANGED_PLAYLIST, newPlayList));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ADD_SONG:{
                if(resultCode == Activity.RESULT_OK){
                    PlayList playList = data.getParcelableExtra(KeysIntent.PLAYLIST);
                    Log.d(TAG, "New songs " + playList.songs().toString());
                    songs.addAll(playList.songs());
                    adapter.notifyDataSetChanged();

                    updatePlaylist(playList);
                }
            }
            break;
        }
    }

    public void updatePlaylist(PlayList playList) {
        newPlayList.addAll(playList.songs());
        Log.e(TAG, "SONGS = " +  newPlayList.songs());
        Log.e(TAG, "OLD SONGS = " +  oldPlayList.songs());
        num_song.setText(String.valueOf(newPlayList.songs().size()));
        duration.setText(Song.DurationUtilities.format(newPlayList.duration()));
    }

}
