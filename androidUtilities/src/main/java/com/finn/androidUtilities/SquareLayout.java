package com.finn.androidUtilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

    enum EQUAL_MODE {
        HEIGHT, WIDTH, MAX, MIN
    }

    EQUAL_MODE equalMode = EQUAL_MODE.WIDTH;

    public EQUAL_MODE getEqualMode() {
        return equalMode;
    }

    public SquareLayout setEqualMode(EQUAL_MODE equalMode) {
        this.equalMode = equalMode;
        return this;
    }

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
        readAttributes(array);
        array.recycle();
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
        readAttributes(array);
        array.recycle();
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout, defStyleAttr, defStyleRes);
        readAttributes(array);
        array.recycle();
    }

    private void readAttributes(TypedArray array) {
        switch (array.getInt(R.styleable.SquareLayout_equalMode, 0)) {
            case 0:
                equalMode = EQUAL_MODE.WIDTH;
                break;
            case 1:
                equalMode = EQUAL_MODE.HEIGHT;
                break;
            case 2:
                equalMode = EQUAL_MODE.MAX;
                break;
            case 3:
                equalMode = EQUAL_MODE.MIN;
                break;
        }
//        equalMode = array.getInt(R.styleable.SquareLayout_equalMode, 0) == 0 ? EQUAL_MODE.WIDTH : EQUAL_MODE.HEIGHT;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
////        int height = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY);
//////        int size = width > height ? height : width;
//////        setMeasuredDimension(size, size);
////        super.onMeasure(width, height);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        switch (equalMode) {
//            case WIDTH:
//                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
////                setMeasuredDimension(width, width);
//                break;
//            case HEIGHT:
//                super.onMeasure(heightMeasureSpec, heightMeasureSpec);
////                setMeasuredDimension(height, height);
//                break;
//            case MAX:
//                int size_max = Integer.max(widthMeasureSpec, heightMeasureSpec);
//                super.onMeasure(size_max, size_max);
////                int size_max = Integer.max(height, width);
////                setMeasuredDimension(size_max, size_max);
//                break;
//            case MIN:
//                int size_min = Integer.min(widthMeasureSpec, heightMeasureSpec);
//                super.onMeasure(size_min, size_min);
////                int size_min = Integer.min(height, width);
////                setMeasuredDimension(size_min, size_min);
//                break;
//        }




//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        int matchParentMeasureSpec = MeasureSpec.makeMeasureSpec(((View) getParent()).getWidth(), MeasureSpec.EXACTLY);
//        int wrapContentMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        measure(matchParentMeasureSpec, wrapContentMeasureSpec);
//        int height = getMeasuredHeight();
//
//        int matchParentMeasureSpec_width = MeasureSpec.makeMeasureSpec(((View) getParent()).getHeight(), MeasureSpec.EXACTLY);
//        int wrapContentMeasureSpec_width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        measure(wrapContentMeasureSpec_width, matchParentMeasureSpec_width);
//        int width = getMeasuredWidth();
//        switch (equalMode) {
//            case WIDTH:
////                layoutParams.height = width;
//                setMeasuredDimension(width, width);
//                break;
//            case HEIGHT:
////                layoutParams.width = height;
//                setMeasuredDimension(height, height);
//                break;
//            case MIN:
//                int min = width < height ? width : height;
//                setMeasuredDimension(min, min);
////                layoutParams.width = min;
////                layoutParams.height = min;
//                break;
//            case MAX:
//                int max = width > height ? width : height;
//                setMeasuredDimension(max, max);
////                layoutParams.width = max;
////                layoutParams.height = max;
//                break;
//        }

    }

}