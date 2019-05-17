package com.example.odm.wanandroid.base;

import android.os.Handler;

/**
 * Created by ODM on 2019/5/14.
 */

public  class HandlerManger {
    public static final String TAG = "Manager";

    private static HandlerManger mInstance;

    public Handler mHandler;

    public synchronized static HandlerManger getInstance() {
        if (mInstance == null) {
            mInstance = new HandlerManger();
        }
        return mInstance;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 利用Handler发送消息
     */
    public void sendSuccessMessage() {
        mHandler.sendEmptyMessage(0x01);
    }

    public void sendFailMessage() {
        mHandler.sendEmptyMessage(0x00);
    }

}
