package com.ntt.androidweatherdemo;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Sony on 10/21/2014.
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "DownloadService";

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty("url")) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                ArrayList<WeatherData> results = downloadData(url);
                if (results != null && results.size() > 0) {
                    bundle.putSerializable("results", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        this.stopSelf();
    }

    private ArrayList<WeatherData> downloadData(String requestUrl) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        //urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            ArrayList<WeatherData> results = parseResult(response);
            return results;
        }
        return null;
    }

    private ArrayList<WeatherData> parseResult(String result) {
        ArrayList<WeatherData> resData = new ArrayList<WeatherData>();
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.optJSONObject("data");
            //JSONObject curWeather = response.optJSONObject("current_condition");
            JSONArray weather = data.optJSONArray("weather");
            WeatherData tmpData = null;

            //Database


            for (int i = 0; i < weather.length(); ++i) {
                JSONObject tmp = weather.optJSONObject(i);
                tmpData = new WeatherData();
                tmpData.setDate(java.sql.Date.valueOf(tmp.optString("date")));
                tmpData.setTempMaxC(Integer.parseInt(tmp.optString("tempMaxC")));
                tmpData.setTempMinC(Integer.parseInt(tmp.optString("tempMinC")));
                tmpData.setWeatherDesc(tmp.optJSONArray("weatherDesc").optJSONObject(0).optString("value"));
                String bitmapUrl = tmp.optJSONArray("weatherIconUrl").optJSONObject(0).optString("value");
                tmpData.setWeatherIcon(downloadImage(bitmapUrl));

                resData.add(tmpData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resData;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    protected Bitmap downloadImage(String address) {
        // Convert string to URL
        URL url = getUrlFromString(address);
        // Get input stream
        InputStream in = getInputStream(url);
        // Decode bitmap
        Bitmap bitmap = decodeBitmap(in);
        // Return bitmap result
        return bitmap;
    }

    private URL getUrlFromString(String address) {
        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e1) {
            url = null;
        }
        return url;
    }

    private InputStream getInputStream(URL url) {
        InputStream in;
        // Open connection
        URLConnection conn;
        try {
            conn = url.openConnection();
            conn.connect();
            in = conn.getInputStream();
        } catch (IOException e) {
            in = null;
        }
        return in;
    }

    private Bitmap decodeBitmap(InputStream in) {
        Bitmap bitmap;
        try {
            // Turn response into Bitmap
            bitmap = BitmapFactory.decodeStream(in);
            // Close the input stream
            in.close();
        } catch (IOException e) {
            in = null;
            bitmap = null;
        }
        return bitmap;
    }

}
