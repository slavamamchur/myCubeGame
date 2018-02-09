package com.cubegames.slava.cubegame.platforms.android.sysutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 300;
    public static final String DB_NAME = "CACHED_IMAGES_DB";
    public static final String TABLE_NAME = "CACHED_IMAGES";
    public static final String MAP_ID_FIELD = "MAP_ID";
    public static final String CHUNK_NUMBER_FIELD = "CHUNK_NUMBER";
    public static final String MAP_IMAGE_FIELD = "MAP_IMAGE";
    public static final String MAP_UPDATED_DATE = "MAP_UPDATED_DATE";

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CACHED_IMAGES ( MAP_ID TEXT NOT NULL, CHUNK_NUMBER INTEGER NOT NULL, MAP_IMAGE BLOB, MAP_UPDATED_DATE NUMERIC, " +
                "UNIQUE (MAP_ID, CHUNK_NUMBER) );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if (oldVersion < 200) {
            db.execSQL("ALTER TABLE CACHED_IMAGES ADD COLUMN MAP_UPDATED_DATE NUMERIC;");

            ContentValues cv = new ContentValues();
            cv.put(MAP_UPDATED_DATE, (Long) null);
            db.update(TABLE_NAME, cv, null, null);
        }*/
    }
}
