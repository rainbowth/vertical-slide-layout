package cn.rainbow.android.widget.verticalslide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bvin on 2017/9/7.
 */

public class VerticalSlideLayout extends ViewGroup {
    public VerticalSlideLayout(Context context) {
        super(context);
    }

    public VerticalSlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childTop = t;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(l, childTop, r, b);
            childTop += child.getMeasuredHeight();
        }
    }
}
