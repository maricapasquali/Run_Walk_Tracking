package com.run_walk_tracking_gps.db.tables;


public class CompoundDescriptor {

    private CompoundDescriptor(){}

    public static final String TABLE_COMPOUND = "COMPOUND";

    public static final String ORDER = "order_song";


    public static  final String CREATE_TABLE_COMPOUND =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COMPOUND + "( " +
                    SongDescriptor.ID_SONG + " INTEGER NOT NULL ," +
                    PlayListDescriptor.ID_PLAYLIST  + " INTEGER NOT NULL," +
                    ORDER + " INTEGER NOT NULL, " +
                    "CONSTRAINT ID_compound primary key ("+SongDescriptor.ID_SONG +","+ PlayListDescriptor.ID_PLAYLIST + "), "+
                    "FOREIGN KEY ("+ SongDescriptor.ID_SONG +") REFERENCES " +SongDescriptor.TABLE_SONG +" ON DELETE CASCADE, " +
                    "FOREIGN KEY ("+ PlayListDescriptor.ID_PLAYLIST +") REFERENCES " +PlayListDescriptor.TABLE_PLAYLIST+" ON DELETE CASCADE)";



    public static final String CREATE_INDEX_COMPOUND =
            "CREATE UNIQUE INDEX IF NOT EXISTS ID_compound on "+ TABLE_COMPOUND
                    +" ("+SongDescriptor.ID_SONG+"," + PlayListDescriptor.ID_PLAYLIST + ");";


    public static final String CREATE_INDEX_PLAYLIST =
            "CREATE INDEX IF NOT EXISTS FKcom_Pla on "+ TABLE_COMPOUND
                    +" ("+ PlayListDescriptor.ID_PLAYLIST + ");";
}
