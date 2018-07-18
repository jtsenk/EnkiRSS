package com.jtsenkbeil.enki.enkirss.feature.util;

import android.util.Log;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;

public class Utils {

    public static void logD(String key, String msg) {
        Log.d(key,msg);
    }

    public static void toastShort(String msg) {
        Toast.makeText(MainActivity.mainContext, msg,Toast.LENGTH_SHORT).show();
    }

} //end Utils class
