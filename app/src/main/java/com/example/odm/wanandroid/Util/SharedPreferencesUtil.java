package com.example.odm.wanandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * SharedPreferences工具类
 * Created by ODM on 2019/5/3.
 */

public class SharedPreferencesUtil {

    final static private String LOGIN = "Login";

    /**
     * 将登陆用户名和密码存储在SharedPreferences上
     * @param context 上下文
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
     */
    public static String getLoginSharedPreferences(Context context)  {
        String data = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        String loginUserName = sharedPreferences.getString("loginUserName","");
        String loginUserPassword = sharedPreferences.getString("loginUserPassword","");
        //当用户名与密码为空，即退出登录状态，返回空字符串，不含登录信息
        if(loginUserName.equals("") && loginUserPassword.equals("")) {
            data = "";
        } else {
            try {
                data = "username=" + URLEncoder.encode(loginUserName, "UTF-8") +
                        "&password=" + URLEncoder.encode(loginUserPassword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return  data;
    }
}
