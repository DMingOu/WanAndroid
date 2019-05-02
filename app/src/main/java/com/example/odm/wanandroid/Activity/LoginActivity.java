package com.example.odm.wanandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.bean.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEt_username;
    private EditText mLoginEt_password;
    private Button mLoginBtn;
    private TextView mRegisterTv;
    private JsonUtil jsonUtil;
    private PostUtil postUtil;
    private User LoginUser;
    final String LoginPath = "https://www.wanandroid.com/user/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

    }

    /*
    *初始化控件
    */
    protected void initViews(){
        //实例化工具类对象
        jsonUtil = new JsonUtil();
        postUtil = new PostUtil();
        LoginUser = new User();
        mLoginEt_username = (EditText)findViewById(R.id.et_login_username);
        mLoginEt_password  = (EditText) findViewById(R.id.et_login_password);
        mLoginBtn = (Button)findViewById(R.id.bt_login);
        mRegisterTv = (TextView)findViewById(R.id.tv_register);
        final SpannableStringBuilder style_mRegisterTv = new SpannableStringBuilder();//内容和标记都可以更改的文本类实例，意在让文字可点击
        style_mRegisterTv.append("还没有账号？快来注册一个吧！"); //设置文字
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(LoginActivity.this, "触发点击跳转注册事件!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        };
        style_mRegisterTv.setSpan(clickableSpan, 6, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置可点击文字的颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF4081"));
        style_mRegisterTv.setSpan(foregroundColorSpan,6,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将Spannable配置给TextView
        mRegisterTv.setMovementMethod(LinkMovementMethod.getInstance());
        //给文本配置设定的内容
        mRegisterTv.setText(style_mRegisterTv);
    }


    /**
     *  登录按钮的点击事件，发送登录的POST请求
     * @param 控件V
     */
    public void login(View v){
        Toast.makeText(LoginActivity.this,"登录按钮被点击",Toast.LENGTH_SHORT).show();
        int id = v.getId();
        switch (id) {
            case R.id.bt_login:
                final String userName = mLoginEt_username.getText().toString();
                final String userPwd = mLoginEt_password.getText().toString();
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                    if(TextUtils.isEmpty(userName)){
                        mLoginEt_username.requestFocus();
                    }
                    if(TextUtils.isEmpty(userPwd)) {
                        mLoginEt_password.requestFocus();
                    }
                } else {
                    //开启子线程，执行注册操作
                    new Thread() {
                        public void run(){
                            final String data;
                            try {
                                //"?username="是错误的，会导致网络返回码400，出现服务器无法理解的语法，虽然在Postman上模拟的完整接口有?，但是Post请求真正发送的并不是自己简单拼接起来的
                                data = "username="+ URLEncoder.encode(userName,"UTF-8")+"&password="+URLEncoder.encode(userPwd,"UTF-8");
                                Log.e("finalPath",LoginPath+"?"+data);
                                jsonUtil.handleUserdata(LoginUser,postUtil.sendPost(LoginPath,data));
                                checkLogin();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
        }
    }
    public void checkLogin(){
        if(LoginUser.getErrorCode() == 0 ) {
            Looper.prepare();
            Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show();

            //登录成功后跳转到主界面
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            Looper.loop();
        } else {
            Looper.prepare();
            Toast.makeText(this, "登录失败！原因："+LoginUser.getErrorMsg(), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}
