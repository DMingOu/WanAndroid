package com.example.odm.wanandroid.Util;

import android.util.Log;

import com.example.odm.wanandroid.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ODM on 2019/5/2.
 */

public class JsonUtil {

    public void handleUserdata(User user,String acceptdata){
        try {
            JSONObject value = new JSONObject(acceptdata);
            int errorCode = value.getInt("errorCode");
            System.out.println("errorCode:"+errorCode);
            user.setErrorCode(errorCode);
            String errorMsg = value.getString("errorMsg");
            user.setErrorMsg(errorMsg);
            Log.e("errorMsg",errorMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
