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
        return new CollectionsUtilities.ListUtilities<Song>().difference(super.getMusic(), super.getPlayList().songs());

        /*
         final PlayList playlist = super.getPlayList();
        final Set<Song> biggerSet = new HashSet<>(getMusic());
        Log.d(TAG, "big = " + biggerSet.toString());
        Set<Song> smallerSet;
        if(playlist!=null)
        {
            smallerSet = new HashSet<>(playlist.songs());
            Log.d(TAG, "small = " + smallerSet.toString());
            if(biggerSet.removeAll(smallerSet)){
                Log.e(TAG, "REMAIN = " + new ArrayList<>(biggerSet).toString());
                return new ArrayList<>(biggerSet);
            }
        }
        return null; //biggerSet.removeAll(smallerSet) ? new ArrayList<>(biggerSet) :  null;
        */
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
