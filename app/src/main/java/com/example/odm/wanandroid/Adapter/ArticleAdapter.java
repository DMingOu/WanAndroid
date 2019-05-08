package com.example.odm.wanandroid.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        //item设置点击事件
        //view.setOnClickListener(this);
        //view.setOnLongClickListener(this);
        return new ItemArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemArticleViewHolder holder, int position) {
        final Article article = mArticleList.get(position);
        holder.mTitleTv.setText(article.getTitle());
        //若文章已有被点击属性，则被设为灰色已读
        if(article.isClicked()) holder.mTitleTv.setTextColor(Color.parseColor("#999999"));
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


//    @Override
//    public boolean onLongClick(View v) {
//        return onArticleItemLongClickListener != null && onArticleItemLongClickListener.onArticleItemLongClick(v, (Integer) v.getTag());
//    }

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
