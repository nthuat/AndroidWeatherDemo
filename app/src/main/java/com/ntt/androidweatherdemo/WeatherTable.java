package com.ntt.androidweatherdemo;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sony on 10/22/2014.
 */
public class WeatherTable {
    public static final String TABLE_WEATHER = "weather";
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DESC = "desc";
    public static final String KEY_MINC = "minc";
    public static final String KEY_MAXC = "maxc";
    public static final String KEY_ICON = "icon";
    public static final String[] projection = new String[]{KEY_ID, KEY_DATE, KEY_DESC, KEY_MINC, KEY_MAXC, KEY_ICON};
    public static final String CREATE_WEATHER_DATA_TABLE = "CREATE TABLE " + TABLE_WEATHER + "(" + KEY_ID + " INT PRIMARY KEY,"
            + KEY_DATE + " DATE," + KEY_DESC + " TEXT," + KEY_MINC + " TEXT,"
            + KEY_MAXC + " TEXT," + KEY_ICON + " BLOB" + ")";

    public static void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_WEATHER_DATA_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        onCreate(db);
    }
}
