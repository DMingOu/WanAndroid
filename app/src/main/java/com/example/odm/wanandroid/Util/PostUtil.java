package com.example.odm.wanandroid.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ODM on 2019/5/2.
 */

public class PostUtil {

    //public static String cookieVal="";  存储cookie字符串
    /**
     * @param path 接口路径
     * @param data 参数数据
     * @return resultdata  以字符串形式返回POST请求得到的数据
     */
    public String sendPost(String path, String data) {
        //定义结果字符串
        String resultdata = "";
        InputStream is;
        try {
            //请求的地址为path，根据地址创建URL对象
            URL url = new URL(path);
            //根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //设置请求的方式-Post
            urlConnection.setRequestMethod("POST");
            //设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            //发送POST请求必须设置允许输入和输出
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            //POST不能缓存
            urlConnection.setUseCaches(false);
            //创造对服务器端的输出流
                OutputStream os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
            //cookieVal = urlConnection.getHeaderField("Set-Cookie");
            //System.out.println("获取到的Cookie："+cookieVal);
            //CookieUtil.saveCookiePreference(MyApplication.getContext(),cookieVal);
                os.close();
            //网络返回码不等于200时，说明网络连接(请求POST)出现异常
            if(urlConnection.getResponseCode() >= 400){
                is = urlConnection.getErrorStream();
            } else {
                //获取服务器端响应的输入流对象
                 is = urlConnection.getInputStream();
            }
                //创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    //根据读取的长度写入到os对象中
                    baos.write(buffer , 0 , len);
                }
                //释放资源
                is.close();
                baos.close();
                //返回结果字符串（JSON数据）
                resultdata = new String(baos.toByteArray());
                //关闭连接
                urlConnection.disconnect();
            } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
         return resultdata;
    }



}
