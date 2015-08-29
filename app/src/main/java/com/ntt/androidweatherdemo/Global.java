package com.ntt.androidweatherdemo;

import android.app.Fragment;
import android.view.MenuItem;

/**
 * Created by Lee on 10/23/2014.
 */
public class Global {
    public static Fragment fragmentMain = null;
    public static Fragment fragmentDetail = null;
    public static MenuItem refreshMenuItem;
    public static String KEY_REFRESH = "refresh";
    public static boolean REFRESH = false;
    public static boolean START_APP = false;
}
