package com.run_walk_tracking_gps.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.run_walk_tracking_gps.db.DataBaseUtilities;
import com.run_walk_tracking_gps.db.tables.CompoundDescriptor;
import com.run_walk_tracking_gps.db.tables.PlayListDescriptor;
import com.run_walk_tracking_gps.db.tables.SongDescriptor;
import com.run_walk_tracking_gps.model.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlLiteCompoundDao implements CompoundDao {

    private static CompoundDao compoundDao;

    private DaoFactory daoFactory;

    private SqlLiteCompoundDao(Context context){
        daoFactory = DaoFactory.getInstance(context);
    }

    public static synchronized CompoundDao create(Context context) {
        if(compoundDao==null){
            compoundDao = new SqlLiteCompoundDao(context.getApplicationContext());
        }
        return compoundDao;
    }

    @Override
    public boolean reOrderSong(int id_playlist, List<Song> reOrdered) {
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        return DataBaseUtilities.transaction(db, ()->{
            for(int i=0; i< reOrdered.size(); i++){
                ContentValues contentValues = new ContentValues();
                contentValues.put(CompoundDescriptor.ORDER, i);

                Map<String, String> where  = new HashMap<>();
                where.put(PlayListDescriptor.ID_PLAYLIST, String.valueOf(id_playlist));
                where.put(SongDescriptor.ID_SONG, String.valueOf(reOrdered.get(i).getId()));

                db.update(CompoundDescriptor.TABLE_COMPOUND, contentValues,
                        DataBaseUtilities.buildWhereClause(where.keySet()), where.values().toArray(new String[0]));
            }
            return true;
        });
    }

    public static int getNextOrder(SQLiteDatabase db, int id_playlist){
        try (Cursor c = db.rawQuery("SELECT MAX("+ CompoundDescriptor.ORDER +") as next FROM " +CompoundDescriptor.TABLE_COMPOUND
                + " WHERE "+ PlayListDescriptor.ID_PLAYLIST +"=? ", new String[]{String.valueOf(id_playlist)})) {
            return (!c.moveToFirst()) ? 0 : c.getInt(c.getColumnIndex("next"))+1 ;
        }
    }

}
