package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.util.Log;
import android.view.View;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;

import java.util.List;

public class AddSongToPlayListActivity extends NewSongActivity {

    private final static String TAG = AddSongToPlayListActivity.class.getName();

    @Override
    protected List<Song> filterSong() {
        List<Song> diff = new CollectionsUtilities.ListUtilities<Song>().difference(super.getMusic(), super.getPlayList().songs());
        Log.d(TAG, "DIFF = " +diff);
        return diff;
    }

    @Override
    protected int title() {
        return R.string.add;
    }

    @Override
    protected int visibility() {
        return View.GONE;
    }

}
