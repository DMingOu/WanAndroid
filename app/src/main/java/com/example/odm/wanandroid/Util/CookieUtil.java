package com.example.odm.wanandroid.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Cookie工具类
 * Created by ODM on 2019/5/2.
 */

public class CookieUtil {

    /**
     * 将cookie数据保存在ShardPreferences
     * @param context 上下文
     * @param cookiedata  cookie数据
     */
    public static void saveCookiePreference(Context context, String cookiedata) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("islogined",Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString("cookie",cookiedata);
        editor.apply();
    }

    /**
     * 得到sharedPreference中的cookie，并作为返回值
     * @param context 上下文
     * @return s
     */
    public static String getCookiePreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("islogined",Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("cookie","");
        return s;
    }

}
