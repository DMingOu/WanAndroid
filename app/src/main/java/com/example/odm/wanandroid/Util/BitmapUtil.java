package com.example.odm.wanandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by ODM on 2019/5/19.
 */

public class BitmapUtil {

    private Context mContext;

        public static void getBitmap(final String url, final BitmapFactory.Options options,final List<Bitmap> bitmapList){
        //主要是把网络图片的数据流读入到内存中
                //Android4.0以后，网络请求一定要在子线程中进行
                final byte[] data = getImages(url);
                //更新UI可以在runOnUiThread这个方法或通过handler
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,options);
                        bitmapList.add(bitmap); //添加图片进集合
        }


    public static byte[] getImages(String path){
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(6*1000);
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                while ((len = inputStream.read(buffer)) !=-1){
                    outputStream.write(buffer,0,len);
                }
                outputStream.close();
                inputStream.close();
            }
            return outputStream.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static BitmapFactory.Options getBitmapFactory(final String url, final int pixelW, final int pixelH){
       final BitmapFactory.Options options = new BitmapFactory.Options();

                if (! url.equals("")){
                    //inJustDecodeBounds为true，不返回bitmap，只返回这个bitmap的尺寸
                    options.inJustDecodeBounds = true;
                    //设置图片色彩
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    final byte[] data = getImages(url);
                    //预加载图片
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,options);
                    //获取原始图片宽高
                    int originalW = options.outWidth;
                    int originalH = options.outHeight;
                    //上面设置为true获取bitmap尺寸大小，在这里一定要重新设置为false，否则位图加载不出来
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = getSampleSize(originalW,originalH,pixelW,pixelH);
                }

        return options;
    }

    /**
     *
     * @param originalW 原图宽
     * @param originalH 原图高
     * @param pixelW 指定图片宽度
     * @param pixelH 指定图片高度
     * @return 返回原图缩放大小
     */
    public static int getSampleSize(int originalW, int originalH, int pixelW, int pixelH) {
        int simpleSize = 1;
        if (originalW > originalH && originalW > pixelW){
            simpleSize = originalW / pixelW;
        }else if (originalH > originalW && originalH > pixelH){
            simpleSize = originalH / pixelH;
        }
        if (simpleSize <=0){
            simpleSize = 1;
        }
        return simpleSize;
    }
}
