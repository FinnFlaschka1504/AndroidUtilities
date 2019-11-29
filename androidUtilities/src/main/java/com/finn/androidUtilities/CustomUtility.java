package com.finn.androidUtilities;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CustomUtility {

    //  --------------- isOnline --------------->
    static public boolean isOnline(Context context) {
        boolean isOnleine = isOnline();
        if (isOnleine) {
            return true;
        } else {
            Toast.makeText(context, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    static public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    //  <--------------- isOnline ---------------

    public static void restartApp(Context context, Class startClass) {
        Intent mStartActivity = new Intent(context, startClass);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static void changeWindowKeyboard(Window window, boolean show) {
        if (show)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        else
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void changeWindowKeyboard(Context context, View view, boolean show) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        else
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openUrl(Context context, String url, boolean select) {
        if (!url.contains("http://") && !url.contains("https://"))
            url = "http://".concat(url);
        if (!select) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            Intent chooser = Intent.createChooser(i, "Öffnen mit...");
            if (chooser.resolveActivity(context.getPackageManager()) != null)
                context.startActivity(chooser);
        }
    }

    public static String formatToEuro(double amount) {
        if (amount == 0)
            return "N/A";
        if (amount % 1 == 0)
            return String.format(Locale.GERMANY,"%.0f €", amount);
        else
            return String.format(Locale.GERMANY,"%.2f €", amount);
    }


    //  --------------- OnClickListener --------------->
    public static View.OnClickListener getOnClickListener(View view) {
        View.OnClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";

        try {
            Field listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo");
            Object listenerInfo = null;

            if (listenerField != null) {
                listenerField.setAccessible(true);
                listenerInfo = listenerField.get(view);
            }

            Field clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnClickListener");

            if (clickListenerField != null && listenerInfo != null) {
                retrievedListener = (View.OnClickListener) clickListenerField.get(listenerInfo);
            }
        } catch (NoSuchFieldException ex) {
            Log.e("Reflection", "No Such Field.");
        } catch (IllegalAccessException ex) {
            Log.e("Reflection", "Illegal Access.");
        } catch (ClassNotFoundException ex) {
            Log.e("Reflection", "Class Not Found.");
        }
        return retrievedListener;
    }

    public static void interceptOnClick(View view, InterceptOnClick interceptOnClick) {
        View.OnClickListener oldListener = getOnClickListener(view);
        view.setOnClickListener(v -> {
            if (!interceptOnClick.runInterceptOnClick(view))
                oldListener.onClick(view);
        });
    }

    interface InterceptOnClick{
        boolean runInterceptOnClick(View view);
    }
    //  <--------------- OnClickListener ---------------

    //  ----- Filter ----->
    private static boolean contains(String all, String sub) {
    return all.toLowerCase().contains(sub.toLowerCase());
}
    //  <----- Filter -----


    //  --------------- Time --------------->
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date shiftTime(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();

    }
    //  <--------------- Time ---------------


    //  --------------- Toast --------------->
    public static Toast centeredToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        return toast;
    }
    public static void showCenteredToast(Context context, String text) {
        centeredToast(context, text).show();
    }
    //  <--------------- Toast ---------------

    public static class Triple<A,B,C> {
        public A first;
        public B second;
        public C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public static class Quadruple<A,B,C,D> {
        public A first;
        public B second;
        public C third;
        public D fourth;

        public Quadruple(A first, B second, C third, D fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }
    }


    //  ----- Pixels ----->
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setMargins (View v, int links, int oben, int rechts, int unten) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(dpToPx(links), dpToPx(oben), dpToPx(rechts), dpToPx(unten));
            v.requestLayout();
        }
    }
    //  <----- Pixels -----


    public static void sendText(AppCompatActivity activity, String text) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        if (waIntent != null) {
            waIntent.putExtra(Intent.EXTRA_TEXT, text);//
            activity.startActivity(Intent.createChooser(waIntent, "App auswählen"));
        } else {
            Toast.makeText(activity, "WhatsApp not found", Toast.LENGTH_SHORT).show();
        }

    }


    //  --------------- Layout-Animation --------------->
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300); //(long) ((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 1.5));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300); //(int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void changeHeight(final View v, ChangeLayout changeLayout){
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        int previousHeight = v.getMeasuredHeight();

        changeLayout.runChangeLayout(v);

        matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        int targetHeight = v.getMeasuredHeight();

        if (previousHeight == targetHeight)
            return;

        v.setPressed(false);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(previousHeight, targetHeight);
        valueAnimator.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            v.getLayoutParams().height = val == targetHeight ? LinearLayout.LayoutParams.WRAP_CONTENT : val;
            v.requestLayout();
        });

        valueAnimator.setDuration(300).start();
    }
    public interface ChangeLayout {
        void runChangeLayout(View view);
    }
    //  <--------------- Layout-Animation ---------------
}
