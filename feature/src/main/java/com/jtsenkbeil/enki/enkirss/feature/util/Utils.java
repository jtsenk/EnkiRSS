package com.jtsenkbeil.enki.enkirss.feature.util;

import android.util.Log;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;

public class Utils {

    //static var for logging/debugging options
    public static boolean debugMode;

    public static void logD(String key, String msg) {
        if (debugMode) {
            Log.d(key, msg);
        }
    }

    public static void toastShort(String msg) {
        Toast.makeText(MainActivity.mainContext, msg,Toast.LENGTH_SHORT).show();
    }


    //from instructor's UtilTime class :JTS
    public static String secToTime(int time) {
        String timeStr = null;
        time = time/1000;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                minute = minute % 60;
                second = time - minute * 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    //from instructor's UtilTime class :JTS
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

} //end Utils class
