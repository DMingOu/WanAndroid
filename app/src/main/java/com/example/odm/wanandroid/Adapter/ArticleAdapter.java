package com.example.odm.wanandroid.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.odm.wanandroid.Activity.Search_ArticleActivity;
import com.example.odm.wanandroid.Db.ArticlebaseHelper;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.bean.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * 为显示文章的ArticleRecycleView准备的适配器类
 * Created by ODM on 2019/5/4.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Article> mArticleList;
    private Context mContext;

    private ArticleRecyclerViewOnItemClickListener onArticleItemClickListener;
    private ArticleRecyclerViewOnItemLongClickListener onArticleItemLongClickListener;

    public ArticleAdapter(List<Article> ArticleList) {
        mArticleList = ArticleList;
    }

    private ArticlebaseHelper  dbhelper;
    private List<Article> articleList = new ArrayList<>();

    public static final int ITEM_TYPE_FOOTER = 0; //是否到达底部的状态量

    public  static  class ItemArticleViewHolder extends RecyclerView.ViewHolder{
        private CardView mItemArcticleCV;
        private TextView mSuperChapterNameTv;
        private TextView mAuthorTv;
        private TextView mTimeTv;
        private TextView mTitleTv;

        //实例化CardView里面的子控件
        public ItemArticleViewHolder(View itemView){
            super(itemView);
            mItemArcticleCV = (CardView)itemView;
            mAuthorTv = (TextView)itemView.findViewById(R.id.tv_author);
            mSuperChapterNameTv = (TextView)itemView.findViewById(R.id.tv_superChapterName);
            mTimeTv = (TextView)itemView.findViewById(R.id.tv_time);
            mTitleTv = (TextView)itemView.findViewById(R.id.tv_title);
        }

    }

    /**
     * 底部加载布局Holder--加载更多
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mLoadPb;
        private TextView mLoadTv;

        public FooterViewHolder(View view){
            super(view);
            mLoadPb = (ProgressBar) view.findViewById(R.id.pb_loading);
            mLoadTv = (TextView)view.findViewById(R.id.tv_loading);
        }
    }

    /**
     * 底部加载布局Holder--没有更多
     */
    public class FooterViewHolder_NoMore extends RecyclerView.ViewHolder {
        private TextView mLoadTv_nomore;
        private  FooterViewHolder_NoMore(View view) {
            super(view);
            mLoadTv_nomore = (TextView) view.findViewById(R.id.tv_loading_nomore);
        }
    }

    //将文章item的布局加载进ViewHolder中
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        //如果达到最后一个item就加载 "加载更多" 这个view
        if(viewType == ITEM_TYPE_FOOTER) {
            //如果没有更多了，就加载 "没有更多" 这个view
            if(! Search_ArticleActivity.getStatus_isHasMore()){
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer_loading_nomore,parent,false);
                return new FooterViewHolder_NoMore(view);
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer_loadingmore,parent,false);
            return new FooterViewHolder(view);
        } else {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent ,false);
        dbhelper =  new ArticlebaseHelper(mContext,"Article.db",null,1);
        return new ItemArticleViewHolder(view);
     }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){
        //传进来的是底部加载的holder
        if(holder instanceof  FooterViewHolder){
            ((FooterViewHolder) holder).mLoadTv.setText("正在加载中");
            return;
        }
        if(holder instanceof  FooterViewHolder_NoMore) {
            ((FooterViewHolder_NoMore) holder).mLoadTv_nomore.setText("没有更多数据了");
            return;
        }
        if (holder instanceof ItemArticleViewHolder) {
            ItemArticleViewHolder newHolder = (ItemArticleViewHolder) holder;
            final Article article = mArticleList.get(position);
            newHolder.mTitleTv.setText(Html.fromHtml(article.getTitle()));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            //查询Article表对象，创建游标对象
            Cursor cursor = db.query("Article", new String[]{"title"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    if (article.getTitle().equals(title)) { //数据库有相同的标题（读过的)，设置为灰色，否则设置为黑色
                        newHolder.mTitleTv.setTextColor(Color.parseColor("#999999"));
                        break;
                    } else {
                        newHolder.mTitleTv.setTextColor(Color.parseColor("#000000"));
                    }
                } while (cursor.moveToNext());
            }
            ContentValues values = new ContentValues();    //创建存放数据的ContentValues对象
            values.put("id",mArticleList.get(position).getId()); //存储RecycleView即将显示出来的文章的id
            db.insert("Article",null,values); //数据库执行插入命令
            cursor.close();
            db.close();
            newHolder.mTimeTv.setText(article.getNiceDate());
            //某些文章的类别为空，我设置属于它为个人开发者
            if (article.getSuperChapterName().equals("")) {
                article.setSuperChapterName("个人开发者");
            }
            newHolder.mSuperChapterNameTv.setText(article.getSuperChapterName());
            newHolder.mAuthorTv.setText("作者:" + article.getAuthor());
            //设置Tag方便进行点击事件数据的处理
            newHolder.mItemArcticleCV.setTag(position);

            //若文章已有被点击属性，则被设为灰色已读
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int position = holder.getLayoutPosition();
                    article.setClicked(true);
                    if (holder instanceof ItemArticleViewHolder) {
                        ItemArticleViewHolder newHolder = (ItemArticleViewHolder) holder;
                        newHolder.mTitleTv.setTextColor(Color.parseColor("#999999"));//灰色
                    }
                    if (onArticleItemClickListener != null) {
                        //注意这里使用getTag方法获取数据
                        onArticleItemClickListener.onArticleItemClick(view, (Integer) view.getTag());
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    article.setClicked(true);
                    if (holder instanceof ItemArticleViewHolder) {
                        ItemArticleViewHolder newHolder = (ItemArticleViewHolder) holder;
                        newHolder.mTitleTv.setTextColor(Color.parseColor("#999999"));
                    }
                    return onArticleItemLongClickListener != null && onArticleItemLongClickListener.onArticleItemLongClick(v, (Integer) v.getTag());
                }
            });
        }
    }

    /**
     * 刷新adpter的数据，防止数据源与内部数据大小冲突
     * @param articleList_new
     */
    public void notifyData(List<Article> articleList_new) {
        if (articleList_new != null) {
            int previousSize = articleList.size();
            articleList.clear();
            notifyItemRangeRemoved(0, previousSize);
            articleList.addAll(articleList_new);
            notifyItemRangeInserted(0, articleList_new.size());
        }
    }

    /**
     * 上滑到最后的item时返回0，否则返回1
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
       if (position == getItemCount()-1 ){
            return ITEM_TYPE_FOOTER;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size() ;
    }

    /**
     * 删除RecycleView某项item
     * @param position
     */
    public void removeItem(int position){
        mArticleList.remove(position);//删除数据源,移除集合中当前下标的数据
        notifyItemRemoved(position);//刷新被删除的地方
        notifyItemRangeChanged(position,getItemCount()); //刷新被删除数据，以及其后面的数据
    }

    /**
     * 删除RecycleView从第一项到最后一项
     */
    public void removeAllItem () {
        notifyItemRangeRemoved(0,getItemCount());
        articleList.clear();
        notifyItemRangeChanged(0, articleList.size());
    }

    /*设置点击事件监视*/
    public void setRecyclerViewOnItemClickListener(ArticleRecyclerViewOnItemClickListener onArticleItemClickListener) {
        this.onArticleItemClickListener = onArticleItemClickListener;
    }

    /*设置长按事件监视*/
    public void setOnItemLongClickListener(ArticleRecyclerViewOnItemLongClickListener onArticleItemLongClickListener) {
        this.onArticleItemLongClickListener = onArticleItemLongClickListener;
    }

    /**
     * 接口：子项点击事件接口
     */
    public interface ArticleRecyclerViewOnItemClickListener {

        void onArticleItemClick(View view, int position);

    }


    /**
     * 接口：子项长按点击事件
     */
    public interface ArticleRecyclerViewOnItemLongClickListener {

        boolean onArticleItemLongClick(View view, int position);

    }


}
