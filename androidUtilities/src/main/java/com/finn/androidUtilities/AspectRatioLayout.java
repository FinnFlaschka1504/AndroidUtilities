package com.finn.androidUtilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AspectRatioLayout extends LinearLayout {

    enum TARGET_MODE {
        HEIGHT, WIDTH, MAX, MIN
    }

    TARGET_MODE targetMode = TARGET_MODE.HEIGHT;
    Float aspectRatio;

    //  ------------------------- Getter & Setter ------------------------->
    public TARGET_MODE getTargetMode() {
        return targetMode;
    }

    public AspectRatioLayout setTargetMode(TARGET_MODE targetMode) {
        this.targetMode = targetMode;
        return this;
    }

    public Float getAspectRatio() {
        return aspectRatio;
    }

    public AspectRatioLayout setAspectRatio(Float aspectRatio) {
        this.aspectRatio = aspectRatio;
        return this;
    }
    //  <------------------------- Getter & Setter -------------------------

    public AspectRatioLayout(Context context) {
        super(context);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);
        readAttributes(array);
        array.recycle();
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);
        readAttributes(array);
        array.recycle();
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout, defStyleAttr, defStyleRes);
        readAttributes(array);
        array.recycle();
    }

    private void readAttributes(TypedArray array) {
        aspectRatio = array.getFloat(R.styleable.AspectRatioLayout_aspectRatio, 1f);

        switch (array.getInt(R.styleable.AspectRatioLayout_targetMode, 1)) {
            case 0:
                targetMode = TARGET_MODE.WIDTH;
                break;
            case 1:
                targetMode = TARGET_MODE.HEIGHT;
                break;
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int width_result;
        int height_result;

        switch (targetMode) {
            default:
            case WIDTH:
                height_result = height;
                width_result = (int) (height * aspectRatio);
                break;
            case HEIGHT:
                width_result = width;
                height_result = (int) (width * aspectRatio);
                break;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width_result, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height_result, MeasureSpec.EXACTLY));
    }



}