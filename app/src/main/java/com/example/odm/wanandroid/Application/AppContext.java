package com.example.odm.wanandroid.Application;

/**
 * 获取上下文context
 * Created by ODM on 2019/4/30.
 */
import android.content.Context;
public class AppContext {
    private static Context mContext;
    private static AppContext mInstance;


    private AppContext(Context mCon) {
        mContext = mCon;
    }

    public static Context getContext() {
        return mContext;
    }

    public static AppContext getInstance() {
        return mInstance;
    }

    static void initialize(Context context) {

    }
}