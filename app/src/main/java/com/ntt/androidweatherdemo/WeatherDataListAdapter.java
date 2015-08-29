package com.ntt.androidweatherdemo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Sony on 10/22/2014.
 */
public class WeatherDataListAdapter extends SimpleCursorAdapter {

    private Cursor cursor;
    private Activity context;
    private LayoutInflater mInflater;
    public WeatherDataListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = (Activity) context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= mInflater.inflate(R.layout.list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mDate = (TextView) view.findViewById(R.id.tv_day);
        viewHolder.mMinC = (TextView) view.findViewById(R.id.tv_temperature_min);
        viewHolder.mMaxC = (TextView) view.findViewById(R.id.tv_temperature_max);
        viewHolder.mIcon = (ImageView) view.findViewById(R.id.img_icon);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_DATE)));
        viewHolder.mMinC.setText("MIN: " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MINC)));
        viewHolder.mMaxC.setText(" MAX: " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MAXC)));
        byte[] bitmapArray = cursor.getBlob(cursor.getColumnIndexOrThrow(WeatherTable.KEY_ICON));
        Bitmap bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        viewHolder.mIcon.setImageBitmap(bmp);
    }


    static class ViewHolder{
        protected TextView mDate;
        protected TextView mMinC;
        protected TextView mMaxC;
        protected ImageView mIcon;
    }
}
