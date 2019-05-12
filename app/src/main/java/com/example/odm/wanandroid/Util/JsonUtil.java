package com.example.odm.wanandroid.Util;

import android.content.Context;
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

    private Context mContext;

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
            if(acceptdata == null){
                System.out.println("数据为空");
            }else {
                JSONObject json = new JSONObject(acceptdata);
                JSONObject data = json.getJSONObject("data");
                //JSONObject data = new JSONObject(acceptdata);
                int curPage = data.getInt("curPage");
                pageListData.setCurPage(curPage);
                int total = data.getInt("total");
                JSONArray datas = data.getJSONArray("datas");
                for(int i = 0; i <datas.length(); i++) {
                    JSONObject content = datas.getJSONObject(i);
                    Article article = new Article(  content.getString("superChapterName"),
                            content.getString("author"),
                            content.getString("niceDate"),
                            content.getString("title"));
                            //title_fix(content.getString("title")));
                    article.setLink(content.getString("link"));
                    articleList.add(article);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 搜索接口返回的数据，title属性带有<em class='highlight'>与</em>，不让它们显示在标题上
     * @param uncorrent_title
     * @return
     */
    public static String title_fix(String uncorrent_title){
        String corrent_title = "";
        uncorrent_title = uncorrent_title.replace("<em class='highlight'>","");
        corrent_title = uncorrent_title.replace("</em>","");
        return corrent_title;
    }
}
