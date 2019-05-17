package com.example.odm.wanandroid.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 去除动画防止，防止出现recyclerView的bug：java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
 * Created by ODM on 2019/5/12.
 */

public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager{

    public RecyclerViewNoBugLinearLayoutManager(Context context) {
        super( context );
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super( context, orientation, reverseLayout );
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super( context, attrs, defStyleAttr, defStyleRes );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren( recycler, state );
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
