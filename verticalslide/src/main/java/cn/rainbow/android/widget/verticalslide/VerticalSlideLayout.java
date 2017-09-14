package cn.rainbow.android.widget.verticalslide;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by bvin on 2017/9/7.
 */

public class VerticalSlideLayout extends ViewGroup {

    private static final String TAG = "VerticalSlideLayout";

    private final int mTouchSlop;
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
            Log.d(TAG, "onLayout: " + childTop + "," + childBottom);
            child.layout(l, childTop, r, childBottom);
            childTop += child.getMeasuredHeight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction());
        boolean mIsBeingDragged = super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getY();
                Log.d(TAG, "onInterceptTouchEvent-ACTION_DOWN: " + mInitialDownY);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent-ACTION_MOVE: ");
                float currentY = ev.getY();
                float diffY = currentY - mInitialDownY;
                View view = getChildAt(0);
                if (isFingerScrollingUp(diffY)) {//向上滑动
                    if (ViewCompat.canScrollVertically(view, -(int) diffY)) {
                        Log.d(TAG, "子View还可以往下滚动");
                        mIsBeingDragged = false;
                    }else {
                        //滑动到底部了，拦截子控件的事件，转由自己处理，去滚动到下一个View
                        Log.d(TAG, "子View无法往下滚动");
                        Toast.makeText(getContext(), "已经滑动到底部了", Toast.LENGTH_SHORT).show();
                        mIsBeingDragged = true;
                    }
                }
                /*if (diffY > mTouchSlop) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    Log.d(TAG, "onInterceptTouchEvent: 满足事件拦截条件");
                    mIsBeingDragged = true;
                }*/
                break;
        }
        return mIsBeingDragged;
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
                if (mMovedOffset > 0) {//还有下一个View&&往下滑动
                    View view = getChildAt(0);//第一个View(当前View)
                    if (ViewCompat.canScrollVertically(view, -(int) mMovedOffset)){//是否能往上滚动
                        //requestLayout();
                    }else {
                        Log.d(TAG, "onTouchEvent: 往上滑动但无法往上滑动了");
                    }
                }else {//往上滑动
                    requestLayout();
                }
                consume = false;
                break;
            case MotionEvent.ACTION_UP:
                if (mMovedOffset < -300) {
                    Log.d(TAG, "onTouchEvent: 释放 ");
                    mFirstChildTop = -mLastChildTop;//让最后一个child置顶
                    requestLayout();
                }else {
                    mFirstChildTop = 0;
                    requestLayout();
                }
                consume = true;
                break;
        }
        return consume;
    }
}
