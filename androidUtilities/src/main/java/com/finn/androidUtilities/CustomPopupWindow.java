package com.finn.androidUtilities;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

public class CustomPopupWindow {
    enum LAYOUT_PARAMS {
        MATCH_PARENT(-1), WRAP_CONTENT(-2);
        int value;

        LAYOUT_PARAMS(int value) {
            this.value = value;
        }
    }
    public enum POSITION_RELATIVE_TO_ANCHOR{
        DEFAULT, TOP, LEFT
    }
    private boolean dimBackground = true;
    private boolean centerOnScreen;
    private View anchor;
    private View view;
    private PopupWindow popupWindow;
    private LAYOUT_PARAMS widthParam = LAYOUT_PARAMS.WRAP_CONTENT;
    private LAYOUT_PARAMS heightParam = LAYOUT_PARAMS.WRAP_CONTENT;
    private float dimAmount = 0.5f;
    private POSITION_RELATIVE_TO_ANCHOR positionRelativeToAnchor = POSITION_RELATIVE_TO_ANCHOR.DEFAULT;
    private int xoff;
    private int yoff;

    public CustomPopupWindow(View anchor, View view) {
        popupWindow = new PopupWindow(widthParam.value, heightParam.value);
        this.anchor = anchor;
        this.view = view;
    }

    public static CustomPopupWindow Builder(View anchor, View view) {
        return new CustomPopupWindow(anchor, view);
    }

    //  --------------- Getters & Setters --------------->
    public CustomPopupWindow enableCenterOnScreen() {
        this.centerOnScreen = true;
        return this;
    }

    public void disableDimBackground() {
        this.dimBackground = false;
    }

    public CustomPopupWindow setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public CustomPopupWindow setWidthParam(LAYOUT_PARAMS widthParam) {
        this.widthParam = widthParam;
        return this;
    }

    public CustomPopupWindow setHeightParam(LAYOUT_PARAMS heightParam) {
        this.heightParam = heightParam;
        return this;
    }

    public CustomPopupWindow setPositionRelativeToAnchor(POSITION_RELATIVE_TO_ANCHOR positionRelativeToAnchor) {
        this.positionRelativeToAnchor = positionRelativeToAnchor;
        return this;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public CustomPopupWindow setXoff(int xoff) {
        this.xoff = xoff;
        return this;
    }

    public CustomPopupWindow setYoff(int yoff) {
        this.yoff = yoff;
        return this;
    }
    //  <--------------- Getters & Setters ---------------


    //  --------------- Actions --------------->
    public CustomPopupWindow show() {
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        if (xoff == 0 && yoff == 0) {
            switch (positionRelativeToAnchor) {
                default:
                case DEFAULT:
                    xoff = 0;
                    yoff = 0;
                    break;
                case TOP:
                    xoff = 0;
                    yoff = -view.getMinimumHeight();
                    break;
            }
        }

        if (centerOnScreen)
            popupWindow.showAtLocation(anchor, Gravity.CENTER, xoff, yoff);
        else
            popupWindow.showAsDropDown(anchor, xoff, yoff);

        if (dimBackground)
            dimBehind();

        return this;
    }

    public PopupWindow show_popupWindow() {
        show();
        return popupWindow;
    }
    //  <--------------- Actions ---------------


    //  --------------- Convenience --------------->
    public void dimBehind() {
        View container;
        if (popupWindow.getBackground() == null) {
            container = (View) popupWindow.getContentView().getParent();
        } else {
            container = (View) popupWindow.getContentView().getParent().getParent();
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = dimAmount;
        wm.updateViewLayout(container, p);
    }

    public static CustomPopupWindow showLoadingWindow(Context context, View anchor){
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        return CustomPopupWindow.Builder(anchor, progressBar).enableCenterOnScreen().show();
    }

    public CustomPopupWindow dismiss() {
        popupWindow.dismiss();
        return this;
    }
    //  <--------------- Convenience ---------------
}
