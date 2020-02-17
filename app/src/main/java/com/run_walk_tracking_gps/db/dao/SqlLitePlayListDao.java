package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.CompoundDescriptor;
import com.run_walk_tracking_gps.db.tables.PlayListDescriptor;
import com.run_walk_tracking_gps.db.tables.SongDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.model.PlayList;
import com.run_walk_tracking_gps.model.Song;
import com.run_walk_tracking_gps.model.builder.SongBuilder;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SqlLitePlayListDao implements PlayListDao {

    private static final String TAG = SqlLitePlayListDao.class.getName();

    private static PlayListDao playListDao;
    private Context context;
    private DaoFactory daoFactory;

    private SqlLitePlayListDao(Context context){
        this.context = context;
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized PlayListDao create(Context context) {
        if(playListDao==null){
            playListDao = new SqlLitePlayListDao(context.getApplicationContext());
        }
        return playListDao;
    }

    @Override
    public Map<Integer, PlayList> getAll() {

        SQLiteDatabase db =  daoFactory.getReadableDatabase();

        try(Cursor cursor = db.rawQuery("SELECT P.*, SUM(" +SongDescriptor.DURATION +") AS duration_playlist " +
                                        "FROM " + SongDescriptor.TABLE_SONG +" S JOIN " + CompoundDescriptor.TABLE_COMPOUND +" C ON (S." + SongDescriptor.ID_SONG +"=C." +SongDescriptor.ID_SONG+") " +
                                        "JOIN " + PlayListDescriptor.TABLE_PLAYLIST +" P ON ( C." +PlayListDescriptor.ID_PLAYLIST +" = P." +PlayListDescriptor.ID_PLAYLIST +") " +
                                        "GROUP by P."+PlayListDescriptor.ID_PLAYLIST,
                null)){

            Map<Integer, PlayList> playLists = new LinkedHashMap<>();
            if(cursor.moveToFirst()) {
                do {
                    PlayList playList = PlayList.create();
                    playList.setId(cursor.getInt(cursor.getColumnIndex(PlayListDescriptor.ID_PLAYLIST)));
                    playList.setName(cursor.getString(cursor.getColumnIndex(PlayListDescriptor.NAME)));
                    playList.setUseLikePrimary(cursor.getInt(cursor.getColumnIndex(PlayListDescriptor.USE_PRIMARY)) == 1);
                    playList.setCreationDate(cursor.getString(cursor.getColumnIndex(PlayListDescriptor.DATE_CREATION)));

                    playList.setDuration(cursor.getLong(cursor.getColumnIndex("duration_playlist")));

                    playLists.put(playList.getId(), playList);

                } while (cursor.moveToNext());
            }
            return playLists;
        }
    }

    @Override
    public List<Song> getPrimaryPlayList() {
        SQLiteDatabase db = daoFactory.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT c." +CompoundDescriptor.ORDER+",s." +SongDescriptor.ID_SONG + ", s." + SongDescriptor.PATH +
                                             " FROM " + CompoundDescriptor.TABLE_COMPOUND +" c JOIN " + PlayListDescriptor.TABLE_PLAYLIST
                                                +" p ON(c." + PlayListDescriptor.ID_PLAYLIST+" = p." + PlayListDescriptor.ID_PLAYLIST+") " +
                                             "JOIN " + SongDescriptor.TABLE_SONG +" s ON (s."+SongDescriptor.ID_SONG+" = c."+SongDescriptor.ID_SONG+") " +
                                             "WHERE p." + UserDescriptor.ID_USER +"=? AND  p." +PlayListDescriptor.USE_PRIMARY +" = 1 " +
                                             "ORDER BY c."+CompoundDescriptor.ORDER,
                new String[]{String.valueOf(Preferences.Session.getIdUser(context))})){

            List<Song> songs = new ArrayList<>();
            if(cursor.moveToFirst()) {
                do {
                    songs.add(cursor.getInt(cursor.getColumnIndex(CompoundDescriptor.ORDER)),
                            SongBuilder.create().setId(cursor.getInt(cursor.getColumnIndex(SongDescriptor.ID_SONG)))
                                    .setPath(cursor.getString(cursor.getColumnIndex(SongDescriptor.PATH)))
                                    .build());

                } while (cursor.moveToNext());
            }
            return songs;
        }
    }

    @Override
    public List<Song> getAllSong(int id_playlist) {
        SQLiteDatabase db = daoFactory.getReadableDatabase();

        try(Cursor cursor = db.rawQuery("SELECT S.*, c." +CompoundDescriptor.ORDER +
                " FROM SONG S JOIN COMPOUND C ON (S.ID_SONG=C.ID_SONG) JOIN PLAYLIST P ON ( C.id_playlist = P.id_playlist) " +
                "WHERE P.id_playlist = ? " +
                "ORDER by order_song;", new String[]{String.valueOf(id_playlist)})){

            List<Song> songs = new ArrayList<>();
            if(cursor.moveToFirst()) {
                do {
                    songs.add(cursor.getInt(cursor.getColumnIndex(CompoundDescriptor.ORDER)),
                            SongBuilder.create()
                                    .setId(cursor.getInt(cursor.getColumnIndex(SongDescriptor.ID_SONG)))
                                    .setTitle(cursor.getString(cursor.getColumnIndex(SongDescriptor.TITLE)))
                                    .setArtist(cursor.getString(cursor.getColumnIndex(SongDescriptor.ARTIST)))
                                    .setDuration(cursor.getLong(cursor.getColumnIndex(SongDescriptor.DURATION)))
                                    .setPath(cursor.getString(cursor.getColumnIndex(SongDescriptor.PATH)))
                                    .build());

                } while (cursor.moveToNext());
            }
            return songs;
        }
    }

    @Override
    public PlayList insert(PlayList playList){
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        final AtomicBoolean success = new AtomicBoolean(false);
        return DataBaseUtilities.transaction(db, ()-> {

            final ContentValues playListContentValues = new ContentValues();
            int id_user = Preferences.Session.getIdUser(context);
            playListContentValues.put(UserDescriptor.ID_USER, id_user);
            int id_playlist = DataBaseUtilities.getNextId(db, id_user,  PlayListDescriptor.TABLE_PLAYLIST, PlayListDescriptor.ID_PLAYLIST);
            playListContentValues.put(PlayListDescriptor.ID_PLAYLIST, id_playlist);
            playListContentValues.put(PlayListDescriptor.NAME, playList.getName());
            playListContentValues.put(PlayListDescriptor.DATE_CREATION, playList.getCreationDate());
            if(playList.isUseLikePrimary()) {
                resetPrimary(db);
                playListContentValues.put(PlayListDescriptor.USE_PRIMARY, 1);
            }
            success.set(db.insert(PlayListDescriptor.TABLE_PLAYLIST, null, playListContentValues) != -1);
            if(!success.get()) throw  new SQLiteException("Insert PLAYLIST FAIL");
            playList.setId(id_playlist);
            // songs
            success.set(SqlLiteSongDao.create(context).insertAll(db, playList.songs()));
            // compound
            success.set(SqlLiteCompoundDao.create(context).insertAll(db, id_playlist, playList.songs()));

           return success.get();
        }) ? playList : null;
    }

    @Override
    public boolean updateName(String name, int id_playlist) {
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        final ContentValues playlistName = new ContentValues();
        playlistName.put(PlayListDescriptor.NAME, name);
        final Map<String, String> map = new HashMap<>();
        map.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));
        return DataBaseUtilities.update(db, PlayListDescriptor.TABLE_PLAYLIST, playlistName, map);
    }

    @Override
    public boolean updateUse(int id_playlist) {
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        AtomicBoolean success = new AtomicBoolean(false);
        return DataBaseUtilities.transaction(db, ()->{

            resetPrimary(db);

            final ContentValues use = new ContentValues();
            use.put(PlayListDescriptor.USE_PRIMARY, 1);

            final Map<String, String> map = new HashMap<>();
            map.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));
            map.put(UserDescriptor.ID_USER, String.valueOf(Preferences.Session.getIdUser(context)));

            success.set(db.update(PlayListDescriptor.TABLE_PLAYLIST,
                    use,
                    DataBaseUtilities.buildWhereClause(map.keySet()),
                    map.values().toArray(new String[0])) > 0);

            return success.get();

        });
    }

    @Override
    public ArrayList<Song> updateSongs(List<Song> songs, int id_playlist) {
        final SQLiteDatabase db = daoFactory.getWritableDatabase();
        DataBaseUtilities.transaction(db, ()->{
            SqlLiteSongDao.create(context).insertAll(db, songs);
            Log.d(TAG, songs.toString());
            // TODO: 12/02/2020 MIGLIORARE
            int orderNext = SqlLiteCompoundDao.getNextOrder(db, id_playlist);
            final CollectionsUtilities.ListUtilities.ArrayListAnySize<Song> newOrderSong =
                    new CollectionsUtilities.ListUtilities.ArrayListAnySize<>();
            for (int j =0, i = orderNext; j < songs.size() && i < (orderNext + songs.size()) ; j++, i++)
                newOrderSong.add(i, songs.get(j));

            Log.d(TAG, newOrderSong.toString());

            return SqlLiteCompoundDao.create(context).insertAll(db, id_playlist, newOrderSong);
        });
        return new ArrayList<>(songs);
    }

    @Override
    public boolean delete(int id_playlist) {
        // ELIMINARE NELLA PLAYLIST IN CUI è SELEZIONATA ,
        // SE IN COUMPOUND NON CI SONO PIU ASSOCIAZIONI
        // ALLORA CANCELLARE ANCHE NELLA TABELLA SONG
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        return DataBaseUtilities.transaction(db, ()-> {
            SqlLiteCompoundDao.create(context).delete(db,id_playlist);

            Map<String, String> map = new HashMap<>();
            map.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));

            return db.delete(PlayListDescriptor.TABLE_PLAYLIST,
                    DataBaseUtilities.buildWhereClause(map.keySet()),
                    map.values().toArray(new String[0])) > 0;
        });

    }

    private void resetPrimary(SQLiteDatabase db){
        String id_user = String.valueOf(Preferences.Session.getIdUser(context));

        final ContentValues contentValues = new ContentValues();
        contentValues.put(PlayListDescriptor.USE_PRIMARY, 0);

        Map<String, String> whereCondition = new HashMap<>();
        whereCondition.put(UserDescriptor.ID_USER, id_user);

        db.update(PlayListDescriptor.TABLE_PLAYLIST, contentValues,
                DataBaseUtilities.buildWhereClause(whereCondition.keySet()),
                whereCondition.values().toArray(new String[0]));
    }

}
