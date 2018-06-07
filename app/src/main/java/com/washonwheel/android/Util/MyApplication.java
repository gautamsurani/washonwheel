package com.washonwheel.android.Util;

import android.app.Application;
import android.content.Context;

/*
 * Created by Welcome on 29-01-2018.
 */

public class MyApplication extends Application {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }

}