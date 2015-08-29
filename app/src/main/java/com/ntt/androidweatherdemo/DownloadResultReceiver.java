package com.ntt.androidweatherdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Sony on 10/22/2014.
 */
public class DownloadResultReceiver extends ResultReceiver {
    private Receiver mReceiver;
    public void setReceiver(Receiver mReceiver) {
        this.mReceiver = mReceiver;
    }


    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public DownloadResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null)
            mReceiver.onReceiveResult(resultCode, resultData);
    }
}
