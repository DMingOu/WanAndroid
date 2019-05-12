package com.example.odm.wanandroid.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.odm.wanandroid.R;

public class WebContentActivity extends AppCompatActivity {

    private WebView mContentWV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcontent);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_web);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_1);
        }
        mContentWV = (WebView) findViewById(R.id.wv_content);
        mContentWV.getSettings().setJavaScriptEnabled(true);
        mContentWV.setWebViewClient(new WebViewClient());
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        //由搜索界面点进来的网页，传进来的标题是带有html语言的
        actionBar.setTitle(Html.fromHtml(title));
        mContentWV.loadUrl(url);
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
