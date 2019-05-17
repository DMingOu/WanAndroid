package com.example.odm.wanandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.util.JsonUtil;
import com.example.odm.wanandroid.util.PostUtil;
import com.example.odm.wanandroid.base.BaseUrl;
import com.example.odm.wanandroid.bean.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private EditText mRgtEt_username;
    private EditText mRgtEt_password;
    private EditText mRgtEt_password_confirm;
    private Button mRgtBtn;
    private TextView mLoginTv;
    private User RgtUser;
    private JsonUtil jsonUtil;
    private PostUtil postUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();

    }


    /*
        *初始化控件
        */
    protected void initViews(){
        RgtUser = new User();//实例化User对象
        //实例化工具类对象
        jsonUtil = new JsonUtil();
        postUtil = new PostUtil();
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_register);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_toolbar_32);
            actionBar.setTitle("注册");
        }

        mRgtEt_username = (EditText)findViewById(R.id.et_register_username);
        mRgtEt_password = (EditText)findViewById(R.id.et_register_password);
        mRgtEt_password_confirm = (EditText)findViewById(R.id.et_register_confirm);
        mRgtBtn = (Button)findViewById(R.id.bt_register);
        mLoginTv = (TextView) findViewById(R.id.tv_login);
        final SpannableStringBuilder style_mLoginTv = new SpannableStringBuilder();//内容和标记都可以更改的文本类实例，意在让文字可点击
        style_mLoginTv.append("已有账号？返回登录页面"); //设置文字
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Toast.makeText(RegisterActivity.this, "触发点击跳转登录事件!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        };
        style_mLoginTv.setSpan(clickableSpan, 5, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置可点击文字的颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF4081"));//red
        style_mLoginTv.setSpan(foregroundColorSpan,5,11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将Spannable配置给TextView
        mLoginTv.setMovementMethod(LinkMovementMethod.getInstance());
        //给文本配置设定的内容
        mLoginTv.setText(style_mLoginTv);

    }

    /**
     * 说明：用户点击注册键后，符合条件就发送POST请求申请注册
     * @param v 控件
     */
    public void register(View v){
        //获取点击控件的ID，并根据ID判断怎样处理
        //Toast.makeText(this,"注册按钮被点击",Toast.LENGTH_SHORT).show();
        int id = v.getId();
        switch (id) {
            case R.id.bt_register:
                hideKeyboard(mRgtEt_password_confirm);//点击注册键隐藏键盘，让用户可以更清楚看到提醒
                //获取用户名
                final String username = mRgtEt_username.getText().toString();
                //获取用户密码和确认密码
                final String userpwd = mRgtEt_password.getText().toString();
                final String userpwd_confirm = mRgtEt_password_confirm.getText().toString();
                Log.e("username",username);
                Log.e("userpwd",userpwd);
                Log.e("reuserpwd",userpwd_confirm);
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(userpwd) || TextUtils.isEmpty(userpwd_confirm)){
                    Toast.makeText(this,"用户名或者密码不能为空",Toast.LENGTH_LONG).show();
                    //让空框获得焦点,让用户可以输入
                    if(!TextUtils.isEmpty(username)){
                        mRgtEt_username.requestFocus();
                    }else if(! TextUtils.isEmpty(userpwd)){
                        mRgtEt_password.requestFocus();
                    }else if(TextUtils.isEmpty(userpwd_confirm)){
                        mRgtEt_password_confirm.requestFocus();
                    }
                } else if(! userpwd.contentEquals(userpwd_confirm)) {  //若两个密码框的内容不同
                    Toast.makeText(this,"密码与再次确定密码不一致！",Toast.LENGTH_LONG).show();
                    //让用户修改确认密码部分
                    mRgtEt_password_confirm.requestFocus();
                } else {
                    //开启子线程，执行注册操作
                     new Thread() {
                         public void run(){
                             final String data;
                             try {
                                 //"?username="是错误的，会导致网络返回码400，出现服务器无法理解的语法，虽然在Postman上模拟的完整接口有?，但是Post请求真正发送的并不是自己简单拼接起来的
                                 data = "username="+ URLEncoder.encode(username,"UTF-8")+"&password="+URLEncoder.encode(userpwd,"UTF-8")
                                         +"&repassword="+URLEncoder.encode(userpwd_confirm,"UTF-8");
                                 jsonUtil.handleUserdata(RgtUser,postUtil.sendPost(BaseUrl.getRgtPath(),data));
                                 checkRegister();
                             } catch (UnsupportedEncodingException e) {
                                 e.printStackTrace();
                             }
                         }
                     }.start();
                }
                break;
        }

    }

    /**
     * 判定注册的结果，并提醒用户
     */
    public void  checkRegister(){
        if(RgtUser.getErrorCode() == 0 ) {
            Looper.prepare();
            Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Looper.prepare();
            Toast.makeText(RegisterActivity.this, "注册失败！原因："+RgtUser.getErrorMsg(), Toast.LENGTH_SHORT).show();
            Looper.loop();
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

    /**
     * 隐藏软键盘
     * @param view ：一般是EditText使用
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
