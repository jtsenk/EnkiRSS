package com.jtsenkbeil.enki.enkirss.feature.util;

import android.util.Log;

public class Utils {

    public static void waitSecs(int secs) {
        //take seconds-to-wait as int parameter secs, then waits that long
        long startTime = System.currentTimeMillis();
        long runTime = 0;
        long endTime = (long)secs * 1000;
        while (runTime < endTime) {
            runTime = System.currentTimeMillis() - startTime;
        }
    }

    public static void logD(String key, String msg) {
        Log.d(key,msg);
    }


} //end Utils class
