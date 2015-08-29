package com.ntt.androidweatherdemo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Sony on 10/22/2014.
 */
public class WeatherContentProvider extends ContentProvider {

    private DatabaseHandler database;
    private static final int WEATHER = 20;
    private static final int WEATHE_ID = 10;

    private static final String AUTHORITY = "com.ntt.androidweatherdemo.contentprovider";
    private static final String BASE_PATH = "weathers";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/weathers";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/weather";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, WEATHER);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WEATHE_ID);
    }

    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(WeatherTable.TABLE_WEATHER);
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case WEATHER:
                break;
            case WEATHE_ID:
                queryBuilder.appendWhere(WeatherTable.KEY_ID + "=" +uri.getLastPathSegment());
                break;
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDb = database.getWritableDatabase();
        int rowDeleted = 0;
        long id = 0;
        switch (uriType) {
            case WEATHER:
                id = sqlDb.insert(WeatherTable.TABLE_WEATHER, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
