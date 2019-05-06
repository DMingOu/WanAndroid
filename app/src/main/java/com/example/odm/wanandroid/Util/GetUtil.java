package com.example.odm.wanandroid.Util;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 工具类：处理GET网络请求
 * Created by ODM on 2019/5/6.
 */

public class GetUtil {
    /**
     * @param Path  进行GET请求的接口字符串
     * @param resultdata  用来接收JSON数据的字符串
     */
    public  static  String  sendGet(final String Path , String resultdata , final Handler handler){

//        //new Thread() {
//            @Override
//            public void run() {
                HttpURLConnection connection;
                try {
                    System.out.println("正在进行sendPost");
                    URL url = new URL(Path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    //接受数据
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                        String line;
                        //不为空进行操作，定义接收JSON数据字符串
                        //String resultdata = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            resultdata += line;
                        }
                        Log.e("resultdata",resultdata);
                        if (resultdata == null) System.out.println("接收数据一开始就为空了QAQ");
                        //System.out.println(date+resultdata);
                        handler.sendEmptyMessage(0x01);  //请求完毕，返回自己自定义的信息 id
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return resultdata;
            }
        //}.start();
    }

