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

    /**
     * 将登陆用户名和密码存储在SharedPreferences上
     * @param context
     * @param loginUserName  登录用户名
     * @param loginUserPassword 登录密码
     */
    public static void saveLoginSharedPreferences(Context context,String loginUserName,String loginUserPassword) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString("loginUserName",loginUserName);
        editor.putString("loginUserPassword",loginUserPassword);
        editor.apply();
    }

    /**
     * 从SharedPreferences上获取到拼接起来的用户名和密码字符串
     * @param context 上下文
     * @return  拼接起来的用户名和密码字符串data
     * @throws UnsupportedEncodingException
     */
    public static String getLoginSharedPreferences(Context context) throws UnsupportedEncodingException {
        String data;
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        String loginUserName = sharedPreferences.getString("loginUserName","");
        String loginUserPassword = sharedPreferences.getString("loginUserPassword","");
        //当用户名与密码为空，即退出登录状态，返回空字符串，不含登录信息
        if(loginUserName == "" && loginUserPassword == "") {
            data = "";
        } else {
            data = "username=" + URLEncoder.encode(loginUserName, "UTF-8") + "&password=" + URLEncoder.encode(loginUserPassword, "UTF-8");
        }
        return  data;
    }
}
