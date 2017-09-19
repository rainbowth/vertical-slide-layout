package cn.rainbow.android.widget.verticalslide;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

/**
 * Created by bvin on 2017/9/7.
 */

public class VerticalSlideLayout extends ViewGroup {

    private static final String TAG = "VerticalSlideLayout";

    private final int mTouchSlop;
    private float mInitialDownX;
    private float mInitialDownY;
    private float mInitialMotionY;
    private float mMovedOffset;
    private int mFirstChildTop;
    private int mLastChildTop;

    public VerticalSlideLayout(Context context) {
        this(context, null);
    }

    public VerticalSlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childTop = mFirstChildTop;//t + (int) mMovedOffset;
        for (int i = 0; i < childCount; i++) {
            if (mMovedOffset == 0) mLastChildTop = childTop;//记录第一次layout后，最后一个child的Top
            View child = getChildAt(i);
            int childBottom = childTop + child.getMeasuredHeight();
            //若要支持gravity的话需要从新定位left和right
            child.layout(l, childTop, child.getMeasuredWidth(), childBottom);
            childTop += child.getMeasuredHeight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction());
        boolean mIsBeingDragged = super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownX = ev.getX();
                mInitialDownY = ev.getY();
                Log.d(TAG, "onInterceptTouchEvent-ACTION_DOWN: " + mInitialDownY);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent-ACTION_MOVE: ");
                float currentX = ev.getX();
                float currentY = ev.getY();
                final float xDiff = Math.abs(currentX - mInitialDownX);
                float dY = currentY - mInitialDownY;
                float yDiff = Math.abs(dY);
                if (xDiff * 0.5f > yDiff && xDiff > mTouchSlop) {//表示水平方向的滑动
                    Log.d(TAG, "水平方向滑动不拦截事件");
                    mIsBeingDragged = false;
                    break;
                }
                View view = getChildAt(0);
                if (isFingerScrollingUp(dY)) {//向上滑动
                    if (ViewCompat.canScrollVertically(view, -(int) dY)) {
                        Log.d(TAG, "子View还可以往下滚动");
                        mIsBeingDragged = false;
                    }else {
                        View lastView = getChildAt(getChildCount() - 1);
                        View scrollView = getCurrentScrollView(lastView);
                        if (lastView.getTop() == 0) {//当前页是最后一页
                            if (ViewCompat.canScrollVertically(scrollView, -(int) dY)) {//最后一页的内容是否能够向下滚动
                                Log.d(TAG, "lastView可以往下滚动");
                                mIsBeingDragged = false;
                                break;
                            }
                        }
                        //滑动到底部了，拦截子控件的事件，转由自己处理，去滚动到下一个View
                        Log.d(TAG, "子View无法往下滚动");
                        mIsBeingDragged = true;
                    }
                }else if (isFingerScrollingDown(dY)){//向下滑动
                    //if (diffY > mTouchSlop) {
                        /*mInitialMotionY = mInitialDownY + mTouchSlop;
                        Log.d(TAG, "onInterceptTouchEvent: 满足事件拦截条件");
                        mIsBeingDragged = true;*/

                        View lastView = getChildAt(getChildCount() - 1);
                        View scrollView = getCurrentScrollView(lastView);
                        if (lastView.getTop() == 0) {//当前页是最后一页
                            if (ViewCompat.canScrollVertically(scrollView, -(int) dY)){
                                Log.d(TAG, "lastView已经滑动到顶部部了");
                                mIsBeingDragged = false;
                            }else {
                                mIsBeingDragged = true;
                            }
                        }
                    /*}else {
                        Log.d(TAG, "onInterceptTouchEvent: 未满足事件拦截条件");
                        mIsBeingDragged = false;
                    }*/
                }

                break;
        }
        return mIsBeingDragged;
    }

    private View getCurrentScrollView(View parent) {
        if (parent instanceof ViewPager) {
            View firstView = ((ViewPager) parent).getChildAt(0);
            View currentItemView;
            int currentItem = ((ViewPager) parent).getCurrentItem();
            if (firstView instanceof TabLayout) {//如果第一个是TabLayout
                currentItemView = ((ViewPager) parent).getChildAt(currentItem + 1);
            } else {
                currentItemView = ((ViewPager) parent).getChildAt(currentItem);
            }
            return currentItemView;
        }
        return parent;
    }

    /**
     * 手指是否向上滑动
     * @param verticalMoved
     * @return
     */
    private boolean isFingerScrollingUp(float verticalMoved){
        return verticalMoved < 0;
    }

    /**
     * 手指是否乡下滑动
     * @param verticalMoved
     * @return
     */
    private boolean isFingerScrollingDown(float verticalMoved){
        return verticalMoved > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getAction());
        boolean consume = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                consume = true;
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                Log.d(TAG, "onTouchEvent-ACTION_MOVE: "+y);
                mInitialMotionY = mInitialDownY;
                mMovedOffset = y - mInitialMotionY;
                mFirstChildTop = (int) mMovedOffset;
                Log.d(TAG, "onTouchEvent: 移动距离 " + mMovedOffset);
                if (isFingerScrollingDown(mMovedOffset)) {//还有下一个View&&往下滑动
                    if (mMovedOffset < mTouchSlop){
                        consume = false;
                        break;
                    }
                    View view = getChildAt(0);//第一个View(当前View)
                    if (ViewCompat.canScrollVertically(view, -(int) mMovedOffset)){//是否能往上滚动
                        mFirstChildTop  = (int) (mMovedOffset-view.getMeasuredHeight());
                        requestLayout();
                    }else {
                        Log.d(TAG, "onTouchEvent: 往上滑动但无法往上滑动了");
                    }
                }else if(isFingerScrollingUp(mMovedOffset)){//往上滑动
                    if (mMovedOffset > -mTouchSlop){
                        consume = false;
                        break;
                    }else {
                        if (getChildCount()>1){
                            View lastView = getChildAt(getChildCount()-1);
                            if (lastView.getTop()==0){//当前最后一页
                                if (ViewCompat.canScrollVertically(lastView, -(int) mMovedOffset)){//是否能向下滚动

                                }else {//最后一个view不用滚动
                                    consume = false;
                                    break;
                                }
                            }
                        }
                    }
                    requestLayout();
                }else {

                }
                consume = false;
                break;
            case MotionEvent.ACTION_UP://释放
                /// 试图滑动到下一页并且还有下一页，未滑过阈值的取消，滑过阈值的自动切换到下一页
                /// 视图滑动到上一页并且不在第一页，当滑动的距离往负方向未超过阈值的取消切换，超过的自动切换到上一页
                if (mMovedOffset==0){
                    consume = false;
                    break;
                }
                if (mMovedOffset < -300) {
                    Log.d(TAG, "onTouchEvent: 释放 ");
                    mFirstChildTop = -mLastChildTop;//让最后一个child置顶
                    requestLayout();
                }else if (mMovedOffset > 300){
                    mFirstChildTop = 0;
                    requestLayout();
                }else {
                    if (isFingerScrollingDown(mMovedOffset)){//当前页最后一页并试图向下滑动
                        View lastView = getChildAt(getChildCount()-1);
                        if (lastView.getTop()>= 0){//当前页是最后一页
                            mFirstChildTop = -mLastChildTop;//让最后一个child置顶
                            requestLayout();
                        }
                    }else if(isFingerScrollingUp(mMovedOffset)){//向上滑动，还有下一页
                        View lastView = getChildAt(getChildCount()-1);
                        if (lastView.getTop()> 0){
                            mFirstChildTop = 0;
                            requestLayout();
                        }
                    }

                }
                consume = true;
                break;
        }
        return consume;
    }
}
