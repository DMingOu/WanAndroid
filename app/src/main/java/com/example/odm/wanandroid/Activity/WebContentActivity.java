package com.example.odm.wanandroid.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.odm.wanandroid.R;

public class WebContentActivity extends AppCompatActivity {

    private WebView mContentWV;
    private ProgressBar mLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcontent);
        initViews();
        String url = getIntent().getStringExtra("url");
        mContentWV.loadUrl(url);

    }

    protected  void initViews(){
        mLoadingPb = (ProgressBar)findViewById(R.id.pb_web_loading);
        mLoadingPb.setVisibility(View.VISIBLE);
        mContentWV = (WebView) findViewById(R.id.wv_content);
        mContentWV.getSettings().setJavaScriptEnabled(true);
        mContentWV.setWebViewClient(new WebViewClient());
//        mContentWV.setWebChromeClient(new WebChromeClient(){
//            @Override
//            //重写WebChromeClient监听网页加载的进度,从而实现进度条
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                if(newProgress==100){
//                    mLoadingPb.setVisibility(View.GONE);//加载完网页进度条消失
//                } else{
//                    mLoadingPb.setProgress(newProgress);//设置进度值
//                    mLoadingPb.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
//                }
//            }
//        });
        mContentWV.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //显示进度条
                mLoadingPb.setProgress(newProgress);
                if (newProgress == 100) {
                    //加载完毕隐藏进度条
                    mLoadingPb.setVisibility(View.GONE);
                    mContentWV.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_web);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_toolbar_32);
            String title = getIntent().getStringExtra("title");
            //由搜索界面点进来的网页，传进来的标题是带有html语言的
            actionBar.setTitle(Html.fromHtml(title));
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








