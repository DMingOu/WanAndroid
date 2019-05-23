package com.example.odm.wanandroid.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.odm.wanandroid.R;
import com.example.odm.wanandroid.adapter.BannerViewAdapter;
import com.example.odm.wanandroid.adapter.BannerViewBaseAdapter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BannerView --Banner控件属性原点布局
 * Created by ODM on 2019/5/18.
 */

public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    /**
     *  圆点布局
     */
    private LinearLayout mPointContainer;

    private BannerViewBaseAdapter mAdapter_BannerViewBase;

    /**
     *  圆点数量
     */
    private int mPointCount;

    /**
     *  圆点图片
     */
    private ImageView[] mPoints;

    /**
     *  最后一个圆点
     */
    private int mLastPos;

    /**
     *  当前是否触摸
     */
    private boolean isTouch = false;

    private ScheduledExecutorService executorService;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                        }
                    },2000);
                    break;
                default:
                    break;
            }
        }
    };

    public BannerView(@NonNull Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        mViewPager =(ViewPager) findViewById(R.id.vp_container);
        mPointContainer =(LinearLayout) findViewById(R.id.ll_point);
        mViewPager.addOnPageChangeListener(this);
    }

    public void setAdapter(BannerViewAdapter adapter) {
        this.mAdapter_BannerViewBase = adapter;
        mPointCount = mAdapter_BannerViewBase.getSize();
        mViewPager.setAdapter(mAdapter_BannerViewBase);
        initPoint();
        /**
         *  防止第二次刷新后 显示空白页面
         */
        mViewPager.setCurrentItem(mPointCount*100+3);
        startScroll();
    }

    /**
     *  加载圆点
     */
    private void initPoint() {
        if (mPointCount == 0) {
            return;
        }

        mPoints = new ImageView[mPointCount];
        //清除所有圆点
        mPointContainer.removeAllViews();
        for (int i=0;i < mPointCount;i++) {
            ImageView view = new ImageView(getContext());
            view.setImageResource(R.drawable.point_normal);
            mPointContainer.addView(view);
            mPoints[i] = view;
        }
        if (mPoints[0] != null) {
            mPoints[0].setImageResource(R.drawable.point_selected);
        }
        mLastPos = 0;
    }
    private void changePoint(int currentPoint) {
        if (mLastPos == currentPoint) {
            return;
        }
        mPoints[currentPoint].setImageResource(R.drawable.point_selected);
        mPoints[mLastPos].setImageResource(R.drawable.point_normal);
        mLastPos = currentPoint;
    }

    public void startScroll() {
        executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isTouch) {
                    return;
                }
                handler.sendEmptyMessage(0);
            }
        },1000,3000, TimeUnit.MILLISECONDS);
    }

    /**
     * 手动关闭线程池，也就是调用BannView中的cancelScroll()方法，不然会造成，进行网络加载，第二次刷新加载数据时，banner直接出现空白页面
     */
    public void cancelScroll() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void destroyDrawingCache() {
        super.destroyDrawingCache();
        executorService.shutdown();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPointCount != 0) {
            changePoint(position % mPointCount);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
