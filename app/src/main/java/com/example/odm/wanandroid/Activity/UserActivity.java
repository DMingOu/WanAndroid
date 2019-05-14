package com.example.odm.wanandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.Util.SharedPreferencesUtil;

public class UserActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mUserNameTv;
    private Button mLoginBt;
    private Button mRegisterBt;
    private Button mLogoutBt;
    final static String LOGIN = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        checkLoginUserName();
    }

    protected void initViews(){
        mImageView = (ImageView)findViewById(R.id.iv_user);
        mUserNameTv = (TextView)findViewById(R.id.tv_login_name);
        mLoginBt = (Button) findViewById(R.id.bt_login);
        mLogoutBt = (Button) findViewById(R.id.bt_logout);
        mRegisterBt = (Button)findViewById(R.id.bt_register);
        mLoginBt.setVisibility(View.VISIBLE);
        mRegisterBt.setVisibility(View.VISIBLE);
        mLogoutBt.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_User);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_back_32);
            actionBar.setTitle("我");
        }
    }

    /**
     * 获取存储在Sp里面的用户名，若非空，说明已经登录了，就刷新用户名
     */
    protected void checkLoginUserName(){
        SharedPreferences sharedPreferences = UserActivity.this.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        String loginUserName = sharedPreferences.getString("loginUserName","");
        if(! loginUserName.equals("")) {
            mUserNameTv.setText("你好呀,"+loginUserName+",欢迎回来");
            mLoginBt.setVisibility(View.INVISIBLE);
            mRegisterBt.setVisibility(View.INVISIBLE);
            mLogoutBt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用户页面的登录按钮的点击事件--跳转去登录页面
     * @param v
     */
    public void toLogin_User(View v){
        switch (v.getId()){
            case R.id.bt_login:
                Intent intent = new Intent();
                intent.setClass(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 用户页面的注册按钮的点击事件--跳转去注册页面
     * @param v
     */
    public void toRegister_User(View v){
        switch (v.getId()){
            case R.id.bt_register:
                Intent intent = new Intent();
                intent.setClass(this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 用户页面的退出登录按钮的点击事件--显示和隐藏按钮控件，清除本地密码
     * @param v
     */
    public void toLogout_User(View v) {
        switch (v.getId()){
            case R.id.bt_logout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferencesUtil.saveLoginSharedPreferences(UserActivity.this,"","");//将存储的用户名和密码清除
                        mLoginBt.setVisibility(View.VISIBLE);
                        mRegisterBt.setVisibility(View.VISIBLE);
                        mLogoutBt.setVisibility(View.INVISIBLE);
                        mUserNameTv.setText("尚未登录");
                            }
                        });
                break;
        }
    }

    /**
     * 点击返回键做了处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }


}
