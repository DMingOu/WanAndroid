package com.example.odm.wanandroid.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.odm.wanandroid.Adapter.ArticleAdapter;
import com.example.odm.wanandroid.Application.MyApplication;
import com.example.odm.wanandroid.Db.ArticlebaseHelper;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.RecyclerViewNoBugLinearLayoutManager;
import com.example.odm.wanandroid.Util.GetUtil;
import com.example.odm.wanandroid.Util.JsonUtil;
import com.example.odm.wanandroid.Util.PostUtil;
import com.example.odm.wanandroid.Util.SharedPreferencesUtil;
import com.example.odm.wanandroid.base.BaseUrl;
import com.example.odm.wanandroid.bean.Article;
import com.example.odm.wanandroid.bean.PageListData;
import com.example.odm.wanandroid.bean.User;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private User user;
    private List<Article> articleList = new ArrayList<>();
    private List<PageListData> pageListDataList = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView mRecyclerView;
    private PageListData pageListData;
    private String articleJsondata  = "";
    private String resultdata = "";
    private ArticlebaseHelper dbHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing=false;
    private static boolean isLogin = false;
    private int load_times = 0; //加载的次数，被用于发送文章请求，控制页码
    private boolean isRefresh = false;
    private boolean isloading = false;
    private static boolean  isHasMore_AtricleList = true;
    private final  int ARTICLECOUNT_ONEPAGE = 20; //从网页请求的数据，以页为单位，一页的文章的数量为20
    //处理返回结果的函数，系统提供的类方法
   Handler handler = new Handler(){//此函数是属于MainActivity.java所在线程的函数方法，所以可以直接调用MainActivity的 所有方法。
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                //System.out.println("resultdata为"+resultdata);
            } else {
                Toast.makeText(MainActivity.this, "请重新输入地址：", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ariticle);
        dbHelper = new ArticlebaseHelper(this,"Article.db",null,1);

        initViews();
        initArticleAdapter();
        isloading = true;
        //若首次启动就加载刷新动画，使用SwipeRefreshLayout的post方法
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                load_times = 0;
                mSwipeRefreshLayout.setRefreshing(true);
                new ArticleListTask().execute(BaseUrl.getArticleListPath());
            }
        });
        checkStatus(); //检查登录状态
        closeAndroidPDialog();//关闭警告弹窗
    }

    @Override
    protected void onStart() {
        super.onRestart();
        Search_ArticleActivity.setStatus_isHasMore(true); //为了在主界面不受搜索界面的是否还能加载影响
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
                db.close();
                Intent intent = new Intent(MainActivity.this,WebContentActivity.class);
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
                db.close();
                Intent intent = new Intent(MainActivity.this,WebContentActivity.class);
                intent.putExtra("title",articleList.get(position).getTitle());
                intent.putExtra("url",articleList.get(position).getLink());
                startActivity(intent);
                return true;
            }
        } );
    }
    protected void initViews(){
        Toolbar toolbar_main = (Toolbar) findViewById(R.id.tool_bar_main);
        setSupportActionBar(toolbar_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.user_32);
        }
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_item_article);

        //外部数据集和内部数据集不统一时会出现报错
        final LinearLayoutManager linearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //监听用户是否有上滑加载的操作
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int endCompletelyPosition = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if(!isloading && totalItemCount < (endCompletelyPosition + 2 ) ) {
                    isloading = true;
                    isRefresh = false;
                    new ArticleListTask().execute(BaseUrl.getArticleListPath());
                }
            }
        });
        //mRecyclerView.setItemViewCacheSize(500);//设置RecyclerView的缓存数量, 大了就不复用ViewHolder，直接全部新建，布局建多了卡是迟早的事
        //当刷新时设置,mIsRefreshing=true;刷新完毕后还原为false//mIsRefreshing=false;
        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mIsRefreshing) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
        //下滑刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_main); //下拉刷新布局
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#009a61"),Color.parseColor("#FF0000"));// green ,red
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_times = 0;
                //下滑刷新，新开一个Task对象--重新请求网络数据
                isloading = false;
                isRefresh = true;
                new ArticleListTask().execute(BaseUrl.getArticleListPath());
            }
        });
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
                            new JsonUtil().handleUserdata(user, new PostUtil().sendPost(BaseUrl.getLoginPath(), data));
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
        }
    }


    /**
     * AsyncTask--发送请求，处理JSON数据，显示文章
     */
    public class ArticleListTask extends AsyncTask<String,Integer,String> {

        private  List<Article> mArticleList = new ArrayList<>();

        /**
         * 可以设置进度条对话框，缓解在一次性处理数据过多时屏幕白屏的尴尬
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        /**
         * @param params  启动任务执行的输入参数 params[0]
         * @return  后台计算结果,返回給onPostExecute方法
         */
        @Override
        protected String doInBackground(String... params) {
            //mArticleList.clear();
            for (int i = 0; i < 1; i++) {
                //初始化文章列表里面的数据
                //publishProgress(i);
               if(isloading ) {
                   i = load_times + i;//加载更多时，令i保持在最新要出来的一页
               }
                resultdata =  GetUtil.sendGet(params[0] + i + "/json", articleJsondata);
            }
            return resultdata;
         }


        /**
         * 为Recycler的Adapter装填数据
         * @param resultdata  关于文章列表的JSON数据，doInbackground方法返回的结果
         */
        @Override
        protected void onPostExecute(String resultdata) {
            String resultJsondata = resultdata;
            super.onPostExecute(resultJsondata);
            mRecyclerView.setAdapter(articleAdapter);
            pageListData = new PageListData();
            JsonUtil.handleArtcileData(mArticleList, pageListData, resultJsondata,isRefresh);
            articleAdapter.notifyData(mArticleList);
            pageListDataList.add(pageListData);//页码里面有几个对象，就代表文章列表有几页
            resultJsondata = "";
            //articleList，在外面的一些方法会调用到它
            articleList.addAll(mArticleList);
            //刷新状态下，如果文章数组小于固定数量20说明已经出现过的文章被过滤掉，List里面这部分都是新的文章，需要从列表末尾调回顶部
            if(mArticleList.size() < ARTICLECOUNT_ONEPAGE && mArticleList.size() > 0  && isRefresh) {
                for(int i = mArticleList.size() ; i >= 1; i-- )
                articleAdapter.notifyItemMoved(articleAdapter.getItemCount(),0);
                articleAdapter.notifyItemRangeChanged(0,articleAdapter.getItemCount()); //刷新位置，防止数据的位置紊乱
            }
            //加载状态下，文章数组数量小于固定数量20，说明文章已经显示完了
            if(mArticleList.size() < ARTICLECOUNT_ONEPAGE && mArticleList.size() > 0  && isloading){
                    MainActivity.setIsHasMore_AtricleList(false);
            }
            mSwipeRefreshLayout.setRefreshing(false);//停止刷新动画
            if(! isloading && isRefresh ) {
                //下滑刷新后定位回第一篇文章
                mRecyclerView.scrollToPosition(0);
                isRefresh = false;
                isloading = false;
            }else{
                //上拉加载后
                load_times++;//加载次数+1
                isRefresh = false;
                isloading = false;
                //上拉加载后，定位到加载出来的位置附近。每一页都有20篇文章
                mRecyclerView.scrollToPosition(articleAdapter.getItemCount() - ARTICLECOUNT_ONEPAGE);
            }
        }
    }




    /**
     * 给标题栏加载menu菜单布局
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_main,menu);
        return  true;
    }


    /**
     * 处理标题栏各个按钮的点击事件
     * @param item
     * @return
     */
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 16908332://左上角按钮的实际id，但是用R.id.home 会找不到
                Intent intent_user = new Intent();
                intent_user.setClass(MainActivity.this,UserActivity.class);
                startActivity(intent_user);
                //Toast.makeText(this,"即将打开用户页面",Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_toolbar_search:
                Intent intent_search = new Intent();
                intent_search.setClass(MainActivity.this,Search_ArticleActivity.class);
                startActivity(intent_search);
                break;

        }
        return true;
    }


    public static boolean isHasMore_AtricleList() {
        return isHasMore_AtricleList;
    }

    public static void setIsHasMore_AtricleList(boolean isHasMore_AtricleList) {
        MainActivity.isHasMore_AtricleList = isHasMore_AtricleList;
    }

    /**
     * 网上的方法--针对安卓P，关闭警告调用了非官方接口的弹窗
     */
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


