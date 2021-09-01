package com.finn.androidUtilities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.pixplicity.sharp.Sharp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import top.defaults.drawabletoolbox.DrawableBuilder;


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

    // ---

    public static PingTask isOnline(Runnable onTrue) {
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(onTrue, null));
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, onTrue, null));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isNotOnline(Runnable onFalse) {
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(null, onFalse));
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, null, onFalse));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isOnline(Runnable onTrue, Runnable onFalse) {
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(onTrue, onFalse));
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, onTrue, onFalse));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isOnline(OnResult onResult) {
        if (!PingTask.hasPending()) {
            PingTask<OnResult> task = new PingTask<>();
            task.execute(onResult);
            return task;
        } else {
            PingTask.addRequest(new Triple<>(onResult, null, null));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isOnline(Context context, Runnable onTrue, Runnable onFalse) {
        Runnable interceptOnFalse = () -> {
            Toast.makeText(context, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
            onFalse.run();
        };
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(onTrue, interceptOnFalse));
            checkStatus(task, context);
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, onTrue, interceptOnFalse));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isOnline(Context context, Runnable onTrue) {
        Runnable interceptOnFalse = () -> {
            Toast.makeText(context, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
        };
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(onTrue, interceptOnFalse));
            checkStatus(task, context);
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, onTrue, interceptOnFalse));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isNotOnline(Context context, Runnable onFalse) {
        Runnable interceptOnFalse = () -> {
            Toast.makeText(context, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
            onFalse.run();
        };
        if (!PingTask.hasPending()) {
            PingTask<Pair<Runnable, Runnable>> task = new PingTask<>();
            task.execute(new Pair<>(null, interceptOnFalse));
            checkStatus(task, context);
            return task;
        } else {
            PingTask.addRequest(new Triple<>(null, null, interceptOnFalse));
            return PingTask.getCurrentTask();
        }
    }

    public static PingTask isOnline(Context context, OnResult onResult) {
        OnResult interceptOnResult = status -> {
            if (!status)
                Toast.makeText(context, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
            onResult.runOnResult(status);
        };
        if (!PingTask.hasPending()) {
            PingTask<OnResult> task = new PingTask<>();
            task.execute(interceptOnResult);
            checkStatus(task, context);
            return task;
        } else {
            PingTask.addRequest(new Triple<>(interceptOnResult, null, null));
            return PingTask.getCurrentTask();
        }
    }

    private static void checkStatus(PingTask pingTask, Context context) {
        new Handler().postDelayed(() ->
        {
            if (pingTask.isRunning())
                Toast.makeText(context, "Einen Moment bitte..", Toast.LENGTH_SHORT).show();
        }, 1000);
    }

    public interface OnResult {
        void runOnResult(boolean status);
    }

    public static class PingTask<T> extends AsyncTask<T, Integer, Boolean> {
        private static PingTask currentTask;
        private OnResult onResult;
        private Runnable onTrue;
        private Runnable onFalse;
        private static Pair<Boolean, Integer> simulate;
        private MenuItem menuItem;
        private static Map<Object, PingTask> taskMap = new HashMap<>();
        private static List<Triple<OnResult, Runnable, Runnable>> requestList = new ArrayList<>();
        private static int pendingRequests;

        @Override
        protected Boolean doInBackground(T... ts) {
            if (ts.length == 0)
                return null;

            pendingRequests++;
            currentTask = this;

            T t = ts[0];
            if (t instanceof OnResult) {
                this.onResult = (OnResult) t;
                requestList.add(new Triple<>(onResult, null, null));
            } else if (t instanceof Pair) {
                onTrue = ((Pair<Runnable, Runnable>) t).first;
                onFalse = ((Pair<Runnable, Runnable>) t).second;
                requestList.add(new Triple<>(null, onTrue, onFalse));
            }


            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int exitValue = ipProcess.waitFor();


                if (simulate != null) {
                    Thread.sleep(simulate.second);
                    if (simulate.first != null)
                        return simulate.first;
                    else
                        return (exitValue == 0);
                } else
                    return (exitValue == 0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pendingRequests--;

            for (Triple<OnResult, Runnable, Runnable> triple : requestList) {
                if (triple.first != null) triple.first.runOnResult(aBoolean);
                if (aBoolean && triple.second != null) triple.second.run();
                if (!aBoolean && triple.third != null) triple.third.run();
            }

            requestList.clear();

//            if (onResult != null) onResult.runOnResult(aBoolean);
//            if (aBoolean && onTrue != null) onTrue.run();
//            if (!aBoolean && onFalse != null) onFalse.run();
            if (menuItem != null) menuItem.setEnabled(true);
            if (taskMap.containsValue(this))
                new HashSet<>(taskMap.entrySet()).stream().filter(entry -> entry.getValue().equals(this)).forEach(entry -> taskMap.remove(entry.getKey()));
        }

        public static void simulate(@Nullable Boolean returnValue, int delay) {
            simulate = new Pair<>(returnValue, delay);
        }

        public void suspendOnClick(View view) {
            interceptOnClick(view, view1 -> isRunning());
        }

        public boolean isRunning() {
            return getStatus() != Status.FINISHED;
        }

        public static boolean hasPending() {
            return pendingRequests > 0;
        }

        public void suspendMenuItem(MenuItem menuItem) {
            this.menuItem = menuItem;
            this.menuItem.setEnabled(false);
        }

        public void markAsPending(Object o) {
            taskMap.put(o, this);
        }

        public static boolean isPending(Object o) {
            PingTask task = taskMap.get(o);
            return task != null && task.isRunning();
        }

        public static PingTask getCurrentTask() {
            return currentTask;
        }

        public static void addRequest(Triple<OnResult, Runnable, Runnable> quadruple) {
            requestList.add(quadruple);
        }
    }
    //  <--------------- isOnline ---------------


    //  ------------------------- Keyboard ------------------------->
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

    public static class KeyboardChangeListener {
        private Activity activity;
        private View rootView;
        private Runnable onShown;
        private Runnable onHide;
        private GenericInterface<Boolean> onChange;
        private ViewTreeObserver.OnGlobalLayoutListener layoutListener;
        private boolean prevState;
        private boolean ignorePrevState;

        //  ------------------------- Constructor ------------------------->
        public KeyboardChangeListener(Activity activity) {
            this.activity = activity;
            rootView = activity.findViewById(android.R.id.content).getRootView();
            applyListener();
        }

        public static KeyboardChangeListener bind(Activity activity) {
            return new KeyboardChangeListener(activity);
        }
        //  <------------------------- Constructor -------------------------


        //  ------------------------- Getter & Setter ------------------------->
        public KeyboardChangeListener setOnShown(Runnable onShown) {
            this.onShown = onShown;
            return this;
        }

        public KeyboardChangeListener setOnHide(Runnable onHide) {
            this.onHide = onHide;
            return this;
        }

        public KeyboardChangeListener setOnChange(GenericInterface<Boolean> onChange) {
            this.onChange = onChange;
            return this;
        }

        public KeyboardChangeListener enableIgnorePrevState() {
            this.ignorePrevState = true;
            return this;
        }
        //  <------------------------- Getter & Setter -------------------------


        //  ------------------------- Convenience ------------------------->
        private void applyListener() {
            activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    layoutListener = () -> {
                        int rootViewHeight = rootView.getHeight();
                        Rect rect = new Rect();

                        rootView.getWindowVisibleDisplayFrame(rect);

                        int availableSpace = rect.bottom - rect.top;
                        int heightDiff = rootViewHeight - availableSpace;

                        boolean result = heightDiff > 100;

                        if (result != prevState || ignorePrevState) {
                            if (onChange != null)
                                onChange.run(result);

                            if (result && onShown != null)
                                onShown.run();
                            else if (!result && onHide != null)
                                onHide.run();
                        }

                        prevState = result;
                    };
                    rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    unregister();
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            });
        }

        public void unregister() {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        }
        //  <------------------------- Convenience -------------------------
    }
    //  <------------------------- Keyboard -------------------------

    public static void restartApp(Context context, Class<? extends AppCompatActivity> startClass) {
        Intent mStartActivity = new Intent(context, startClass);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static void restartApp(AppCompatActivity context) {
        restartApp(context, context.getClass());
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
        if (amount % 1 == 0)
            return String.format(Locale.GERMANY, "%.0f €", amount);
        else
            return String.format(Locale.GERMANY, "%.2f €", amount);
    }

    public static void tintImageButton(@NonNull ImageView button, boolean colored, Context context, int iconId) {
        Drawable drawable = ContextCompat.getDrawable(context, iconId).mutate();
        ColorStateList colours;
        if (colored)
            colours = button.getResources().getColorStateList(com.finn.androidUtilities.R.color.button_state_list_colored, null);
        else
            colours = button.getResources().getColorStateList(com.finn.androidUtilities.R.color.button_state_list_image, null);
        button.setColorFilter(colours.getColorForState(button.getDrawableState(), Color.GREEN), PorterDuff.Mode.SRC_IN);
//        new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.color_black), PorterDuff.Mode.SRC_ATOP);
//        new ColorFilter()
//        drawable.setColorFilter(colours.getColorForState(button.getDrawableState(), Color.GREEN));
//        TypedValue typedValue = new TypedValue();
//        context.getTheme().res
    }

    public static void colorMenuItemIcon(Menu menu, @IdRes int id, int color){
        menu.findItem(id).setIconTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}}, new int[]{color}));
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
        interceptOnClick(view, false, interceptOnClick);
    }

    public static void interceptOnClick(View view, boolean skipWithLongClick, InterceptOnClick interceptOnClick) {
        View.OnClickListener oldListener = getOnClickListener(view);
        view.setOnClickListener(v -> {
            if (!interceptOnClick.runInterceptOnClick(view))
                oldListener.onClick(view);
        });
        if (skipWithLongClick)
            view.setOnLongClickListener(v -> {
                oldListener.onClick(v);
                return true;
            });
    }

    public interface InterceptOnClick {
        boolean runInterceptOnClick(View view);
    }
    //  <--------------- OnClickListener ---------------

    //  ----- Filter ----->
    private static boolean contains(String all, String sub) {
        return all.toLowerCase().contains(sub.toLowerCase());
    }
    //  <----- Filter -----

    //  ------------------------- Checks ------------------------->
    public static boolean isUrl(String text) {
        return text.matches("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
    }
    //  <------------------------- Checks -------------------------

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

    public static Date removeMilliseconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date shiftTime(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();

    }

    public static boolean isUpcoming(Date date) {
        if (date == null)
            return false;
        return new Date().before(date);
    }

    public static Date getDateFromJsonString(String key, JSONObject jsonObject) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).parse(jsonObject.getString(key));
        } catch (ParseException | JSONException e) {
            return null;
        }
    }
    //  <--------------- Time ---------------


    //  --------------- Toast --------------->
    public static Toast centeredToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        return toast;
    }

    public static void showCenteredToast(Context context, String text) {
        centeredToast(context, text).show();
    }

    public static void showOnClickToast(Context context, String text, View.OnClickListener onClickListener) {
        Toast toast = centeredToast(context, text);
        View view = toast.getView().findViewById(android.R.id.message);
        if (view != null) view.setOnClickListener(onClickListener);
        toast.show();
    }
//  <--------------- Toast ---------------

    public static class Triple<A, B, C> {
        public A first;
        public B second;
        public C third;

        //  ------------------------- Constructor ------------------------->
        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public static <A, B, C> Triple<A, B, C> create(A a, B b, C c) {
            return new Triple<>(a, b, c);
        }
        //  <------------------------- Constructor -------------------------


        //  ------------------------- Getter & Setter ------------------------->
        public A getFirst() {
            return first;
        }

        public Triple<A, B, C> setFirst(A first) {
            this.first = first;
            return this;
        }

        public B getSecond() {
            return second;
        }

        public Triple<A, B, C> setSecond(B second) {
            this.second = second;
            return this;
        }

        public C getThird() {
            return third;
        }

        public Triple<A, B, C> setThird(C third) {
            this.third = third;
            return this;
        }
        //  <------------------------- Getter & Setter -------------------------
    }

    public static class Quadruple<A, B, C, D> {
        public A first;
        public B second;
        public C third;
        public D fourth;

        //  ------------------------- Constructor ------------------------->
        public Quadruple(A first, B second, C third, D fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        public static <A, B, C, D> Quadruple<A, B, C, D> create(A a, B b, C c, D d) {
            return new Quadruple<>(a, b, c, d);
        }
        //  <------------------------- Constructor -------------------------


        //  ------------------------- Getter & Setter ------------------------->
        public A getFirst() {
            return first;
        }

        public Quadruple<A, B, C, D> setFirst(A first) {
            this.first = first;
            return this;
        }

        public B getSecond() {
            return second;
        }

        public Quadruple<A, B, C, D> setSecond(B second) {
            this.second = second;
            return this;
        }

        public C getThird() {
            return third;
        }

        public Quadruple<A, B, C, D> setThird(C third) {
            this.third = third;
            return this;
        }

        public D getFourth() {
            return fourth;
        }

        public Quadruple<A, B, C, D> setFourth(D fourth) {
            this.fourth = fourth;
            return this;
        }
        //  <------------------------- Getter & Setter -------------------------
    }

    //  ----- Pixels ----->
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setMargins(View v, int margin) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            margin = dpToPx(margin);
            p.setMargins(margin, margin, margin, margin);
            v.requestLayout();
        }
    }

    public static void setMargins(View v, int horizontal, int vertical) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(
                    horizontal == -1 ? p.leftMargin : dpToPx(horizontal),
                    vertical == -1 ? p.topMargin : dpToPx(vertical),
                    horizontal == -1 ? p.rightMargin : dpToPx(horizontal),
                    vertical == -1 ? p.bottomMargin : dpToPx(vertical));
            v.requestLayout();
        }
    }

    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(
                    left == -1 ? p.leftMargin : dpToPx(left),
                    top == -1 ? p.topMargin : dpToPx(top),
                    right == -1 ? p.rightMargin : dpToPx(right),
                    bottom == -1 ? p.bottomMargin : dpToPx(bottom));
            v.requestLayout();
        }
    }

    public static void setPadding(View v, int padding) {
        padding = dpToPx(padding);
        v.setPadding(padding, padding, padding, padding);
    }

    public static void setPadding(View v, int horizontal, int vertical) {
            v.setPadding(
                    horizontal == -1 ? v.getPaddingLeft() : dpToPx(horizontal),
                    vertical == -1 ? v.getPaddingTop() : dpToPx(vertical),
                    horizontal == -1 ? v.getPaddingRight() : dpToPx(horizontal),
                    vertical == -1 ? v.getPaddingBottom() : dpToPx(vertical));
    }

    public static void setPadding(View v, int left, int top, int right, int bottom) {
        v.setPadding(
                left == -1 ? v.getPaddingLeft() : dpToPx(left),
                top == -1 ? v.getPaddingTop() : dpToPx(top),
                right == -1 ? v.getPaddingRight() : dpToPx(right),
                bottom == -1 ? v.getPaddingBottom() : dpToPx(bottom));
    }

    public static Pair<Integer, Integer> getScreenSize(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;
        int height = size.y;
        return Pair.create(width, height);
    }
    //  <----- Pixels -----


    //  ------------------------- Text ------------------------->
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

    public static String removeTrailingZeros(String s) {
        return (s.contains(".") || s.contains(",")) ? s.replaceAll("0*$", "").replaceAll("[,.]$", "") : s;
    }

    public static Pair<Integer, Integer> getTextWithAndHeight(Context context, String text, int size, int... typefaces) {
        TextView textView = new TextView(context);
        textView.setTextSize(size);
        for (int typeface : typefaces)
            textView.setTypeface(textView.getTypeface(), typeface);
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.width();
        int height = bounds.height();
        return Pair.create(width, height);
    }

    public static String getEllipsedString(Context context, String text, int maxWidth_px, int size, int... typefaces) {
        TextView textView = new TextView(context);
        textView.setTextSize(size);
        for (int typeface : typefaces)
            textView.setTypeface(textView.getTypeface(), typeface);
        Paint textPaint = textView.getPaint();
        for (int i = 0; i < text.length(); i++) {
            Rect bounds = new Rect();
            String sub = CustomUtility.subString(text, 0, i == 0 ? text.length() : -i) + (i == 0 ? "" : "…");
            textPaint.getTextBounds(sub, 0, sub.length(), bounds);
            int width = bounds.width();
            if (width < maxWidth_px)
                return sub;
        }
        return "";
    }

    public static String subString(String text, int start, int ende) {
        if (start < 0)
            start = text.length() + start;
        if (ende < 0)
            ende = text.length() + ende;
        return text.substring(start, ende);
    }

    public static String subString(String text, int start) {
        if (start < 0)
            start = text.length() + start;
        return text.substring(start);
    }

    public static String stringReplace(String source, int start, int end, String replacement) {
        return source.substring(0, start) + replacement + source.substring(end);
    }

    public static String formatDuration(Duration duration, @Nullable String format) {
        if (format == null)
            format = "'%w% Woche§n§~, ~''%d% Tag§e§~, ~''%h% Stunde§n§~, ~''%m% Minute§n§~, ~''%s% Sekunde§n§~, ~'";
        com.finn.androidUtilities.CustomList<Pair<String, Integer>> patternList = new com.finn.androidUtilities.CustomList<>(Pair.create("%w%", 604800), Pair.create("%d%", 86400), Pair.create("%h%", 3600), Pair.create("%m%", 60), Pair.create("%s%", 1));
        int seconds = (int) (duration.toMillis() / 1000);
        while (true) {
            Matcher segments = Pattern.compile("'.+?'").matcher(format);
            if (!segments.find())
                break;
            String segment = segments.group();
            Iterator<Pair<String, Integer>> iterator = patternList.iterator();
            while (iterator.hasNext()) {
                Pair<String, Integer> pair = iterator.next();
                if (segment.contains(pair.first)) {
                    int amount = seconds / pair.second;
                    if (amount > 0) {
                        seconds = seconds % pair.second;
                        Matcher matcher = Pattern.compile(pair.first).matcher(segment);
                        String replacement = matcher.replaceFirst(String.valueOf(amount));
                        if (replacement.contains("§")) {
                            Matcher removePlural = Pattern.compile("§\\w+?§").matcher(replacement);
                            if (removePlural.find())
                                replacement = removePlural.replaceFirst(amount > 1 ? CustomUtility.subString(removePlural.group(), 1, -1) : "");
                        }
                        format = segments.replaceFirst(CustomUtility.subString(replacement, 1, -1));
                    } else
                        format = segments.replaceFirst("");

                    patternList.remove(pair);
                    break;
                }
            }
        }

        if (format.contains("~")) {
            while (true) {
                Matcher segments = Pattern.compile("~.+?~").matcher(format);
                if (!segments.find())
                    break;

                int start = segments.start();
                int end = segments.end();
                String replacement = CustomUtility.subString(segments.group(), 1, -1);

                format = CustomUtility.stringReplace(format, start, end, segments.find() ? replacement : "");
            }
        }

        return format;
    }
    //  <------------------------- Text -------------------------


    public static <T> Pair<T, T> swap(T t1, T t2) {
        T temp = t1;
        t1 = t2;
        t2 = temp;
        return new Pair<>(t1, t2);
    }


    //  --------------- Layout --------------->
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
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

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
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

    public static void changeHeight(final View v, ChangeLayout changeLayout) {
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

    public static void setDimensions(View view, boolean width, boolean height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(width ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT, height ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT));
            return;
        }
        if (width)
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        else
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (height)
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        else
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(lp);
    }
    //  <--------------- Layout ---------------


    //  --------------- getViews --------------->
    public static <T extends View> ArrayList<T> getViewsByType(ViewGroup root, Class<T> tClass) {
        return getViewsByType(root, tClass, false);
    }

    public static <T extends View> ArrayList<T> getViewsByType(ViewGroup root, Class<T> tClass, boolean exactly) {
        final ArrayList<T> result = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup)
                result.addAll(getViewsByType((ViewGroup) child, tClass, exactly));

            if (!exactly && tClass.isInstance(child))
                result.add(tClass.cast(child));
            else if (exactly && tClass.getName().equals(child.getClass().getName()))
                result.add(tClass.cast(child));
        }
        return result;
    }

    public static <T extends View> void applyToAllViews(ViewGroup root, Class<T> tClass, ApplyToAll<T> applyToAll) {
        getViewsByType(root, tClass).forEach(applyToAll::runApplyToAll);
    }

    public interface ApplyToAll<T extends View> {
        void runApplyToAll(T t);
    }

    public static <S extends View, T extends View> void replaceView(S oldView, T newView, @Nullable TransferState<S, T> transferState) {
        ViewGroup parent = (ViewGroup) oldView.getParent();
        int index = parent.indexOfChild(oldView);
        parent.removeView(oldView);
        parent.addView(newView, index);
        if (transferState != null)
            transferState.runTransferState(oldView, newView);
    }

    public static <S extends View, T extends View> void replaceView_children(S oldView, T newView, @Nullable TransferState<S, T> transferState) {
        replaceView(oldView, newView, transferState);
        if (oldView instanceof ViewGroup && newView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) oldView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                viewGroup.removeView(child);
                ((ViewGroup) newView).addView(child);
            }
        }
    }

    public interface TransferState<S, T> {
        void runTransferState(S source, T target);
    }
    //  <--------------- getViews ---------------


    //  --------------- SquareView --------------->
    enum EQUAL_MODE {
        HEIGHT, WIDTH, MAX, MIN
    }

    public static void squareView(View view) {
        squareView(view, EQUAL_MODE.MAX);
    }

    public static void squareView(View view, EQUAL_MODE equalMode) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        int height = view.getMeasuredHeight();

        int matchParentMeasureSpec_width = View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getHeight(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec_width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(wrapContentMeasureSpec_width, matchParentMeasureSpec_width);
        int width = view.getMeasuredWidth();
        switch (equalMode) {
            case WIDTH:
                layoutParams.height = width;
                break;
            case HEIGHT:
                layoutParams.width = height;
                break;
            case MIN:
                int min = width < height ? width : height;
                layoutParams.width = min;
                layoutParams.height = min;
                break;
            case MAX:
                int max = width > height ? width : height;
                layoutParams.width = max;
                layoutParams.height = max;
                break;
        }
    }
    //  <--------------- SquareView ---------------


    //  --------------- DrawableBuilder --------------->
    public static Drawable drawableBuilder_rectangle(int color, int corners, boolean ripple) {
        DrawableBuilder drawableBuilder = new DrawableBuilder()
                .rectangle()
                .solidColor(color)
                .cornerRadius(CustomUtility.dpToPx(corners));
        if (ripple) drawableBuilder
                .ripple()
                .rippleColor(0xF8868686);
        return drawableBuilder
                .build();
    }

    public static Drawable drawableBuilder_oval(int color) {
        return new DrawableBuilder()
                .oval()
                .solidColor(color)
                .build();
    }

    public static int setAlphaOfColor(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }
    //  <--------------- DrawableBuilder ---------------


    //  --------------- ConcatCollections --------------->
    public interface GetCollections<T, V> {
        Collection<V> runGetCollections(T t);
    }

    public static <T, V> List<V> concatenateCollections(Collection<T> tCollection, GetCollections<T, V> getCollections) {
        List<Collection<V>> collectionList = new ArrayList<>();
        tCollection.forEach(t -> collectionList.add(getCollections.runGetCollections(t)));
        return collectionList.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> List<T> concatenateCollections(Collection<Collection<T>> collections) {
        return collections.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> List<T> concatenateCollections(List<T>... collections) {
        return Arrays.stream(collections).flatMap(Collection::stream).collect(Collectors.toList());
    }
    //  <--------------- ConcatCollections ---------------


    //  ------------------------- ifNotNull ------------------------->
    public static <E> boolean ifNotNull(E e, ExecuteIfNotNull<E> executeIfNotNull) {
        if (e == null)
            return false;
        executeIfNotNull.runExecuteIfNotNull(e);
        return true;
    }

    public static <E> boolean ifNotNull(E e, ExecuteIfNotNull<E> executeIfNotNull, Runnable executeIfNull) {
        if (e == null) {
            executeIfNull.run();
            return false;
        }
        executeIfNotNull.runExecuteIfNotNull(e);
        return true;
    }

    public interface ExecuteIfNotNull<E> {
        void runExecuteIfNotNull(E e);
    }

    public static boolean ignoreNull(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static <T> T returnIfNull(T object, T returnIfNull) {
        return object != null ? object : returnIfNull;
    }
    //  <------------------------- ifNotNull -------------------------


    //  ------------------------- Reflections ------------------------->
    public static List<TextWatcher> removeTextListeners(TextView view) {
        List<TextWatcher> returnList = null;
        try {
            Field mListeners = TextView.class.getDeclaredField("mListeners");
            mListeners.setAccessible(true);
            returnList = (List<TextWatcher>) mListeners.get(view);
            mListeners.set(view, null);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        return returnList;
    }
    //  <------------------------- Reflections -------------------------


    //  ------------------------- EasyLogic ------------------------->
    public static class NoArgumentException extends RuntimeException {
        public static final String DEFAULT_MESSAGE = "Keine Argumente mitgegeben";

        public NoArgumentException(String message) {
            super(message);
        }
    }

    public static <T> boolean boolOr(GenericReturnInterface<T, Boolean> what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        for (T o : to) {
            if (what.run(o))
                return true;
        }
        return false;

    }

    public static <T> boolean boolOr(T what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        for (T o : to) {
            if (Objects.equals(what, o))
                return true;
        }
        return false;
    }

    public static <T> boolean boolXOr(GenericReturnInterface<T, Boolean> what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        boolean found = false;
        for (T o : to) {
            if (what.run(o)) {
                if (found)
                    return false;
                found = true;
            }
        }
        return found;
    }

    public static <T> boolean boolXOr(T what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        boolean found = false;
        for (T o : to) {
            if (Objects.equals(what, o)) {
                if (found)
                    return false;
                found = true;
            }
        }
        return found;
    }

    public static <T> boolean boolAnd(T what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        for (T o : to) {
            if (!Objects.equals(what, o))
                return false;
        }
        return true;
    }

    public static <T> boolean boolAnd(GenericReturnInterface<T, Boolean> what, T... to) {
        if (to.length == 0)
            throw new NoArgumentException(NoArgumentException.DEFAULT_MESSAGE);

        for (T o : to) {
            if (!what.run(o))
                return false;
        }
        return true;
    }

    // ---

    public static boolean stringExists(CharSequence s) {
        return s != null && !s.toString().trim().isEmpty();
    }

    public static <T extends CharSequence> T stringExistsOrElse(T s, T orElse) {
        return stringExists(s) ? s : orElse;
    }

    public static <T> T isNotNullOrElse(T input, T orElse) {
        return input != null ? input : orElse;
    }

    public static <T> T isNotNullOrElse(T input, GenericReturnOnlyInterface<T> orElse) {
        return input != null ? input : orElse.run();
    }

    public static <T> T isNotValueOrElse(T input, T value, T orElse) {
        return !Objects.equals(input, value) ? input : orElse;
    }

    public static <T> T isNotValueOrElse(T input, T value, GenericReturnInterface<T, T> orElse) {
        return !Objects.equals(input, value) ? input : orElse.run(input);
    }

    public static <T, R> R isNotValueReturnOrElse(T input, T value, R returnValue, R orElse) {
        return !Objects.equals(input, value) ? returnValue : orElse;
    }

    public static <T, R> R isNotValueReturnOrElse(T input, T value, GenericReturnInterface<T, R> returnValue, GenericReturnInterface<T, R> orElse) {
        return !Objects.equals(input, value) ? returnValue.run(input) : orElse.run(input);
    }

    public static <T> T isValueOrElse(T input, T value, T orElse) {
        return Objects.equals(input, value) ? input : orElse;
    }

    public static <T> T isValueOrElse(T input, T value, GenericReturnInterface<T, T> orElse) {
        return Objects.equals(input, value) ? input : orElse.run(input);
    }

    public static <T, R> R isValueReturnOrElse(T input, T value, R returnValue, R orElse) {
        return Objects.equals(input, value) ? returnValue : orElse;
    }

    public static <T, R> R isValueReturnOrElse(T input, T value, GenericReturnInterface<T, R> returnValue, GenericReturnInterface<T, R> orElse) {
        return Objects.equals(input, value) ? returnValue.run(input) : orElse.run(input);
    }

    public static <T, R> R isNullReturnOrElse(T input, R returnValue, GenericReturnInterface<T, R> orElse) {
        return Objects.equals(input, null) ? returnValue : orElse.run(input);
    }

    public static <T, R> R isCheckReturnOrElse(T input, GenericReturnInterface<T, Boolean> check, @Nullable GenericReturnInterface<T, R> returnValue, GenericReturnInterface<T, R> orElse) {
        if (check.run(input)) {
            if (returnValue == null)
                return (R) input;
            else
                return returnValue.run(input);
        } else
            return orElse.run(input);
    }
    //  <------------------------- EasyLogic -------------------------


    //  ------------------------- Switch Expression ------------------------->
    public static class SwitchExpression<Input, Output> {
        private Input input;
        private CustomList<Pair<Input, Object>> caseList = new CustomList<>();
        private Object defaultCase;

        public SwitchExpression(Input input) {
            this.input = input;
        }

        public static <Input> SwitchExpression<Input, Object> setInput(Input input) {
            return new SwitchExpression<>(input);
        }

        //  ------------------------- Getters & Setters ------------------------->
        public Input getInput() {
            return input;
        }
        //  <------------------------- Getters & Setters -------------------------


        //  ------------------------- Cases ------------------------->
        public <Type> SwitchExpression<Input, Type> addCase(Input inputCase, ExecuteOnCase<Input, Type> executeOnCase) {
            caseList.add(new Pair<>(inputCase, executeOnCase));
            return (SwitchExpression<Input, Type>) this;
        }

        public <Type> SwitchExpression<Input, Type> addCase(Input inputCase, Type returnOnCase) {
            caseList.add(new Pair<>(inputCase, returnOnCase));
            return (SwitchExpression<Input, Type>) this;
        }

        public SwitchExpression<Input, Output> addCaseToLastCase(Input inputCase) {
            caseList.add(new Pair<>(inputCase, caseList.getLast().second));
            return this;
        }

        // ---------------

        public <Type> SwitchExpression<Input, Type> setDefault(ExecuteOnCase<Input, Type> defaultCase) {
            this.defaultCase = defaultCase;
            return (SwitchExpression<Input, Type>) this;
        }

        public <Type> SwitchExpression<Input, Type> setDefault(Type defaultCase) {
            this.defaultCase = defaultCase;
            return (SwitchExpression<Input, Type>) this;
        }

        public SwitchExpression<Input, Output> setLastCaseAsDefault() {
            defaultCase = caseList.getLast().second;
            return this;
        }

        // ---------------

        public interface ExecuteOnCase<Input, Output> {
            Output runExecuteOnCase(Input input);
        }
        //  <------------------------- Cases -------------------------


        public Output evaluate() {
            Optional<Pair<Input, Object>> optional = caseList.stream().filter(inputExecuteOnCasePair -> Objects.equals(input, inputExecuteOnCasePair.first)).findFirst();

            if (optional.isPresent()) {
                Object o = optional.get().second;
                if (o instanceof ExecuteOnCase)
                    return (Output) ((ExecuteOnCase) o).runExecuteOnCase(input);
                else
                    return (Output) o;
            } else if (defaultCase != null) {
                if (defaultCase instanceof ExecuteOnCase)
                    return (Output) ((ExecuteOnCase) defaultCase).runExecuteOnCase(input);
                else
                    return (Output) defaultCase;
            } else {
                return null;
            }
        }
    }
    //  <------------------------- Switch Expression -------------------------


    //  ------------------------- Interfaces ------------------------->
    public interface GenericInterface<T> {
        void run(T t);
    }

    public interface GenericReturnInterface<T, R> {
        R run(T t);
    }

    public interface DoubleGenericInterface<T, T2> {
        void run(T t, T2 t2);
    }

    public interface DoubleGenericReturnInterface<T, T2, R> {
        R run(T t, T2 t2);
    }

    public interface TripleGenericInterface<T, T2, T3> {
        void run(T t, T2 t2, T3 t3);
    }

    public interface TripleGenericReturnInterface<T, T2, T3, R> {
        R run(T t, T2 t2, T3 t3);
    }

    public interface GenericReturnOnlyInterface<T> {
        T run();
    }

    public static <T> boolean runGenericInterface(GenericInterface<T> genericInterface, T parameter) {
        if (genericInterface != null) {
            genericInterface.run(parameter);
            return true;
        }
        return false;
    }

    public static <T,T2> boolean runDoubleGenericInterface(DoubleGenericInterface<T,T2> genericInterface, T parameter, T2 parameter2) {
        if (genericInterface != null) {
            genericInterface.run(parameter, parameter2);
            return true;
        }
        return false;
    }

    public static <T,T2, T3> boolean runTripleGenericInterface(TripleGenericInterface<T,T2, T3> genericInterface, T parameter, T2 parameter2, T3 parameter3) {
        if (genericInterface != null) {
            genericInterface.run(parameter, parameter2, parameter3);
            return true;
        }
        return false;
    }

    public static boolean runRunnable(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
            return true;
        }
        return false;
    }

    public static boolean runVarArgRunnable(int index, Runnable... varArg){
        if (varArg != null && index >= 0) {
            if (varArg.length > index && varArg[index] != null)
                varArg[index].run();
            else
                return false;
        } else
            return false;
        return true;
    }

    public static <T> boolean runVarArgGenericInterface(int index, T input, GenericInterface<T>... varArg){
        if (varArg != null && index >= 0) {
            if (varArg.length > index && varArg[index] != null)
                varArg[index].run(input);
            else
                return false;
        } else
            return false;
        return true;
    }

    // --------------- Recursion

    public interface RecursiveGenericInterface<T> {
        void run(T t, RecursiveGenericInterface<T> recursiveInterface);
    }

    public static <T> void runRecursiveGenericInterface(T t, RecursiveGenericInterface<T> recursiveInterface) {
        recursiveInterface.run(t, recursiveInterface);
    }

    public interface RecursiveGenericReturnInterface<T,R> {
        R run(T t, RecursiveGenericReturnInterface<T, R> recursiveInterface);
    }

    public static <T, R> R runRecursiveGenericReturnInterface(T t, Class<R> returnType, RecursiveGenericReturnInterface<T, R> recursiveInterface) {
        return recursiveInterface.run(t, recursiveInterface);
    }

    //  <------------------------- Interfaces -------------------------


    //  ------------------------- ImageView ------------------------->
    public static void loadUrlIntoImageView(Context context, ImageView imageView, String imagePath, @Nullable String fullScreenPath, Runnable... onFail_onSuccess_onFullscreen) {
        if (imagePath.endsWith(".svg")) {
            CustomUtility.fetchSvg(context, imagePath, imageView, onFail_onSuccess_onFullscreen);
        } else {
            Glide
                    .with(context)
                    .load(imagePath)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (onFail_onSuccess_onFullscreen.length > 0 && onFail_onSuccess_onFullscreen[0] != null)
                                onFail_onSuccess_onFullscreen[0].run();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            if (onFail_onSuccess_onFullscreen.length > 1 && onFail_onSuccess_onFullscreen[1] != null)
//                                onFail_onSuccess_onFullscreen[1].run();

                            return false;
                        }

                    })
                    .error(com.finn.androidUtilities.R.drawable.ic_broken_image)
                    .placeholder(com.finn.androidUtilities.R.drawable.ic_download)
                    .into(new DrawableImageViewTarget(imageView) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            if (resource == null)
                                return;
                            super.setResource(resource);
                            if (onFail_onSuccess_onFullscreen.length > 1 && onFail_onSuccess_onFullscreen[1] != null)
                                onFail_onSuccess_onFullscreen[1].run();

                        }
                    });
//                    .into(imageView);
        }
        if (fullScreenPath == null)
            return;
        imageView.setOnClickListener(v -> {
            if (onFail_onSuccess_onFullscreen.length > 2 && onFail_onSuccess_onFullscreen[2] != null)
                onFail_onSuccess_onFullscreen[2].run();
            CustomDialog.Builder(context)
                    .setView(com.finn.androidUtilities.R.layout.dialog_poster)
                    .setSetViewContent((customDialog1, view1, reload1) -> {
                        ImageView dialog_poster_poster = view1.findViewById(com.finn.androidUtilities.R.id.dialog_poster_poster);
                        if (fullScreenPath.endsWith(".png") || fullScreenPath.endsWith(".svg"))
                            dialog_poster_poster.setPadding(0, 0, 0, 0);

                        if (fullScreenPath.endsWith(".svg")) {
                            CustomUtility.fetchSvg(context, fullScreenPath, dialog_poster_poster, onFail_onSuccess_onFullscreen);
                        } else {
                            Glide
                                    .with(context)
                                    .load(fullScreenPath)
                                    .error(com.finn.androidUtilities.R.drawable.ic_broken_image)
                                    .placeholder(com.finn.androidUtilities.R.drawable.ic_download)
                                    .into(dialog_poster_poster);
                        }
                        dialog_poster_poster.setOnContextClickListener(v1 -> {
                            customDialog1.dismiss();
                            return true;
                        });

                    })
                    .addOptionalModifications(customDialog -> {
                        if (!(fullScreenPath.endsWith(".png") || fullScreenPath.endsWith(".svg")))
                            customDialog.removeBackground_and_margin();
                    })
                    .disableScroll()
                    .show();
        });
    }

    private static OkHttpClient httpClient;

    public static void fetchSvg(Context context, String url, final ImageView target, Runnable... onFail_onSuccess) {
        if (httpClient == null) {
            // Use cache for performance and basic offline capability
            httpClient = new OkHttpClient.Builder()
                    .cache(new Cache(context.getCacheDir(), 5 * 1024 * 1014))
                    .build();
        }

        if (!url.startsWith("http"))
            url = "https://" + url;
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        Runnable runOnFailure = () -> {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    target.setImageResource(com.finn.androidUtilities.R.drawable.ic_broken_image);
                    if (onFail_onSuccess.length > 0 && onFail_onSuccess[0] != null) {
                        onFail_onSuccess[0].run();
                    }
                });
            }
        };

        Runnable runOnSuccess = () -> {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    if (onFail_onSuccess.length > 1 && onFail_onSuccess[1] != null) {
                        onFail_onSuccess[1].run();
                    }
                });
            }
        };

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnFailure.run();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream stream = response.body().byteStream();
                try {
                    Sharp.loadInputStream(stream).into(target);
                } catch (Exception e) {
                    runOnFailure.run();
                }
                stream.close();
                runOnSuccess.run();
            }
        });
    }

    // ---------------

    public static class ImageHelper {
        public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }

    public static void roundImageView(ImageView imageView, int dp) {
        int radius;
        if (dp == -1) {
            imageView.measure(0, 0);
            radius = Math.max(imageView.getMeasuredWidth(), imageView.getMeasuredHeight()) / 2;
        } else
            radius = dpToPx(dp);

//        Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        imageView.setDrawingCacheEnabled(true);
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView.layout(0, 0,
                imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        imageView.buildDrawingCache(true);
        Bitmap oldBitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(oldBitmap, radius));
    }
    //  <------------------------- ImageView -------------------------


    //  ------------------------- ExpendableToolbar ------------------------->
    public static Runnable applyExpendableToolbar_recycler(Context context, RecyclerView recycler, Toolbar toolbar, AppBarLayout appBarLayout, CollapsingToolbarLayout collapsingToolbarLayout, TextView noItem, String title) {
        final boolean[] canExpand = {true};
        int tolerance = 50;
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    canExpand[0] = recycler.computeVerticalScrollOffset() <= tolerance;
                    recycler.setNestedScrollingEnabled(canExpand[0]);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (canExpand[0] && recycler.computeVerticalScrollOffset() > tolerance) {
                    canExpand[0] = false;
                    recycler.setNestedScrollingEnabled(canExpand[0]);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        if (params.getBehavior() == null)
            params.setBehavior(new AppBarLayout.Behavior());
        AppBarLayout.Behavior behaviour = (AppBarLayout.Behavior) params.getBehavior();
        behaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return canExpand[0];
            }
        });

        return generateApplyToolBarTitle(context, toolbar, appBarLayout, collapsingToolbarLayout, noItem, title);
    }

    public static void applyExpendableToolbar_scrollView(Context context, NestedScrollView scrollView, AppBarLayout appBarLayout) {
        final boolean[] canExpand = {true};
        final boolean[] touched = {false};
        final boolean[] scrolled = {false};
        Runnable[] check = {() -> {
        }};
        NestedScrollView newScrollView = new NestedScrollView(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN)
                    touched[0] = true;
                else if (ev.getAction() == MotionEvent.ACTION_UP)
                    touched[0] = false;

                check[0].run();
                return super.dispatchTouchEvent(ev);
            }

        };
        CustomUtility.replaceView(scrollView, newScrollView, (source, target) -> {
            target.setId(source.getId());
            target.setLayoutParams(source.getLayoutParams());
            while (source.getChildCount() > 0) {
                View child = source.getChildAt(0);
                source.removeViewAt(0);
                target.addView(child);
            }
        });

        int tolerance = 50;
        check[0] = () -> {
            if (!canExpand[0] && !scrolled[0] && !touched[0]) {
                canExpand[0] = true;
                newScrollView.setNestedScrollingEnabled(canExpand[0]);
            } else if (canExpand[0] && scrolled[0] && touched[0]) {
                canExpand[0] = false;
                newScrollView.setNestedScrollingEnabled(canExpand[0]);
            }
        };

        newScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            scrolled[0] = scrollY > tolerance;
            check[0].run();
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        if (params.getBehavior() == null)
            params.setBehavior(new AppBarLayout.Behavior());
        AppBarLayout.Behavior behaviour = (AppBarLayout.Behavior) params.getBehavior();
        behaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return canExpand[0];
            }
        });
    }

    private static Runnable generateApplyToolBarTitle(Context context, Toolbar toolbar, AppBarLayout appBarLayout, CollapsingToolbarLayout collapsingToolbarLayout, TextView noItem, String title) {
        return () -> {
            final float[] maxOffset = {-1};
            float distance = noItem.getY() - appBarLayout.getBottom();
            int stepCount = 5;
            final int[] prevPart = {-1};

            List<String> ellipsedList = new ArrayList<>();

            appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
                if (maxOffset[0] == -1) {
                    maxOffset[0] = -appBarLayout.getTotalScrollRange();
                    int maxWidth = CustomUtility.getViewsByType(toolbar, ActionMenuView.class).get(0).getLeft() - CustomUtility.getViewsByType(toolbar, AppCompatImageButton.class).get(0).getRight(); //320
                    for (int i = 0; i <= stepCount; i++)
                        ellipsedList.add(CustomUtility.getEllipsedString(context, title, maxWidth - CustomUtility.dpToPx(3) - (int) (55 * ((stepCount - i) / (double) stepCount)), 18 + (int) (16 * (i / (double) stepCount))));
                }

                int part = stepCount - Math.round(verticalOffset / (maxOffset[0] / stepCount));
                if (part != prevPart[0])
                    collapsingToolbarLayout.setTitle(ellipsedList.get(prevPart[0] = part));

                float alpha = 1f - ((verticalOffset - maxOffset[0]) / distance);
                noItem.setAlpha(Math.max(alpha, 0f));
            });
        };
    }
    //  <------------------------- ExpendableToolbar -------------------------


    //  ------------------------- Arrays ------------------------->
    public static int getIndexByString(Context context, int arrayId, String language) {
        String[] array = context.getResources().getStringArray(arrayId);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(language))
                return i;
        }
        return 0;
    }

    public static String getStringByIndex(Context context, int arrayId, int index) {
        return context.getResources().getStringArray(arrayId)[index];
    }

    // --------------- VarArgs

    public static <T> boolean easyVarArgs(T[] varArg, int index, CustomUtility.GenericInterface<T> ifExists) {
        if (varArg.length > index) {
            T t;
            if ((t = varArg[index]) != null) {
                ifExists.run(t);
                return true;
            }
        }
        return false;
    }

    public static <T> T easyVarArgsOrElse(int index, @Nullable CustomUtility.GenericReturnOnlyInterface<T> orElse, T... varArg) {
        if (varArg != null) {
            if (varArg.length > index) {
                T t;
                if ((t = varArg[index]) != null)
                    return t;
            }
        }
        return orElse == null ? null : orElse.run();
    }
    //  <------------------------- Arrays -------------------------


    //  ------------------------- Maps ------------------------->
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
    //  <------------------------- Maps -------------------------
}
