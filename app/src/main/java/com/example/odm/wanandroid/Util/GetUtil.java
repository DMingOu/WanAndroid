package com.example.odm.wanandroid.util;

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
     * @return resultdata 返回接收到JSON数据的字符串
     */
    public  static  String  sendGet( String Path , String resultdata ){

                HttpURLConnection connection;
                try {
                    URL url = new URL(Path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    //接收数据
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                        String line;
                        //不为空进行操作
                        while ((line = bufferedReader.readLine()) != null) {
                            resultdata += line;
                        }
                        if (resultdata == null) System.out.println("接收数据一开始就为空了");
                        //handler.sendEmptyMessage(0x01);  //请求完毕，返回自己自定义的信息 id，与主线程通知，开启下一步操作
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return resultdata;
            }
    }

