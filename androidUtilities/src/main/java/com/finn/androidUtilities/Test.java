package com.finn.androidUtilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Test {
    
    public static void test(Context context){
        Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
    }



    public static void calcStart(int amount) {
        calc(amount, 0.9);
    }
    private static void calc(int index, double prev) {
        if (index == 0)
            return;

        double result = 0.5 + prev / 2;
        Log.d("Kopfschmerzen", "calc: " + result);
        calc(--index, result);
    }
}
