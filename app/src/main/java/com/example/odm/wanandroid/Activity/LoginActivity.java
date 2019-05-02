package com.example.odm.wanandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odm.wanandroid.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEt_username;
    private EditText mLoginEt_password;
    private Button mLoginBtn;
    private TextView mRegisterTv;


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
        style_mRegisterTv.setSpan(clickableSpan, 6, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置可点击文字的颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF4081"));
        style_mRegisterTv.setSpan(foregroundColorSpan,6,13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将Spannable配置给TextView
        mRegisterTv.setMovementMethod(LinkMovementMethod.getInstance());
        //给文本配置设定的内容
        mRegisterTv.setText(style_mRegisterTv);
    }

}
