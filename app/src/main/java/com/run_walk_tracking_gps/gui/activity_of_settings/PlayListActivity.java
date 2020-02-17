package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.SqlLiteCompoundDao;
import com.run_walk_tracking_gps.db.dao.SqlLitePlayListDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteSongDao;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.adapter.listview.SongsAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.InputDialog;
import com.run_walk_tracking_gps.model.MusicCoach;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class PlayListActivity extends CommonActivity {

    private static final String TAG = PlayListActivity.class.getName();
    private static final int REQUEST_ADD_SONG = 66;

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

                Log.d(TAG, "OLD = " + oldPlayList);
                if(oldPlayList.songs().isEmpty())
                    oldPlayList.addAll(SqlLitePlayListDao.create(this).getAllSong(oldPlayList.getId()));

                newPlayList = oldPlayList.clone();

                create_date.setText(oldPlayList.getCreationDate());
                num_song.setText(String.valueOf(oldPlayList.songs().size()));
                duration.setText(Song.DurationUtilities.format(oldPlayList.duration()));
            }
        }

        adapter = new SongsAdapter(this, newPlayList, (id_song, isEmpty) -> {
            if(isEmpty){
                setResult(RESULT_OK, new Intent().putExtra(KeysIntent.DELETE_PLAYLIST, newPlayList.getId()));
                finish();
            }
            duration.setText(Song.DurationUtilities.format(newPlayList.duration()));
            num_song.setText(String.valueOf(newPlayList.songs().size()));
        });

        Factory.CustomDragSortController customDragSortController = Factory.CustomDragSortController.create(songsView);
        songsView.setAdapter(adapter);
        songsView.setFloatViewManager(customDragSortController);
        songsView.setOnTouchListener(customDragSortController);

        songsView.setOnItemClickListener((parent, view, position, id) -> {

            view.findViewById(R.id.preview).setOnClickListener(v -> {
                ImageView finalPView = (ImageView)v;
                final Song song = (Song)parent.getAdapter().getItem(position);
                for (int i = 0; i < adapter.getCount(); i++) {
                    ((ImageView)parent.getChildAt(i).findViewById(R.id.preview))
                            .setImageDrawable(getDrawable(position == i ? R.drawable.ic_stop : R.drawable.ic_play ));
                }
                final MediaPlayerHelper mediaPlayerHelper = MediaPlayerHelper.getInstance(PlayListActivity.this);
                final MediaPlayerHelper.OnStartAndEndPreviewListener listener = new MediaPlayerHelper.OnStartAndEndPreviewListener() {
                    @Override
                    public void onStart() {
                        finalPView.setImageDrawable(PlayListActivity.this.getDrawable(R.drawable.ic_stop));
                    }
                    @Override
                    public void onStop() {
                        finalPView.setImageDrawable(PlayListActivity.this.getDrawable(R.drawable.ic_play));
                    }
                };
                if(mediaPlayerHelper.getSoundUri().equals(song.getPath())) {
                    mediaPlayerHelper.togglePreview(song.getPath(), listener);
                }else{
                    mediaPlayerHelper.stopPreview();
                    mediaPlayerHelper.preview(song.getPath(), listener);
                }
            });


        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("PROVA_", SqlLiteSongDao.create(this).getAll().toString());
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
                                if(SqlLitePlayListDao.create(this).updateName(text, newPlayList.getId())){
                                    newPlayList.setName(text);
                                    getSupportActionBar().setTitle(newPlayList.getName());
                                }
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
                                    if(SqlLitePlayListDao.create(this).delete(oldPlayList.getId())){
                                        if(oldPlayList.isUseLikePrimary()) MusicCoach.release();
                                        setResult(RESULT_OK, new Intent().putExtra(KeysIntent.DELETE_PLAYLIST, oldPlayList.getId()));
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
            break;
            case R.id.use_primary: {
                if(SqlLitePlayListDao.create(this).updateUse(newPlayList.getId())){
                    newPlayList.setUseLikePrimary(true);
                    item.setVisible(false);
                    MusicCoach.release();
                }
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
                SqlLiteCompoundDao.create(this).reOrderSong(newPlayList.getId(),  newPlayList.songs());
                MusicCoach.release();

                Log.d(TAG, newPlayList.toString());
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
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerHelper.getInstance(this).stopPreview();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "Change PLAYLITS = " +  !oldPlayList.equals(newPlayList));

        if(oldPlayList.equals(newPlayList))
            setResult(Activity.RESULT_CANCELED);
        else
            setResult(Activity.RESULT_OK, new Intent().putExtra(KeysIntent.CHANGED_PLAYLIST, newPlayList));

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ADD_SONG:{
                if(resultCode == Activity.RESULT_OK){
                    ArrayList<Song> list = data.getParcelableArrayListExtra(KeysIntent.LIST_SONG);
                    Log.d(TAG, "New songs " + list);
                    newPlayList.addAll(list);
                    adapter.notifyDataSetChanged();
                    num_song.setText(String.valueOf(newPlayList.songs().size()));
                    duration.setText(Song.DurationUtilities.format(newPlayList.duration()));
                }
            }
            break;
        }
    }

}
