package com.run_walk_tracking_gps.gui.components.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewPlayListAdapter;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchSongDialog extends AlertDialog.Builder {

    private final String TAG = SearchSongDialog.class.getName();

    private AlertDialog dialog;
    private ImageView close;

    private OnCloseListener onCloseListener;
    private NewPlayListAdapter playListAdapter;
    private Map<Song, Boolean> filterSong;

    private SearchSongDialog(Context context, List<Song> songs, OnCloseListener onCloseListener) {
        super(context, R.style.DialogFullScreen);
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_search_song, null);
        setView(view);

        this.onCloseListener = onCloseListener;
        this.close = view.findViewById(R.id.close);

        final TextInputEditText search = view.findViewById(R.id.search);
        final ListView searchList = view.findViewById(R.id.result);

        this.filterSong = new LinkedHashMap<>();
        this.playListAdapter = new NewPlayListAdapter(context, filterSong, true);
        searchList.setAdapter(playListAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                filterSong.clear();
                Log.d(TAG, "Buffer : " + filterSong.keySet());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    songs.stream().filter(song -> StringUtilities.containsIgnoreCase(song.getArtist(), s.toString()) ||
                            StringUtilities.containsIgnoreCase(song.getTitle(), s.toString()))
                            .collect(Collectors.toList())
                            .forEach(song -> filterSong.put(song, false));
                    Log.e(TAG, s + " Risultati : " + (filterSong.keySet().isEmpty() ? "Nessun risultato" : filterSong.keySet()));
                }
                playListAdapter.update(filterSong);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog = create();

        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK)) {
                close();
                return true;
            }
            return false;
        });

        close.setOnClickListener(v -> close());

    }

    public static SearchSongDialog create(Context context, Set<Song> songs, OnCloseListener listener){
        return new SearchSongDialog(context, new ArrayList<>(songs), listener);
    }

    @Override
    public AlertDialog show() {
       dialog.show();
       return this.dialog;
    }

    private void close(){
        dialog.dismiss();
        onCloseListener.onClose(playListAdapter.getChosenSong());
    }

    public interface OnCloseListener{
        void onClose(List<Song> songs);
    }
}

