package com.example.odm.wanandroid.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.odm.wanandroid.application.MyApplication;
import com.example.odm.wanandroid.db.ArticlebaseHelper;
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

    private  Context mContext;

    /**
     * 处理关于用户个人信息的Json数据
     * @param user
     * @param acceptdata
     */
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
     * 处理关于文章列表的JSON数据
     * @param articleList  文章列表对象
     * @param pageListData 页码对象
     * @param acceptdata   接受的JSON数据
     * @return articleList  填充了文章的文章列表
     */
    public static void handleArtcileData(List<Article> articleList , PageListData pageListData, String acceptdata ,boolean isRefresh) {
        try{
            if(acceptdata == null){
                System.out.println("数据为空");
            }else {
                ArticlebaseHelper dbhelper = new ArticlebaseHelper(MyApplication.getContext(),"Article.db",null,1);
                SQLiteDatabase db = dbhelper.getReadableDatabase();
                JSONObject json = new JSONObject(acceptdata);
                JSONObject data = json.getJSONObject("data");
                int curPage = data.getInt("curPage");
                pageListData.setCurPage(curPage);
                int total = data.getInt("total");
                JSONArray datas = data.getJSONArray("datas");
                for(int i = 0; i <datas.length(); i++) {
                    JSONObject content = datas.getJSONObject(i);
                    //通过传参进来判定此时处理的是需要更新的数据还是普通的加载下一页的数据
                    if(isRefresh){
                        boolean isShown = false;
                        //当前在刷新文章，需要过滤掉已有的文章，需要显示id还没出现在数据库的文章
                        Cursor cursor = db.query("Article",null, null, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                               int id = cursor.getInt(cursor.getColumnIndex("id"));
                                if (content.getInt("id") == id) { //数据库有相同的标题（读过的)，设置为灰色，否则设置为黑色
                                    isShown = true;
                                    break;
                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                        if(isShown) {
                            continue; //如果这篇文章已经被展示了，跳过这篇文章
                        }
                    }
                    Article article = new Article(  content.getString("superChapterName"),
                            content.getString("author"),
                            content.getString("niceDate"),
                            content.getString("title"));
                    article.setLink(content.getString("link"));  //link-->webview跳转
                    article.setId(content.getInt("id"));         //id-->刷新需要判断是否已被显示
                    articleList.add(article);                    //文章列表加入文章对象
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
