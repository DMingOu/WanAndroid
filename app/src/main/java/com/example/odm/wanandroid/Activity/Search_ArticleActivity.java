package com.example.odm.wanandroid.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odm.wanandroid.adapter.ArticleAdapter;
import com.example.odm.wanandroid.db.ArticlebaseHelper;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.base.RecyclerViewNoBugLinearLayoutManager;
import com.example.odm.wanandroid.util.JsonUtil;
import com.example.odm.wanandroid.util.PostUtil;
import com.example.odm.wanandroid.base.BaseUrl;
import com.example.odm.wanandroid.bean.Article;
import com.example.odm.wanandroid.bean.PageListData;
import com.example.odm.wanandroid.receiver.InterRecevier;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Search_ArticleActivity extends AppCompatActivity {
    private EditText  mSearchEt; //搜索内容编辑框
    private Context mContext;
    private List<Article> articleList = new ArrayList<>();
    private List<PageListData> pageListDataList = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView mRecyclerView;
    private PageListData pageListData;
    private String resultdata = "";
    private ArticlebaseHelper dbHelper;
    private String keyword;//搜索关键词
    private PostUtil postUtil;//Post工具类
    private BroadcastReceiver receiver = new InterRecevier();
    private boolean refreshing = false;
    private boolean mIsRefreshing=false;
    private boolean loading  = false;
    private  static boolean isHasMore = false;//是否还有下一页能加载
    private int load_times = 0; //加载的次数，被用于发送文章请求，控制页码
    final  int ARTICLECOUNT_ONEPAGE = 20;

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 0x02) {   //
                //此处写handler处理操作
            } else {
                //Toast.makeText(Search_ArticleActivity.this, "请重新输入地址：", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        initBoardcaster();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);//注销注册的广播
        handler.removeCallbacksAndMessages(null);//断掉与Handler 的联系，销毁Handler 消息的处理,防止handler导致内存泄漏
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *初始化控件
     */
    protected  void initViews(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_search);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_toolbar_32);
            actionBar.setTitle("搜索");
        }

        mSearchEt  = (EditText)findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_article_search);
        final LinearLayoutManager linearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(Search_ArticleActivity.this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        postUtil = new PostUtil();
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //监听用户是否有上滑加载的操作
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int endCompletelyPosition = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if(!loading && totalItemCount < (endCompletelyPosition + 2 ) ) {
                    if(isHasMore) {
                        loading = true;
                        refreshing = false;
                        new Search_ArticleActivity.ArticleList_SearchTask().execute(BaseUrl.getSearchPath());
                 }
                }
            }
        });
        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return mIsRefreshing;
                    }
                }
        );

        //监听软键盘的输入
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    hideKeyboard(mSearchEt);
                    keyword  = mSearchEt.getText().toString();
                    load_times = 0; //每次点击搜索键重置加载次数为0，展示第一页搜索的数据
                    articleList.clear();
                    loading = true;
                    refreshing = false;
                    // 搜索的操作--网络请求数据
                    new ArticleList_SearchTask().execute(BaseUrl.getSearchPath());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 初始化文章列表
     */
    protected void initArticleAdapter(){
        articleAdapter = new ArticleAdapter(articleList);
        articleAdapter.setSearching(true);
        //设置ArticleAdapter的每个子项的点击事件--往数据增加已读文章的标题，跳转到对应网页
        articleAdapter.setRecyclerViewOnItemClickListener(new ArticleAdapter.ArticleRecyclerViewOnItemClickListener() {
            @Override
            public void onArticleItemClick(View view, int position) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();    //创建存放数据的ContentValues对象
                values.put("title",articleList.get(position).getTitle());
                db.insert("Article",null,values); //数据库执行插入命令
                db.close();//关闭数据库
                Intent intent = new Intent(Search_ArticleActivity.this,WebContentActivity.class);
                intent.putExtra("url",articleList.get(position).getLink());
                intent.putExtra("title",articleList.get(position).getTitle());
                startActivity(intent);
            }
        });
        //设置ArticleAdapter的每个子项的长按点击事件--跳转到网页
//        articleAdapter.setOnItemLongClickListener(new ArticleAdapter.ArticleRecyclerViewOnItemLongClickListener(){
//            @Override
//            public boolean onArticleItemLongClick (View view, int position) {
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();    //创建存放数据的ContentValues对象
//                values.put("title",articleList.get(position).getTitle());
//                db.insert("Article",null,values); //数据库执行插入命令
//                db.close();//关闭数据库
//                Intent intent = new Intent(Search_ArticleActivity.this,WebContentActivity.class);
//                Intent intent = new Intent(Search_ArticleActivity.this,WebContentActivity.class);
//                intent.putExtra("title",articleList.get(position).getTitle());
//                intent.putExtra("url",articleList.get(position).getLink());
//                startActivity(intent);
//                return true;
//            }
//        } );
    }


    /**
     * AsyncTask--发送请求，处理JSON数据，显示文章
     */
    public class ArticleList_SearchTask extends AsyncTask<String,Integer,String > {

        private  List<Article> mArticleList = new ArrayList<>();

        /**
         * @param params  启动任务执行的输入参数 params[0]
         * @return  后台计算结果,返回給onPostExecute方法
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                for (int i = 0; i < 1; i++) {
                    //初始化文章列表里面的数据
                    if (loading && isHasMore) {
                        i = load_times + i;//加载更多时，令i保持在最新要出来的一页
                    }
                    String keywordString = "k=" + URLEncoder.encode(keyword, "UTF-8");
                    resultdata = postUtil.sendPost(params[0] + i + "/json", keywordString);
            }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return resultdata;
        }


        /**
         * 为RecycleView的Adapter装填数据
         * @param resultdata  文章列表数据，doInbackground方法返回的结果
         */
        @Override
        protected void onPostExecute(String resultdata) {
            super.onPostExecute(resultdata);
            mRecyclerView.setAdapter(articleAdapter);
            pageListData = new PageListData();
            JsonUtil.handleArtcileData(mArticleList, pageListData, resultdata,refreshing);
            pageListDataList.add(pageListData);//页码里面有几个对象，就代表文章列表有几页
            articleList.addAll(mArticleList);
            articleAdapter.notifyDataSetChanged();//刷新Adapter数据
            if (mArticleList.size() == 0) {
                Toast.makeText(Search_ArticleActivity.this, "抱歉没有这方面的内容", Toast.LENGTH_LONG).show();
            }else {
                //如果加载出来的列表小于正常一页的数量20，说明已经加载完毕了
                if (mArticleList.size() <  ARTICLECOUNT_ONEPAGE && mArticleList.size() > 0){
                    isHasMore = false;
                    load_times = 0; //重置了加载次数，让用户搜索关键词可以从第一页开始
                    Log.e("ArticleAmount","文章总数" + articleAdapter.getItemCount());
                } else {
                    isHasMore = true; //加载这一页有20篇文章，说明可能还会有下一页
                }
                if(! loading && refreshing ) {
                    refreshing = false;
                    loading = false;
                }else{
                    //上拉加载后
                    load_times++;//加载次数+1
                    refreshing = false;
                    loading = false;
                    //上拉加载后，定位到加载出来的位置附近。每一页都有20篇文章
                    if(articleAdapter.getItemCount() >= 20) {
                        mRecyclerView.scrollToPosition(articleAdapter.getItemCount() - ARTICLECOUNT_ONEPAGE );
                    } else {
                        //若首页未满，则返回到顶部
                        mRecyclerView.scrollToPosition(0);
                    }
//                    if(load_times == 0 && ! isHasMore){
//                        mRecyclerView.scrollToPosition(0);
//                    }
                }
            }
        }
    }

    /**
     * 获取"搜索界面是否还有下一页"状态
     * @return isHasMore
     */
    public static boolean getStatus_isHasMore(){
        return isHasMore;
    }

    /**
     * 设置"搜索界面是否有下一页"的属性
     * @param bool 是否下页属性
     */
    public static void setStatus_isHasMore( boolean bool) {
        isHasMore = bool;
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

    protected void initBoardcaster(){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION); //动态注册广播
        this.registerReceiver(receiver,filter);//注册广播
    }

}
