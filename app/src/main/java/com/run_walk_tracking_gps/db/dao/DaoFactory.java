package com.run_walk_tracking_gps.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.run_walk_tracking_gps.db.tables.CompoundDescriptor;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.PlayListDescriptor;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.db.tables.SongDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.db.tables.WeightDescriptor;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;

public class DaoFactory extends SQLiteOpenHelper implements Dao {

    private static final String TAG = DaoFactory.class.getName();
    private static DaoFactory DaoFactoryInstance = null;
    private static final String DB_NAME = "RUN_WALK_TRACKING_DB.db";
    private static final int DB_VERSION = 4;
    private Context context;

    public static synchronized DaoFactory getInstance(Context context){
        if(DaoFactoryInstance == null)
            DaoFactoryInstance = new DaoFactory(context.getApplicationContext());
        return DaoFactoryInstance;
    }

    private DaoFactory(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public static boolean existDatabase(Context context) {
        return context.getDatabasePath(DB_NAME).exists();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "onOpen");
        db.execSQL("PRAGMA FOREIGN_KEYS=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate");
        // CREAZIONE TABELLE
        sqLiteDatabase.execSQL(UserDescriptor.CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(ImageProfileDescriptor.CREATE_TABLE_PROFILE_IMAGE);
        sqLiteDatabase.execSQL(SettingsDescriptor.SportDefault.CREATE_TABLE_SPORT_DEFAULT);
        sqLiteDatabase.execSQL(SettingsDescriptor.TargetDefault.CREATE_TABLE_TARGET_DEFAULT);
        sqLiteDatabase.execSQL(SettingsDescriptor.UnitMeasureDefault.CREATE_TABLE_UNIT_MEASURE_DEFAULT);
        sqLiteDatabase.execSQL(WeightDescriptor.CREATE_TABLE_WEIGHT);
        sqLiteDatabase.execSQL(WorkoutDescriptor.CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(PlayListDescriptor.CREATE_TABLE_PLAYLIST);
        sqLiteDatabase.execSQL(SongDescriptor.CREATE_TABLE_SONG);
        sqLiteDatabase.execSQL(CompoundDescriptor.CREATE_TABLE_COMPOUND);

        // CREAZIONE TRIGGER
        sqLiteDatabase.execSQL(PlayListDescriptor.CREATE_TRIGGER_BEFORE_INSERT);
        sqLiteDatabase.execSQL(PlayListDescriptor.CREATE_TRIGGER_BEFORE_UPDATE);
        sqLiteDatabase.execSQL(CompoundDescriptor.CREATE_TRIGGER_BEFORE_DELETE);

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
        sqLiteDatabase.execSQL(PlayListDescriptor.CREATE_INDEX_PLAYLIST);
        sqLiteDatabase.execSQL(PlayListDescriptor.CREATE_INDEX_USER);
        sqLiteDatabase.execSQL(SongDescriptor.CREATE_INDEX_SONG);
        sqLiteDatabase.execSQL(CompoundDescriptor.CREATE_INDEX_COMPOUND);
        sqLiteDatabase.execSQL(CompoundDescriptor.CREATE_INDEX_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        if(newVersion > oldVersion){
            onCreate(db);
        }
    }


    private WorkoutDao workoutDao;
    private UserDao userDao;
    private UserDao.ImageDao imageDao;
    private StatisticsDao statisticsDao;
    private StatisticsDao.WeightDao weightDao;
    private SettingsDao settingsDao;
    private PlayListDao playListDao;
    private SongDao songDao;
    private CompoundDao compoundDao;

    @Override
    public WorkoutDao getWorkoutDao(){
        if(workoutDao == null) workoutDao = SqlLiteWorkoutDao.create(context, this);
        return workoutDao;
    }

    @Override
    public UserDao getUserDao(){
        if(userDao == null) userDao = SqlLiteUserDao.create(context, this);
        return userDao;
    }

    @Override
    public UserDao.ImageDao getImageDao(){
        if(imageDao==null) imageDao = SqlLiteUserDao.SqlLiteImageDao.create(context, this);
        return imageDao;
    }

    @Override
    public StatisticsDao getStatisticsDao(){
        if(statisticsDao == null) statisticsDao = SqlLiteStatisticsDao.create(context, this);
        return statisticsDao;
    }

    @Override
    public StatisticsDao.WeightDao getWeightDao(){
        if(weightDao == null) weightDao = SqlLiteStatisticsDao.SqlLiteWeightDao.create(context, this);
        return weightDao;
    }

    @Override
    public SettingsDao getSettingDao(){
        if(settingsDao == null) settingsDao = SqlLiteSettingsDao.create(context, this);
        return settingsDao;
    }

    @Override
    public PlayListDao getPlayListDao(){
        if(playListDao == null) playListDao = SqlLitePlayListDao.create(context, this);
        return playListDao;
    }

    @Override
    public SongDao getSongDao(){
        if(songDao == null) songDao = SqlLiteSongDao.create(context, this);
        return songDao;
    }

    @Override
    public CompoundDao getCompoundDao(){
        if(compoundDao == null) compoundDao = SqlLiteCompoundDao.create(this);
        return compoundDao;
    }

}
