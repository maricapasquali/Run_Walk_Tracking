package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.SqlLitePlayListDao;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewPlayListAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.components.dialog.SearchSongDialog;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.model.builder.SongBuilder;
import com.run_walk_tracking_gps.utilities.AudioFileHelper;
import com.run_walk_tracking_gps.utilities.DateHelper;
import com.run_walk_tracking_gps.utilities.MediaPlayerHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class NewSongActivity extends CommonActivity {

    protected static final String TAG = NewSongActivity.class.getName();
    private static final int REQUEST_PERMISSION = 54;

    private FloatingActionButton searchSong;
    private NewPlayListAdapter playListAdapter;
    private ListView songsView;
    private TextInputEditText namePlaylist;
    private CheckBox usePrimary;

    private PlayList playList = PlayList.create();

    private Map<Song, Boolean> songs = new LinkedHashMap<>();
    
    protected AudioFileHelper audioFileHelper;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title());

        audioFileHelper = AudioFileHelper.getInstance(this);

        namePlaylist = findViewById(R.id.create_name_playlist);
        songsView = findViewById(R.id.songs);
        searchSong = findViewById(R.id.search_song);
        usePrimary = findViewById(R.id.use_primary_check);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }else{

            if(getIntent()!=null){
                playList = getIntent().getParcelableExtra(KeysIntent.PLAYLIST);
                namePlaylist.setText(playList.getName());
                Log.d(TAG, playList.toString());
            }

            List<Song> sFilter = filterSong();
            if(sFilter==null) audioFileHelper.getMusicMore30Sec().forEach(s -> songs.put(s, false));
            else sFilter.forEach(s -> songs.put(s, false));
        }

        playListAdapter = new NewPlayListAdapter(this, songs);
        songsView.setAdapter(playListAdapter);
        //songsView.setItemsCanFocus(true);


        findViewById(R.id.playlist_layout).setVisibility(visibility());
        usePrimary.setVisibility(visibility());
    }

    protected abstract List<Song> filterSong();

    protected abstract int title();

    protected abstract int visibility();

    protected PlayList getPlayList(){
        return playList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void result(boolean isChanged, Intent intent){
        if(isChanged) setResult(Activity.RESULT_OK, intent);
        else setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_info:

                if(playList.exist()){
                    final ArrayList<Song> addSongs =
                            SqlLitePlayListDao.create(this).updateSongs(playListAdapter.getChosenSong(), playList.getId());
                    Log.d(TAG, "List songs : " + addSongs);

                    result(!addSongs.isEmpty(), new Intent().putExtra(KeysIntent.LIST_SONG, addSongs));
                }else{
                    final DateHelper dateHelper = DateHelper.create(this);
                    final String date_creation = dateHelper.formatShortDateTimeToString(new Date(dateHelper.getCurrentDate()*1000));
                    playList.setCreationDate(date_creation);
                    playList.setUseLikePrimary(usePrimary.isChecked());
                    playList.setName(namePlaylist.getText().toString());

                    playList.replaceAll(playListAdapter.getChosenSong());
                    Log.d(TAG, "Playlist : " + playList);

                    playList = SqlLitePlayListDao.create(this).insert(playList);
                    result(playList!=null, new Intent().putExtra(KeysIntent.PLAYLIST, playList));
                }
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void listenerAction() {

        searchSong.setOnClickListener(v ->
                SearchSongDialog.create(this, songs.keySet(), songsChosenSearch -> {
                    if(!songsChosenSearch.isEmpty()) {
                        songsChosenSearch.forEach(s -> songs.put(s, true));
                        Log.d(TAG, "After search : "+ songs);
                        playListAdapter.update(songs);
                    }
                }).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerHelper.getInstance(this).stopPreview();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                if(grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        audioFileHelper.getMusicMore30Sec().forEach(s -> songs.put(s, false));
                        Log.d(TAG, songs.toString());
                        playListAdapter.update(songs);
                    }
                }
                break;
        }
    }

}
