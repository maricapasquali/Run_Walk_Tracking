package com.run_walk_tracking_gps.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.db.tables.WeightDescriptor;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;

public class DaoFactory extends SQLiteOpenHelper {
    private static DaoFactory DaoFactoryInstance = null;
    public static final String DB_NAME = "RUN_WALK_TRACKING_DB.db";
    public static final int DB_VERSION = 1;

    /*
        perchè syncronized? cosa succede se accedo all'istanza da diversi flussi di controllo?
     */
    public static synchronized DaoFactory getInstance(Context context){
        if(DaoFactoryInstance ==null){
            /*
             * usare il context dell'applicazione (context.getApplicationContext()).
             * evita memory leak; rimane il riferimento dell'activity anche quando viene chiamata la onDestroy
             * leggere il seguente articolo per ulteriori info:
             * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
             */
            DaoFactoryInstance = new DaoFactory(context.getApplicationContext());

        }
        return DaoFactoryInstance;
    }


    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    public static boolean existDatabase(Context context) {
        return context.getDatabasePath(DB_NAME).exists();
    }

    private DaoFactory(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** Added for testing over different db */
    static DaoFactory fromName(Context context, String dbName) {
        return new DaoFactory(context, dbName, false);
    }

    private DaoFactory(Context context, String dbName, boolean inMem){
        super(context, inMem ? null : dbName, null, DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA FOREIGN_KEYS=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //creare qui tutte le tabelle del db
        // CREAZIONE TABELLE

        sqLiteDatabase.execSQL(UserDescriptor.CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(ImageProfileDescriptor.CREATE_TABLE_PROFILE_IMAGE);
        sqLiteDatabase.execSQL(SettingsDescriptor.SportDefault.CREATE_TABLE_SPORT_DEFAULT);
        sqLiteDatabase.execSQL(SettingsDescriptor.TargetDefault.CREATE_TABLE_TARGET_DEFAULT);
        sqLiteDatabase.execSQL(SettingsDescriptor.UnitMeasureDefault.CREATE_TABLE_UNIT_MEASURE_DEFAULT);
        sqLiteDatabase.execSQL(WeightDescriptor.CREATE_TABLE_WEIGHT);
        sqLiteDatabase.execSQL(WorkoutDescriptor.CREATE_TABLE_WORKOUT);

        // CREAZIONE INDICI
        sqLiteDatabase.execSQL(UserDescriptor.CREATE_INDEX_USER);
        sqLiteDatabase.execSQL(UserDescriptor.CREATE_INDEX_NAME);
        sqLiteDatabase.execSQL(ImageProfileDescriptor.CREATE_INDEX_IMG);

        sqLiteDatabase.execSQL(SettingsDescriptor.SportDefault.CREATE_INDEX);

        sqLiteDatabase.execSQL(SettingsDescriptor.TargetDefault.CREATE_INDEX);

        sqLiteDatabase.execSQL(SettingsDescriptor.UnitMeasureDefault.CREATE_INDEX);

        sqLiteDatabase.execSQL(WeightDescriptor.CREATE_INDEX_WEIGHT);
        sqLiteDatabase.execSQL(WeightDescriptor.CREATE_INDEX_DATE);

        sqLiteDatabase.execSQL(WorkoutDescriptor.CREATE_INDEX);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            //adatto il db con delle query alla versione richiesta
        }
    }
}
