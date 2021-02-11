package com.finn.androidUtilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MaxDimensionsLayout extends LinearLayout {
    float maxHeight = -1;
    float maxWidth = -1;
    boolean enabled = true;

    public MaxDimensionsLayout(Context context) {
        super(context);
    }

    public MaxDimensionsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaxDimensionsLayout);
        readAttributes(array);
        array.recycle();
    }

    public MaxDimensionsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaxDimensionsLayout);
        readAttributes(array);
        array.recycle();
    }

    public MaxDimensionsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaxDimensionsLayout, defStyleAttr, defStyleRes);
        readAttributes(array);
        array.recycle();
    }

    private void readAttributes(TypedArray array) {
        maxWidth = array.getDimension(R.styleable.MaxDimensionsLayout_maxWidth, -1);
        maxHeight = array.getDimension(R.styleable.MaxDimensionsLayout_maxHeight, -1);
        enabled = array.getBoolean(R.styleable.MaxDimensionsLayout_android_enabled, true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
////        if (heightMode != MeasureSpec.UNSPECIFIED) {
//        int measuredHeightBefore = getMeasuredHeight();
////        super.measure(0,0);
//        measureChildren(0, 0);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//        LinearLayout parent = (LinearLayout) getParent();
//        int measuredHeight = parent.getMeasuredHeight();
//
//        int childHeightSum = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            childHeightSum += child.getMeasuredHeight();
//        }
//        int measuredHeightAfter = getMeasuredHeight();
        if (maxHeight != -1 && enabled) {
//            int min = Math.min(parentHeight, childHeightSum);

            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) maxHeight, MeasureSpec.AT_MOST);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST);
        }
//        }
        if (maxWidth != -1 && enabled) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) maxWidth, MeasureSpec.AT_MOST);
        }
        setMeasuredDimension(maxWidth == -1 ? parentWidth : Math.min(parentWidth, (int) maxWidth), maxHeight == -1 ? parentHeight : Math.min(parentWidth, (int) maxWidth));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    //  ------------------------- Getter & Setter ------------------------->
    public float getMaxHeight() {
        return maxHeight;
    }

    public MaxDimensionsLayout setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public MaxDimensionsLayout setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    //  <------------------------- Getter & Setter -------------------------
}
