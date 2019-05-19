package com.example.odm.wanandroid.base;

/**
 * Created by ODM on 2019/5/14.
 */

public class BaseUrl {

    final static String LogoutPath = "https://www.wanandroid.com/user/logout/json";
    final static String RgtPath = "https://www.wanandroid.com/user/register";
    final static String LoginPath = "https://www.wanandroid.com/user/login";
    final static String ArticleListPath = "https://www.wanandroid.com/article/list/";
    final static String SearchPath = "https://www.wanandroid.com/article/query/";
    final static String BannerPath = "https://www.wanandroid.com/banner/json";

    public static String getSearchPath() {
        return SearchPath;
    }

    public static String getRgtPath() {
        return RgtPath;
    }

    public static String getLogoutPath() {
        return LogoutPath;
    }

    public static String getLoginPath() {
        return LoginPath;
    }

    public static String getArticleListPath() {
        return ArticleListPath;
    }

    public static String getBannerPath() {
        return BannerPath;
    }

}
