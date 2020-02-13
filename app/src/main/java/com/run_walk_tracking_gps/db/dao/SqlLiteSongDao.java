package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.CompoundDescriptor;
import com.run_walk_tracking_gps.db.tables.PlayListDescriptor;
import com.run_walk_tracking_gps.db.tables.SongDescriptor;
import com.run_walk_tracking_gps.model.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SqlLiteSongDao implements SongDao {

    private String TAG = SqlLiteSongDao.class.getName();

    private static SongDao songDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLiteSongDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized SongDao create(Context context) {
        if(songDao==null){
            songDao = new SqlLiteSongDao(context.getApplicationContext());
        }
        return songDao;
    }

    @Override
    public boolean insertAll(SQLiteDatabase db, List<Song> songs) {
        songs.forEach(song -> {
            int id_song = getIdIfExist(db, song);
            if(id_song<0) {
                final ContentValues songContentValues = new ContentValues();
                id_song = DataBaseUtilities.getNextId(db, SongDescriptor.TABLE_SONG, SongDescriptor.ID_SONG);
                songContentValues.put(SongDescriptor.ID_SONG, id_song);
                songContentValues.put(SongDescriptor.TITLE, song.getTitle());
                songContentValues.put(SongDescriptor.ARTIST, song.getArtist());
                songContentValues.put(SongDescriptor.DURATION, song.getDuration());
                songContentValues.put(SongDescriptor.PATH, song.getPathPreview().toString());

                if (db.insert(SongDescriptor.TABLE_SONG, null, songContentValues) == -1)
                    throw new SQLiteException("Insert SONG FAIL");
            }
            song.setId(id_song);

        });
        return true;
    }

    @Override
    public boolean delete(int id_song, int id_playlist) {
        // ELIMINARE NELLA PLAYLIST IN CUI è SELEZIONATA ,
        // SE IN COUMPOUND NON CI SONO PIU ASSOCIAZIONI
        // ALLORA CANCELLARE ANCHE NELLA TABELLA SONG
        AtomicLong num_song = new AtomicLong(-1);
        SQLiteDatabase db = daoFactory.getWritableDatabase();

        DataBaseUtilities.transaction(db, ()-> {
            Map<String, String> where = new HashMap<>();
            where.put(SongDescriptor.ID_SONG, String.valueOf(id_song));
            try(Cursor c = db.query(CompoundDescriptor.TABLE_COMPOUND, new String[]{SongDescriptor.ID_SONG},
                    DataBaseUtilities.buildWhereClause(where.keySet()), where.values().toArray(new String[0]),
                    SongDescriptor.ID_SONG, "COUNT(" + SongDescriptor.ID_SONG +") == 1",null, null)){

                if(c.moveToFirst()){
                    // DELETE FROM SONG WHERE ID_SONG = id_song;
                    Log.d(TAG, "Delete in TABLE SONG");
                    db.delete(SongDescriptor.TABLE_SONG, DataBaseUtilities.buildWhereClause(where.keySet()), where.values().toArray(new String[0]));
                }else{

                    where.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));
                    Log.d(TAG, "Delete in TABLE COMPOUND");
                    db.delete(CompoundDescriptor.TABLE_COMPOUND, DataBaseUtilities.buildWhereClause(where.keySet()), where.values().toArray(new String[0]));
                }
            }

            /* verificare se playlist ci sono 0 canzoni eliminare playlist*/
            where.clear();
            where.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));
            Log.d(TAG, "where condition " +where);
            num_song.set(DatabaseUtils.queryNumEntries(db, CompoundDescriptor.TABLE_COMPOUND,
                    DataBaseUtilities.buildWhereClause(where.keySet()),
                    new String[]{String.valueOf(id_playlist)}));
            return true;
        });

        Log.d(TAG, "NUM SONG = " + num_song);
        if(num_song.get() ==0)
            SqlLitePlayListDao.create(context).delete(id_playlist);

        return true;
    }

    private int getIdIfExist(SQLiteDatabase db, Song song){
        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(SongDescriptor.TITLE, song.getTitle());
        whereCondition.put(SongDescriptor.ARTIST, song.getArtist());
        whereCondition.put(SongDescriptor.DURATION, String.valueOf(song.getDuration()));
        whereCondition.put(SongDescriptor.PATH, song.getPathPreview().toString());

        try (Cursor c = db.query(SongDescriptor.TABLE_SONG, new String[]{SongDescriptor.ID_SONG},
                DataBaseUtilities.buildWhereClause(whereCondition.keySet()),
                whereCondition.values().toArray(new String[0]),null , null , null)) {
            return (!c.moveToFirst()) ? -1 : c.getInt(c.getColumnIndex(SongDescriptor.ID_SONG));
        }
    }
}
