package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.view.View;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Song;

import java.util.List;


public class NewPlayListActivity extends NewSongActivity {

    @Override
    protected List<Song> filterSong() {
        return null;
    }

    @Override
    protected int title() {
        return R.string.create;
    }

    @Override
    protected int visibility() {
        return View.VISIBLE;
    }

}
