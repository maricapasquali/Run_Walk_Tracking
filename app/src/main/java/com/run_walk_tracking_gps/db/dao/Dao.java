package com.run_walk_tracking_gps.db.dao;

public interface Dao {

    WorkoutDao getWorkoutDao();

    UserDao getUserDao();

    UserDao.ImageDao getImageDao();

    StatisticsDao getStatisticsDao();

    StatisticsDao.WeightDao getWeightDao();

    SettingsDao getSettingDao();

    PlayListDao getPlayListDao();

    SongDao getSongDao();

    CompoundDao getCompoundDao();
}
