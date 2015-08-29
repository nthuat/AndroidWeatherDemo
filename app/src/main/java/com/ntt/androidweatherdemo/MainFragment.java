package com.ntt.androidweatherdemo;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainFragment extends Fragment implements
        DownloadResultReceiver.Receiver,
        LoaderManager.LoaderCallbacks<Cursor> {

    private View rootView;
    private ListView mListData;
    private ArrayList<WeatherData> results;
    private SimpleCursorAdapter arrayAdapter = null;
    private DownloadResultReceiver mReceiver;
    private String url = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=Saigon&format=json&num_of_days=5&key=ss224dej9pbmrbd8rfnr362w";
    private DatabaseHandler db;
    private String[] from;
    private int[] to;
    private LoaderManager loaderManager;
    private int dataSize = 0;
    Intent intentService;
    private ImageView imgIconLandscape;
    private TextView tvDescLandscape;
    private TextView tvTemperatureMinLandscape;
    private TextView tvTemperatureMaxLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().getActionBar().setTitle(R.string.app_name);

        if (Global.REFRESH) {
            if (isCheckOrientationLandscape())
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mListData = (ListView) rootView.findViewById(R.id.listview);


        File database = getActivity().getApplicationContext().getDatabasePath("weatherData.db");

        if (!database.exists() || Global.REFRESH) {
            Log.i("Database", "Not Found");
            loadData();
        } else {
            Log.i("Database", "Found");
            fillData();
        }
        if (isCheckOrientationLandscape()) {
            if (imgIconLandscape == null) {
                imgIconLandscape = (ImageView) rootView.findViewById(R.id.img_icon_detail);
                tvDescLandscape = (TextView) rootView.findViewById(R.id.tv_desc);
                tvTemperatureMinLandscape = (TextView) rootView.findViewById(R.id.tv_temperature_min);
                tvTemperatureMaxLandscape = (TextView) rootView.findViewById(R.id.tv_temperature_max);
            }
            runDetailLandscape(0);
        }

        return rootView;
    }

    private boolean isCheckOrientationLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    private void loadData() {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        intentService = new Intent(Intent.ACTION_SYNC, null, getActivity(), DownloadService.class);
        intentService.putExtra("url", url);
        intentService.putExtra("receiver", mReceiver);
        intentService.putExtra("requestId", 101);
        getActivity().startService(intentService);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                Global.refreshMenuItem.setActionView(R.layout.action_progressbar);
                break;
            case DownloadService.STATUS_FINISHED:
                Global.refreshMenuItem.setActionView(null);
                Global.REFRESH = true;
                results = (ArrayList<WeatherData>) resultData.getSerializable("results");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dataSize = getCountOfRowDB();
                int i = dataSize;
                for (WeatherData data : results) {
                    if(!checkRowIsExist(dateFormat.format(data.getDate()))) {
                        ContentValues values = new ContentValues();
                        values.put(WeatherTable.KEY_ID, i++);
                        values.put(WeatherTable.KEY_DATE, dateFormat.format(data.getDate()));
                        values.put(WeatherTable.KEY_DESC, data.getWeatherDesc());
                        values.put(WeatherTable.KEY_MINC, data.getTempMinC());
                        values.put(WeatherTable.KEY_MAXC, data.getTempMaxC());

                        byte[] byteBitmapArray = convertBitmapToArray(data.getWeatherIcon());
                        values.put(WeatherTable.KEY_ICON, byteBitmapArray);
                        getActivity().getContentResolver().insert(WeatherContentProvider.CONTENT_URI, values);
                    }
                }

                setValueFillData();

                mListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (isCheckOrientationLandscape())
                            runDetailLandscape(id);
                        else
                            navigationDetailPortrait(id);
                    }
                });
        }
    }

    private int getCountOfRowDB() {
        int count = 0;
        Cursor cursor = getActivity().getContentResolver()
                .query(WeatherContentProvider.CONTENT_URI
                        , new String[]{"COUNT(*)"}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
        } else {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private boolean checkRowIsExist(String fieldData) {
        Cursor c = getActivity().getContentResolver().query(WeatherContentProvider.CONTENT_URI, null
                , WeatherTable.KEY_DATE + " = " + "'" + fieldData + "'", null, null);
        return c.getCount() != 0;
    }

    private void navigationDetailPortrait(long id) {
        Global.refreshMenuItem.setVisible(false);
        Uri uri = Uri.parse(WeatherContentProvider.CONTENT_URI + "/" + id);
        Global.fragmentDetail = new DetailFragment();
        Bundle b = new Bundle();
        b.putString(WeatherContentProvider.CONTENT_ITEM_TYPE, uri.toString());
        Global.fragmentDetail.setArguments(b);
        getFragmentManager().beginTransaction().replace(R.id.container, Global.fragmentDetail).commit();
    }

    private void runDetailLandscape(long id) {
        Uri uri = Uri.parse(WeatherContentProvider.CONTENT_URI + "/" + id);
        Cursor cursor = getActivity().getContentResolver().query(uri, WeatherTable.projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            tvTemperatureMinLandscape.setText("Min: " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MINC)));
            tvTemperatureMaxLandscape.setText("Max: " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MAXC)));
            tvDescLandscape.setText("Weather will be " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_DESC)));
            byte[] bitmapArray = cursor.getBlob(cursor.getColumnIndexOrThrow(WeatherTable.KEY_ICON));
            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            imgIconLandscape.setImageBitmap(bmp);
            cursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), WeatherContentProvider.CONTENT_URI, WeatherTable.projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        arrayAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapter.swapCursor(null);
    }

    private byte[] convertBitmapToArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void setValueFillData() {
        from = new String[]{WeatherTable.KEY_DATE, WeatherTable.KEY_MINC, WeatherTable.KEY_MAXC, WeatherTable.KEY_ICON};
        to = new int[]{R.id.tv_day, R.id.tv_temperature_min, R.id.tv_temperature_max, R.id.img_icon};
        arrayAdapter = new WeatherDataListAdapter(getActivity(), R.layout.list_item, null, from, to, 0);
        mListData.setAdapter(arrayAdapter);
        loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void fillData() {
        setValueFillData();
        mListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isCheckOrientationLandscape())
                    runDetailLandscape(id);
                else
                    navigationDetailPortrait(id);
            }
        });
    }
}
