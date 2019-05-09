package com.example.odm.wanandroid.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.odm.wanandroid.Db.ArticlebaseHelper;
import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.bean.Article;

import java.util.List;

/**
 * 为显示文章的ArticleRecycleView准备的适配器类
 * Created by ODM on 2019/5/4.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ItemArticleViewHolder>  {

    private List<Article> mArticleList;
    private Context mContext;

    private ArticleRecyclerViewOnItemClickListener onArticleItemClickListener;
    private ArticleRecyclerViewOnItemLongClickListener onArticleItemLongClickListener;

    public ArticleAdapter(List<Article> ArticleList) {
        mArticleList = ArticleList;
    }

    private ArticlebaseHelper  dbhelper;

    public  static  class ItemArticleViewHolder extends RecyclerView.ViewHolder{
        private CardView  mItemArcticleCV;
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

    //将文章item的布局加载进ViewHolder中
    @Override
    public ItemArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent ,false);
        dbhelper =  new ArticlebaseHelper(mContext,"Article.db",null,1);
        return new ItemArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemArticleViewHolder holder, int position) {
        final Article article = mArticleList.get(position);
        holder.mTitleTv.setText(article.getTitle());
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        //查询Article表对象，创建游标对象
        Cursor cursor = db.query("Article", new String[] {"title"}, null, null, null, null, null);
        //boolean isCilck = false; //设置文章是否被读，若数据库有相同标题，则文章已读
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                if (article.getTitle().equals(title)) {
                    Log.e("Articletitle",article.getTitle());
                    holder.mTitleTv.setTextColor(Color.parseColor("#999999"));
                    break;
                } else {
                    holder.mTitleTv.setTextColor(Color.parseColor("#000000"));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        //若文章已有被点击属性，则被设为灰色已读
        //if (isCilck)  holder.mTitleTv.setTextColor(Color.parseColor("#999999"));
        holder.mTimeTv.setText(article.getNiceDate());
        holder.mSuperChapterNameTv.setText(article.getSuperChapterName());
        holder.mAuthorTv.setText("作者:" + article.getAuthor());
        //设置Tag方便进行点击事件数据的处理
        holder.mItemArcticleCV.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int position = holder.getLayoutPosition();
                article.setClicked(true);
                holder.mTitleTv.setTextColor(Color.parseColor("#999999"));//灰色
                if (onArticleItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                onArticleItemClickListener.onArticleItemClick(view, (Integer) view.getTag());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                article.setClicked(true);
                holder.mTitleTv.setTextColor(Color.parseColor("#999999"));//灰色
                return onArticleItemLongClickListener != null && onArticleItemLongClickListener.onArticleItemLongClick(v, (Integer) v.getTag());
             }
        });
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
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
