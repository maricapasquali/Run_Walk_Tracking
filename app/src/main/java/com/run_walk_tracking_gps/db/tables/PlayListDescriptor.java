package com.run_walk_tracking_gps.db.tables;

public class PlayListDescriptor {

    private PlayListDescriptor(){}

    public static final String TABLE_PLAYLIST = "PLAYLIST";

    public static final String ID_PLAYLIST = "id_playlist";
    public static final String NAME = "name";
    public static final String DATE_CREATION = "date_creation";
    public static final String USE_PRIMARY = "useLikePrimary";


    public static  final String CREATE_TABLE_PLAYLIST =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST + "( " +
                    ID_PLAYLIST + " INTEGER PRIMARY KEY, " +
                    NAME  + " TEXT NOT NULL, " +
                    DATE_CREATION + " TEXT NOT NULL, " +
                    USE_PRIMARY + " INTEGER NOT NULL DEFAULT 0, " +
                    UserDescriptor.ID_USER + " INTEGER NOT NULL, " +
                    "CONSTRAINT ID_Playlist UNIQUE ("+ID_PLAYLIST+"), " +
                    "FOREIGN KEY ("+ UserDescriptor.ID_USER +") REFERENCES "+UserDescriptor.TABLE_USER +" ON DELETE CASCADE )";


    public static final String CREATE_INDEX_PLAYLIST =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_Playlist on "+ TABLE_PLAYLIST+" ("+ID_PLAYLIST+");";


    public static final String CREATE_INDEX_USER =
            "CREATE INDEX IF NOT EXISTS FKcreate on "+ TABLE_PLAYLIST+" ("+UserDescriptor.ID_USER+");";


    public static final String CREATE_TRIGGER_BEFORE_UPDATE =
            "CREATE TRIGGER IF NOT EXISTS before_update_playlist BEFORE UPDATE ON "+TABLE_PLAYLIST +" FOR EACH ROW "+
            "BEGIN "+
            "UPDATE " +TABLE_PLAYLIST +" SET " + USE_PRIMARY +" = 0 " +
            "WHERE " + UserDescriptor.ID_USER + " = old." +UserDescriptor.ID_USER +" AND "+ID_PLAYLIST+"<> old."+ID_PLAYLIST+"; " +
            "end";

    public static final String CREATE_TRIGGER_BEFORE_INSERT =
            "CREATE TRIGGER IF NOT EXISTS before_insert_playlist BEFORE INSERT ON "+TABLE_PLAYLIST +" FOR EACH ROW "+
                    "when new." + USE_PRIMARY +" = 1 " +
                    "BEGIN "+
                    "UPDATE " +TABLE_PLAYLIST +" SET " + USE_PRIMARY +" = 0 " +
                    "WHERE " + UserDescriptor.ID_USER + " = new." +UserDescriptor.ID_USER +" AND "+ID_PLAYLIST+"<> new."+ID_PLAYLIST+"; " +
                    "end";
}
