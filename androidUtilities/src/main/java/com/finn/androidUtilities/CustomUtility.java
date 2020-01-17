package com.finn.androidUtilities;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (amount % 1 == 0)
            return String.format(Locale.GERMANY, "%.0f €", amount);
        else
            return String.format(Locale.GERMANY, "%.2f €", amount);
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

    interface InterceptOnClick {
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
    //  <--------------- Toast ---------------

    public static class Triple<A, B, C> {
        public A first;
        public B second;
        public C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public static class Quadruple<A, B, C, D> {
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

    public static <T> Pair<T, T> swap(T t1, T t2) {
        T temp = t1;
        t1 = t2;
        t2 = temp;
        return new Pair<>(t1, t2);
    }

    //  --------------- Layout-Animation --------------->
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
    //  <--------------- Layout-Animation ---------------


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
    public static <E> boolean ifNotNull(E e, ExecuteIfNotNull<E> executeIfNotNull){
        if (e == null)
            return false;
        executeIfNotNull.runExecuteIfNotNull(e);
        return true;
    }

    public static <E> boolean ifNotNull(E e, ExecuteIfNotNull<E> executeIfNotNull, Runnable executeIfNull){
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
    //  <------------------------- ifNotNull -------------------------

    //  ------------------------- Reflections ------------------------->
    public static List<TextWatcher> removeTextListeners(TextView view){
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
    public static <T> Boolean boolOr(T what, T... to){
        if (to.length == 0)
            return null;

        for (Object o : to) {
            if (Objects.equals(what, o))
                return true;
        }
        return false;
    }

    public static <T> Boolean boolXOr(T what, T... to){
        if (to.length == 0)
            return null;

        boolean found = false;
        for (Object o : to) {
            if (Objects.equals(what, o)) {
                if (found)
                    return false;
                found = true;
            }
        }
        return found;
    }

    public static <T> Boolean boolAnd(T what, T... to){
        if (to.length == 0)
            return null;

        for (Object o : to) {
            if (!Objects.equals(what, o))
                return false;
        }
        return true;
    }

        // ---

    public static boolean stringExists(String s){
        return s != null && !s.isEmpty();
    }
    //  <------------------------- EasyLogic -------------------------

}
