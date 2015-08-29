package com.ntt.androidweatherdemo;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Sony on 10/22/2014.
 */
public class WeatherData {
    private Date date;
    private int tempMinC;

    private int tempMaxC;
    private Bitmap weatherIcon;
    private String weatherDesc;

    public WeatherData(){}

    public WeatherData(Date date, String weatherDesc, int tempMinC, int tempMaxC, Bitmap weatherIcon) {
        this.date = date;
        this.tempMinC = tempMinC;
        this.tempMaxC = tempMaxC;
        this.weatherIcon = weatherIcon;
        this.weatherDesc = weatherDesc;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTempMinC() {
        return tempMinC;
    }

    public void setTempMinC(int tempMinC) {
        this.tempMinC = tempMinC;
    }

    public int getTempMaxC() {
        return tempMaxC;
    }

    public void setTempMaxC(int tempMaxC) {
        this.tempMaxC = tempMaxC;
    }

    public Bitmap getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(Bitmap weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }
}
