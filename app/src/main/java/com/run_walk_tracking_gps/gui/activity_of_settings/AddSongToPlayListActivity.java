package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.view.View;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;

import java.util.List;

public class AddSongToPlayListActivity extends NewSongActivity {

    private final static String TAG = AddSongToPlayListActivity.class.getName();

    @Override
    protected List<Song> filterSong() {
        return new CollectionsUtilities.ListUtilities<Song>().difference(super.audioFileHelper.getMusicMore30Sec(), super.getPlayList().songs());
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
