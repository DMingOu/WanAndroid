package com.example.odm.wanandroid.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odm.wanandroid.Adapter.ArticleAdapter;
import com.example.odm.wanandroid.Db.ArticlebaseHelper;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.bean.Article;
import com.example.odm.wanandroid.bean.PageListData;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Search_ArticleActivity extends AppCompatActivity {

    private ImageView  mBackIv; //返回键图片控件
    private EditText  mSearchEt; //搜索内容编辑框
    private Context mContext;
    private List<Article> articleList = new ArrayList<>();
    private List<PageListData> pageListDataList = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager lineLayoutManager;
    private PageListData pageListData;
    private String articleJsondata  = "";
    private String resultdata = "";
    private ArticlebaseHelper dbHelper;
    private String keyword;//搜索关键词
    private PostUtil postUtil;//Post工具类
    final String SearchPath = "https://www.wanandroid.com/article/query/";

    Handler handler = new Handler(){//此函数是属于MainActivity.java所在线程的函数方法，所以可以直接调用MainActivity的 所有方法。
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {   //
                pageListData = new PageListData();
                System.out.println("resultdata为"+resultdata);
                JsonUtil.handleArtcileData(articleList, pageListData, resultdata);
                pageListDataList.add(pageListData);//页码里面有几个对象，就代表文章列表有几页
                resultdata = "";
            } else {
                Toast.makeText(Search_ArticleActivity.this, "请重新输入地址：", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);
        dbHelper = new ArticlebaseHelper(this,"Article.db",null,1);
        initViews();
        initArticleAdapter();
    }

    /**
     *初始化控件
     */
    protected  void initViews(){
        mBackIv = (ImageView)findViewById(R.id.iv_back);
        mSearchEt  = (EditText)findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_article_search);
        lineLayoutManager = new LinearLayoutManager(Search_ArticleActivity.this);
        lineLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(lineLayoutManager);
        postUtil = new PostUtil();
        //点击事件--返回上一个页面
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //监听软键盘的输入
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    hideKeyboard(mSearchEt);
                    // 在这里写搜索的操作,一般都是网络请求数据
                    keyword  = mSearchEt.getText().toString();
                    new ArticleList_SearchTask().execute(SearchPath);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 初始化文章列表,但未装填数据
     */
    protected void initArticleAdapter(){
        articleAdapter = new ArticleAdapter(articleList);
        //设置ArticleAdapter的每个子项的点击事件--往数据增加已读文章的标题，跳转到对应网页
        articleAdapter.setRecyclerViewOnItemClickListener(new ArticleAdapter.ArticleRecyclerViewOnItemClickListener() {
            @Override
            public void onArticleItemClick(View view, int position) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();    //创建存放数据的ContentValues对象
                values.put("title",articleList.get(position).getTitle());
                db.insert("Article",null,values); //数据库执行插入命令
                Intent intent = new Intent(Search_ArticleActivity.this,WebContentActivity.class);
                intent.putExtra("url",articleList.get(position).getLink());
                intent.putExtra("title",articleList.get(position).getTitle());
                startActivity(intent);
            }
        });
        //设置ArticleAdapter的每个子项的长按点击事件--跳转到网页
        articleAdapter.setOnItemLongClickListener(new ArticleAdapter.ArticleRecyclerViewOnItemLongClickListener(){
            @Override
            public boolean onArticleItemLongClick (View view, int position) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();    //创建存放数据的ContentValues对象
                values.put("title",articleList.get(position).getTitle());
                db.insert("Article",null,values); //数据库执行插入命令
                Intent intent = new Intent(Search_ArticleActivity.this,WebContentActivity.class);
                intent.putExtra("title",articleList.get(position).getTitle());
                intent.putExtra("url",articleList.get(position).getLink());
                startActivity(intent);
                return true;
            }
        } );
    }

    public class ArticleList_SearchTask extends AsyncTask<String,Integer,List<Article> > {


        /**
         * @param params  启动任务执行的输入参数 params[0]
         * @return  后台计算结果,返回給onPostExecute方法
         */
        @Override
        protected List<Article> doInBackground(String... params) {
            articleList.clear();
            try {

                for (int i = 0; i < 1; i++) {
                    //初始化文章列表里面的数据
                    //publishProgress(i);
                    String  keywordString= "k="+URLEncoder.encode(keyword,"UTF-8");
                    resultdata = postUtil.sendPost(params[0]+i+"/json",keywordString);
                    pageListData = new PageListData();
                    System.out.println("resultdata为"+resultdata);
                    JsonUtil.handleArtcileData(articleList, pageListData, resultdata);
                    pageListDataList.add(pageListData);//页码里面有几个对象，就代表文章列表有几页
                    resultdata = "";

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return articleList;
        }


        /**
         * 为Recycler的Adapter装填数据
         * @param articleList  文章列表数据，doInbackground方法返回的结果
         */
        @Override
        protected void onPostExecute(List<Article> articleList) {
            System.out.println("正在执行onPostExecute方法");
            super.onPostExecute(articleList);
            if (articleList.size() == 0) {
                Toast.makeText(Search_ArticleActivity.this, "抱歉没有这方面的内容", Toast.LENGTH_SHORT).show();
            } else {
                mRecyclerView.setAdapter(articleAdapter);
                articleAdapter.notifyDataSetChanged();//刷新Adapter数据
            }
        }
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
