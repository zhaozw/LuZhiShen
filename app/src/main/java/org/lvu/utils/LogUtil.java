package org.lvu.utils;

import android.util.Log;

/**
 * Created by wuyr on 6/8/16 6:54 PM.
 */
public class LogUtil {
    public static void print(Object s) {
        if (s != null)
            Log.e("LogUtil",String.valueOf(s));
    }
}