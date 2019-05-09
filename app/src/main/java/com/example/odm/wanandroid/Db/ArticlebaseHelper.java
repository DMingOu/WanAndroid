package com.example.odm.wanandroid.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 存储Article的标题的数据库
 * Created by ODM on 2019/5/9.
 */

public class ArticlebaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    //带全部参数的构造函数，此构造函数必不可少
    public ArticlebaseHelper(Context context , String name , SQLiteDatabase.CursorFactory factory,int version) {
        super(context,name,factory,version);
        mContext = context;
    }

    /**
     * 创建数据库
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLE = "create table Article (title text)";
        db.execSQL(CREATE_ARTICLE);
        //Toast.makeText(mContext,"Create Succeeded",Toast.LENGTH_SHORT).show();
    }

    /**
     * 数据库升级方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
