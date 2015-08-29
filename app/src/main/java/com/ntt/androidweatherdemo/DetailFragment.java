package com.ntt.androidweatherdemo;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class DetailFragment extends Fragment{

	private View rootView;
    private ImageView mDetailsIcon;
    private TextView mDetailsDesc;
    private TextView mDetailsMinC;
    private TextView mDetailsMaxC;
    private Uri uri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDetailsIcon = (ImageView) rootView.findViewById(R.id.img_icon_detail);
        mDetailsMinC = (TextView) rootView.findViewById(R.id.tv_temperature_min);
        mDetailsMaxC = (TextView) rootView.findViewById(R.id.tv_temperature_max);
        mDetailsDesc = (TextView) rootView.findViewById(R.id.tv_desc);

        Bundle b = this.getArguments();
        String url = b.getString(WeatherContentProvider.CONTENT_ITEM_TYPE);
        uri = Uri.parse(url);
        fillData(uri);

		return rootView;
	}

    private void fillData(Uri uri){
        Cursor cursor = getActivity().getContentResolver().query(uri, WeatherTable.projection, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
            mDetailsMinC.setText("Min: "+ cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MINC)));
            mDetailsMaxC.setText("Max: "+cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_MAXC)));
            mDetailsDesc.setText("Weather will be " + cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_DESC)));
            byte[] bitmapArray = cursor.getBlob(cursor.getColumnIndexOrThrow(WeatherTable.KEY_ICON));
            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            mDetailsIcon.setImageBitmap(bmp);
            getActivity().getActionBar().setTitle(cursor.getString(cursor.getColumnIndexOrThrow(WeatherTable.KEY_DATE)));
            cursor.close();
        }
    }
}
