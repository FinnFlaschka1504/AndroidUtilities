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
        switch (array.getInt(R.styleable.SquareLayout_equalMode, 2)) {
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
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int squareLen;

        switch (equalMode) {
            case WIDTH:
                squareLen = width;
                break;
            case HEIGHT:
                squareLen = height;
                break;
            case MIN:
                squareLen = Math.min(width, height);
                break;
            default:
            case MAX:
                squareLen = Math.max(width, height);
                break;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(squareLen, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(squareLen, MeasureSpec.EXACTLY));
    }
}