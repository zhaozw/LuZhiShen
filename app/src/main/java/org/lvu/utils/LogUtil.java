package org.lvu.utils;

import android.util.Log;

/**
 * Created by wuyr on 6/8/16 6:54 PM.
 */
public class LogUtil {
    public static void print(Object s) {
        if (s != null) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            Log.e(String.format("%s-->%s", element.getClassName(), element.getMethodName()), String.valueOf(s));
        }
    }
}