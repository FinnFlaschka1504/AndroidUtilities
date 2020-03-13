package com.finn.androidUtilities;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Listener:
 *  class *** implements CustomInternetHelper.InternetStateReceiverListener
 *
 *  @Override
 *  public void networkAvailable() {}
 *
 *  @Override
 *  public void networkUnavailable() {}
 *
 *
 *
 * Initialize:
 *  CustomInternetHelper.initialize(this);
 *
 *          -oder-
 *
 *  if ((customInternetHelper = CustomInternetHelper.getInstance()) == null)
 *      customInternetHelper = CustomInternetHelper.newInstance(this).addListener(this);
 *  else
 *      customInternetHelper.addListener(this);
 *
 *
 *
 * OnDestroy:
 *  customInternetHelper.removeListener(this);
 *
 *          -oder-
 *
 *  customInternetHelper.destroyInstance(this);
 */

public class CustomInternetHelper extends BroadcastReceiver {

    protected static Set<InternetStateReceiverListener> listeners = new HashSet<>();
    protected static Boolean connected;

    private static CustomInternetHelper customInternetHelper;


    //  --------------- Initialize & Instance --------------->
    public static CustomInternetHelper initialize(InternetStateReceiverListener listener) {
        if (getInstance() == null)
            customInternetHelper = newInstance((AppCompatActivity) listener).addListener(listener);
        else
            customInternetHelper.addListener(listener);
        if (listener instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) listener;
            appCompatActivity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {

                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

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
                    destroyInstance(appCompatActivity);
                    appCompatActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            });
        }
        return customInternetHelper;
    }

    public static CustomInternetHelper getInstance() {
        return customInternetHelper;
    }

    public static CustomInternetHelper newInstance(AppCompatActivity appCompatActivity) {
        customInternetHelper = new CustomInternetHelper();
        appCompatActivity.registerReceiver(customInternetHelper, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return customInternetHelper;
    }

    public static void destroyInstance(AppCompatActivity appCompatActivity) {
        if (appCompatActivity instanceof InternetStateReceiverListener)
            removeListener((InternetStateReceiverListener) appCompatActivity);

        appCompatActivity.unregisterReceiver(customInternetHelper);
        customInternetHelper = null;
    }
    //  <--------------- Initialize & Instance ---------------


    public CustomInternetHelper() {
        connected = null;
    }

    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }

        notifyStateToAll();
    }

    public static CustomDialog showActivateInternetDialog(AppCompatActivity context) {
        return CustomDialog.Builder(context)
                .setTitle("Internet Aktivieren")
                .setText("Was möchtest du aktivieren?")
                .addButton("Mobil", dialog -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                })
                .addButton("W-Lan", dialog -> {
                    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (wifi != null) {
                        wifi.setWifiEnabled(true);
                    }
                    Toast.makeText(context, "W-Lan aktiviert", Toast.LENGTH_SHORT).show();
                })
                .enableExpandButtons()
                .show();
    }

    //  --------------- Notify --------------->
    private void notifyStateToAll() {
        for(InternetStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(InternetStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }
    //  <--------------- Notify ---------------


    //  --------------- Listener --------------->
    public CustomInternetHelper addListener(InternetStateReceiverListener listener) {
        listeners.add(listener);
        notifyState(listener);
        return this;
    }

    public static void removeListener(InternetStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface InternetStateReceiverListener {
        public void networkAvailable();
        public void networkUnavailable();
    }
    //  <--------------- Listener ---------------
}