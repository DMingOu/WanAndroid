package com.example.odm.wanandroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.odm.wanandroid.Adapter.ArticleAdapter;
import com.example.odm.wanandroid.Application.MyApplication;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.Util.GetUtil;
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.Util.SharedPreferencesUtil;
import com.example.odm.wanandroid.bean.Article;
import com.example.odm.wanandroid.bean.PageListData;
import com.example.odm.wanandroid.bean.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    //private Button mtoLoginBtn;
    //private TextView misLoginTv;
    private DrawerLayout mDrawerLayout;
    private List<Article> articleList = new ArrayList<>();
    private List<PageListData> pageListDataList = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager lineLayoutManager;
    private PageListData pageListData;
    private String articleJsondata  = "";
    private String resultdata = "";

    final String LoginPath = "https://www.wanandroid.com/user/login";
    final String ArticleListPath = "https://www.wanandroid.com/article/list/";
    private static boolean isLogin = false;


    //处理返回结果的函数，系统提供的类方法  //handler处理返回数据， 此方法，我写在onCreate()函数外。
   Handler handler = new Handler(){//此函数是属于MainActivity.java所在线程的函数方法，所以可以直接调用MainActivity的 所有方法。
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {   //
                pageListData = new PageListData();
                System.out.println("resultdata为"+resultdata);
                JsonUtil.handleArtcileData(articleList, pageListData, resultdata);
                pageListDataList.add(pageListData);//页码里面有几个对象，就代表文章列表有几页
                resultdata = "";
            } else {
                Toast.makeText(MainActivity.this, "请重新输入地址：", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_article_recycleview);
        initViews();

        checkStatus();
        initArticlesData();
    }

    protected void initViews(){
        //mtoLoginBtn =(Button) findViewById(R.id.btn_toLogin);
        //misLoginTv = (TextView) findViewById(R.id.tv_islogin);
        //mtoLoginBtn.setVisibility(View.GONE);
        //misLoginTv.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_item_article);
        lineLayoutManager = new LinearLayoutManager(MainActivity.this);
        lineLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(lineLayoutManager);
        articleAdapter = new ArticleAdapter(articleList);
        mRecyclerView.setAdapter(articleAdapter);
    }
    /**
     * 进入软件后判断是否登录，依次检查登录状态和SharedPreferences存储的密码
     */
    public void checkStatus() {
        user = new User();
        //判断是否已经登录
        if(!isLogin) {
            try {
                //判断本地的SharePreferences是否为空
                final String data = SharedPreferencesUtil.getLoginSharedPreferences(MyApplication.getContext());
                Log.e("data",data);
                if(! data.equals("")) {
                    //利用本地的SharePreferences自动登录
                    new Thread() {
                        public void run() {
                            //发送POST登录请求并处理返回的JSON数据
                            new JsonUtil().handleUserdata(user, new PostUtil().sendPost(LoginPath, data));
                            if (user.getErrorCode() == 0) {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                                isLogin = true;
                                Looper.loop();
                            }
                        }
                    }.start();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            changeTextViewByRunOnUiThread(user);
        }
    }

    /**
     * 按钮点击事件，跳转去登录页面
     * @param v
     */
    public void toLogin(View v){
        switch (v.getId()){
            case R.id.btn_toLogin:
                Intent intent = new Intent();
                intent.setClass(this,LoginActivity.class);
                startActivity(intent);
        break;
        }
    }

    private  void changeTextViewByRunOnUiThread(final User user)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    //misLoginTv.setText("用户"+user.getUsername());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Looper.prepare();
//                Toast.makeText(MainActivity.this,"用户"+user.getUsername(),Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }
        });
    }

    //初始化文章列表里面的数据
    protected void initArticlesData()  {
        articleList.clear();
//        for(int i = 1; i <= 20  ;i++){
//            articleList.add(new Article("文章类别"+i,"作者"+i, "热度时间"+i,"文章标题"+i));
//        }
        //获取三页的文章列表的数据，解析后添加到articlList里面
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 2; i++) {
                      resultdata =  GetUtil.sendGet(ArticleListPath + i + "/json", articleJsondata, handler);
                    }
                }
            }.start();

    }
}
