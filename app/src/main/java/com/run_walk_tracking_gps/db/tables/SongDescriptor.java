package com.run_walk_tracking_gps.db.tables;

public class SongDescriptor {

    private SongDescriptor(){}

    public static final String TABLE_SONG = "SONG";

    public static final String ID_SONG = "id_song";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String DURATION = "duration";
    public static final String PATH = "path";

    public static  final String CREATE_TABLE_SONG =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SONG + "( " +
                    ID_SONG + " INTEGER PRIMARY KEY ," +
                    TITLE  + " TEXT NOT NULL," +
                    ARTIST + " TEXT NOT NULL," +
                    DURATION + " LONG NOT NULL , " +
                    PATH + " TEXT NOT NULL, "+
                    "CONSTRAINT ID_Song UNIQUE ("+ID_SONG+"))";


    public static final String CREATE_INDEX_SONG =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_Song on "+ TABLE_SONG+" ("+ID_SONG+");";


}
