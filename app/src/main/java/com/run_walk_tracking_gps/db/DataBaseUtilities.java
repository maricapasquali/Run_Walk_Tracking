package com.run_walk_tracking_gps.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.run_walk_tracking_gps.db.tables.CompoundDescriptor;
import com.run_walk_tracking_gps.db.tables.PlayListDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataBaseUtilities {

    /**
     * costruisce una stringa che indica la clausola where a partire da un set di colonne su cui fare la selezione.
     * @param columnNames set con i nomi delle colonne su cui basare la costruzione della clausola where in formato stringa
     * @return la stringa corrispondente alla clausola where
     */
    public static String buildWhereClause(Set<String> columnNames){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(String columnName: columnNames) {
            if (first) {
                first = false;
            } else {
                result.append(" AND ");
            }
            result.append(columnName);
            result.append(" = ");
            result.append("?");
        }
        return result.toString();
    }

    public static JSONArray getJSONArrayByCursor(Cursor cursor, Function<Cursor, JSONObject> function) {
        JSONArray weights = new JSONArray();
        if(cursor == null || !cursor.moveToFirst()) return weights;
        do {
            weights.put(function.apply(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return weights;
    }

    public static boolean insert(SQLiteDatabase db, String table, ContentValues contentValues) {
        return transaction(db, () -> db.insert(table, null, contentValues)>0);
    }

    public static boolean replace(SQLiteDatabase db, String table, ContentValues contentValues) {
        return transaction(db, () -> db.replace(table, null, contentValues)>0);
    }

    public static boolean update(SQLiteDatabase db, String table, ContentValues contentValues, Map<String, String> whereCondition) {
        return transaction(db, () -> db.update(table, contentValues, buildWhereClause(whereCondition.keySet()), whereCondition.values().toArray(new String[0]))>0);
    }

    public static boolean delete(SQLiteDatabase db, String table, Map<String, String> whereCondition ){
        return transaction(db, () -> db.delete(table, buildWhereClause(whereCondition.keySet()), whereCondition.values().toArray(new String[0]))!=-1);
    }

    public static boolean transaction(SQLiteDatabase db, Supplier<Boolean> supplier){
        boolean success = false;
        try {
            db.beginTransaction();

            success = supplier.get();

            db.setTransactionSuccessful();
        } catch (SQLiteException e){
            //Error in between database transaction
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }

    public static int getNextId(SQLiteDatabase db, int id_user, String table, String idField){
        try (Cursor c = db.rawQuery("SELECT MAX("+ idField+") as next FROM " +table + " WHERE "+ UserDescriptor.ID_USER +"=? ",
                new String[]{String.valueOf(id_user)})) {
            return (!c.moveToFirst()) ? 1 : c.getInt(c.getColumnIndex("next"))+1 ;
        }
    }

    public static int getNextId(SQLiteDatabase db, String table, String idField){
        try (Cursor c = db.rawQuery("SELECT MAX("+ idField+") as next FROM " +table, null)) {
            return (!c.moveToFirst()) ? 1 : c.getInt(c.getColumnIndex("next"))+1 ;
        }
    }

}
