package com.example.odm.wanandroid.Util;

import android.util.Log;

import com.example.odm.wanandroid.bean.Article;
import com.example.odm.wanandroid.bean.PageListData;
import com.example.odm.wanandroid.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
            JSONObject data = value.getJSONObject("data");
            String username = data.getString("username");
            user.setUsername(username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param articleList  文章列表对象
     * @param pageListData 页码对象
     * @param acceptdata   接受的JSON数据
     * @return articleList  填充了文章的文章列表
     */
    public static void handleArtcileData(List<Article> articleList , PageListData pageListData, String acceptdata) {
        try{
            System.out.println("正在进行解析JSON数据"+acceptdata);
            if(acceptdata == null){
                System.out.println("数据为空");
            }else {
                Log.e("acceptdata", acceptdata );
                JSONObject json = new JSONObject(acceptdata);
                JSONObject data = json.getJSONObject("data");
                //JSONObject data = new JSONObject(acceptdata);
                int curPage = data.getInt("curPage");
                System.out.println("curPage的值："+curPage);
                pageListData.setCurPage(curPage);
                JSONArray datas = data.getJSONArray("datas");
                for(int i = 0; i <datas.length(); i++) {
                    JSONObject content = datas.getJSONObject(i);
                    Article article = new Article(  content.getString("superChapterName"),
                            content.getString("author"),
                            content.getString("niceDate"),
                            content.getString("title"));
                    Log.d("title",article.getTitle());
                    article.setLink(content.getString("link"));
                    articleList.add(article);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
