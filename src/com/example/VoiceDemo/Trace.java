package com.example.VoiceDemo;


import android.util.Log;

public class Trace {


    public static void i(String s) {
//        if (BuildConfig.DEBUG) {
            Log.i("tag", s);
//        }
    }

}
