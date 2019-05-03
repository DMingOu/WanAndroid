package com.example.odm.wanandroid.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ODM on 2019/5/3.
 */

public class SharedPreferencesUtil {

    final static String LOGIN = "Login";

    public static void saveLoginSharedPreferences(Context context,String loginUserName,String loginUserPassword) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString("loginUserName",loginUserName);
        editor.putString("loginUserPassword",loginUserPassword);
        editor.apply();
    }

    public static String getLoginSharedPreferences(Context context) throws UnsupportedEncodingException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        String loginUserName = sharedPreferences.getString("loginUserName","");
        String loginUserPassword = sharedPreferences.getString("loginUserPassword","");
        final String data = "username="+ URLEncoder.encode(loginUserName,"UTF-8")+"&password="+URLEncoder.encode(loginUserPassword,"UTF-8");
        return  data;
    }
}
