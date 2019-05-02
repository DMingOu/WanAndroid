package com.example.odm.wanandroid.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by ODM on 2019/5/2.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
