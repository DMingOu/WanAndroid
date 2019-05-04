package com.example.odm.wanandroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.Util.SharedPreferencesUtil;
import com.example.odm.wanandroid.bean.Article;
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
    private ArticleAdapter articleAdapter;
    private RecyclerView mrecyclerView;
    private LinearLayoutManager lineLayoutManager;
    final String LoginPath = "https://www.wanandroid.com/user/login";
    private static boolean isLogin = false;
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
        mrecyclerView = (RecyclerView)findViewById(R.id.rv_item_article);
        lineLayoutManager = new LinearLayoutManager(MainActivity.this);
        lineLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mrecyclerView.setLayoutManager(lineLayoutManager);
        articleAdapter = new ArticleAdapter(articleList);
        mrecyclerView.setAdapter(articleAdapter);
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
                    Thread.sleep(2000);
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
    protected void initArticlesData(){
        articleList.clear();
        for(int i = 1; i <= 20  ;i++){
            articleList.add(new Article("文章类别"+i,"作者"+i, "热度时间"+i,"文章标题"+i));
        }
    }

}
