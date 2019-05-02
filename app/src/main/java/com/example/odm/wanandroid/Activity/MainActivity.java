package com.example.odm.wanandroid.Activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.odm.wanandroid.Application.MyApplication;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.Util.CookieUtil;
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.bean.User;

public class MainActivity extends AppCompatActivity {
    private User user;
    final String LoginPath = "https://www.wanandroid.com/user/login";
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStatus();
    }

    /**
     * 进入软件后判断是否登录，依次检查登录状态和Cookie存储
     */
    public void checkStatus() {
        user = new User();
        //判断是否已经登录
        if(!isLogin) {
            //判断Cookie是否为空
            Log.e("cookie",CookieUtil.getCookiePreference(MyApplication.getContext()));
            if( ! (CookieUtil.getCookiePreference(MyApplication.getContext())).equals("")){
                //利用Cookie自动登录
                new Thread(){
                    public void run(){
                        //发送POST登录请求并处理返回的JSON数据
                        new JsonUtil().handleUserdata(user,new PostUtil().sendPost(LoginPath,""));
                        if(user.getErrorCode() == 0){
                            Toast.makeText(MainActivity.this,"自动登录成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();
            }
        }
    }
}
