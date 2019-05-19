package com.example.odm.wanandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.activity.WebContentActivity;
import com.example.odm.wanandroid.application.MyApplication;
import com.example.odm.wanandroid.base.WrapperImageView;
import com.example.odm.wanandroid.bean.Banner;

import java.util.List;

/**
 * Created by ODM on 2019/5/18.
 * BannerViewAdapter
 */


public class BannerViewAdapter extends BannerViewBaseAdapter {

    private List<Banner> mBeansList;//banner子项集合
    private Context mContext;


    public BannerViewAdapter(List<Banner> bannerBeans) {
        this.mBeansList = bannerBeans;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        WrapperImageView wrapperImageView;
        TextView title;

        if (mContext == null) {
            mContext = container.getContext();
        }
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_banner,null);

        final Banner bannerbean = mBeansList.get(position);
        title =(TextView) mView.findViewById(R.id.banner_title);
        title.setText(bannerbean.getTitle());
        wrapperImageView = (WrapperImageView)mView.findViewById(R.id.banner_image);
        wrapperImageView.isUseCache = true;//使用缓存
        wrapperImageView.setImageURL(bannerbean.getImagePath());//加载获取对应的网络图片
        wrapperImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("图片被点击");
                Intent intent = new Intent();
                intent.setClass(MyApplication.getContext(), WebContentActivity.class);
                intent.putExtra("url",bannerbean.getUrl());
                intent.putExtra("title",bannerbean.getTitle());
                MyApplication.getContext().startActivity(intent);
            }
        });
        return mView;
    }

    @Override
    public int getSize() {
        return mBeansList.size();
    }
}