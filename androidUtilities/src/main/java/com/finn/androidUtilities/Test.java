package com.finn.androidUtilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IntDef;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

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


    //  ------------------------- IntDef ------------------------->
    public static final int MODE1 = 1;
    public static final int MODE2 = 2;
    public static final int MODE3 = 3;

    @IntDef({MODE1,MODE2,MODE3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {}

    public static void showMode(Context context, @Mode int mode){
        Toast.makeText(context, String.valueOf(mode), Toast.LENGTH_SHORT).show();
    }
    //  <------------------------- IntDef -------------------------


//    public static void jsonBuilder(){
//        String text = "{\"software\":\"Grammar.com v2\",\"warnings\":{\"incompleteResults\":false},\"language\":{\"name\":\"German (Germany)\",\"code\":\"de-DE\",\"detectedLanguage\":{\"name\":\"German (Germany)\",\"code\":\"de-DE\",\"confidence\":0.99}},\"matches\":[{\"message\":\"M\\u00f6glicher Rechtschreibfehler gefunden\",\"shortMessage\":\"Rechtschreibfehler\",\"replacements\":[{\"value\":\"Grundelement\"},{\"value\":\"Grundelemente\"},{\"value\":\"Gr\\u00fcnelement\"},{\"value\":\"Grundelements\"},{\"value\":\"Rundelement\"},{\"value\":\"Grundelementen\"},{\"value\":\"Brandelement\"},{\"value\":\"Grenzelement\"},{\"value\":\"Frontelement\"},{\"value\":\"Gr\\u00fcnderelement\"},{\"value\":\"Brustelement\"},{\"value\":\"Buntelement\"},{\"value\":\"Gru\\u00dfelement\"},{\"value\":\"Gurtelement\"},{\"value\":\"Granatelement\"},{\"value\":\"Gr\\u00fcnteeelement\"},{\"value\":\"Gr\\u00fcntonelement\"},{\"value\":\"Brutelement\"},{\"value\":\"Grund\\u00f6lelement\"},{\"value\":\"Soundelement\"}],\"offset\":14,\"length\":12,\"context\":{\"text\":\"Lesen ist ein Gruntelement unserer Kultur und auch unseres Alltqag...\",\"offset\":14,\"length\":12},\"sentence\":\"Lesen ist ein Gruntelement unserer Kultur und auch unseres Alltqags.\",\"type\":{\"typeName\":\"UnknownWord\"},\"rule\":{\"id\":\"GERMAN_SPELLER_RULE\",\"description\":\"M\\u00f6glicher Rechtschreibfehler\",\"issueType\":\"misspelling\",\"category\":{\"id\":\"TYPOS\",\"name\":\"M\\u00f6gliche Tippfehler\"}},\"ignoreForIncompleteSentence\":false,\"contextForSureMatch\":0},{\"message\":\"M\\u00f6glicher Rechtschreibfehler gefunden\",\"shortMessage\":\"Rechtschreibfehler\",\"replacements\":[{\"value\":\"Alltags\"},{\"value\":\"Alltag\"},{\"value\":\"Alltags-\"},{\"value\":\"Alltages\"},{\"value\":\"All-tags\"}],\"offset\":59,\"length\":8,\"context\":{\"text\":\"...element unserer Kultur und auch unseres Alltqags.\",\"offset\":43,\"length\":8},\"sentence\":\"Lesen ist ein Gruntelement unserer Kultur und auch unseres Alltqags.\",\"type\":{\"typeName\":\"UnknownWord\"},\"rule\":{\"id\":\"GERMAN_SPELLER_RULE\",\"description\":\"M\\u00f6glicher Rechtschreibfehler\",\"issueType\":\"misspelling\",\"category\":{\"id\":\"TYPOS\",\"name\":\"M\\u00f6gliche Tippfehler\"}},\"ignoreForIncompleteSentence\":false,\"contextForSureMatch\":0}]}";
//        String replaceAll = text.replaceAll("\"", "\"\"");
//        String BREAKPOINT = null;
//    }
//    public static void arrayBuilder(){
//        String result = "[";
//        String text = "␣\t!\t\"\t#\t$\t%\t&\t'\t(\t)\t*\t+\t,\t-\t.\t/\t:\t;\t<\t=\t>\t?\t@\t[\t\\\t]\t{\t|\t}\n" +
//                "%20\t%21\t%22\t%23\t%24\t%25\t%26\t%27\t%28\t%29\t%2A\t%2B\t%2C\t%2D\t%2E\t%2F\t%3A\t%3B\t%3C\t%3D\t%3E\t%3F\t%40\t%5B\t%5C\t%5D\t%7B\t%7C\t%7D\n";
//
//        String[] from = text.split("\n")[0].split("\t");
//        String[] to = text.split("\n")[1].split("\t");
//        List<String> pairs = new ArrayList<>();
//        for (int i = 0; i < from.length; i++) {
//            pairs.add(String.format("[\"%s\",\"%s\"]", from[i], to[i]));
//        }
//
//        result += String.join(",", pairs) + "]";
//        String BREAKPOINT = null;
//    }
}
