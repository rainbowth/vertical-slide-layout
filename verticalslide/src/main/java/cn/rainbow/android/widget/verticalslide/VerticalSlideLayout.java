package cn.rainbow.android.widget.verticalslide;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by bvin on 2017/9/7.
 */

public class VerticalSlideLayout extends ViewGroup {

    private static final String TAG = "VerticalSlideLayout";

    private final int mTouchSlop;
    private float mInitialDownY;
    private float mInitialMotionY;
    private float mMovedOffset;

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
        int childTop = t + (int) mMovedOffset;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            Log.d(TAG, "onLayout: " + childTop + "," + b);
            child.layout(l, childTop, r, childTop + child.getMeasuredHeight());
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
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                if (currentY - mInitialDownY > mTouchSlop) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    Log.d(TAG, "onInterceptTouchEvent: 满足事件拦截条件");
                    mIsBeingDragged = true;
                }
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                mMovedOffset = y - mInitialMotionY;
                Log.d(TAG, "onTouchEvent: 移动距离 " + mMovedOffset);
                requestLayout();
                break;
        }
        return true;
    }
}
