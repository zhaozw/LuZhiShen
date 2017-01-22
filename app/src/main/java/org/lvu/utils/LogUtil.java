package org.lvu.utils;

import android.util.Log;

/**
 * Created by wuyr on 6/8/16 6:54 PM.
 */
public class LogUtil {

    private static boolean isDebugOn = true;

    public static void print(Object s) {
        if (isDebugOn)
            if (s != null) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                Log.e(String.format("%s-->%s", element.getClassName(), element.getMethodName()), String.valueOf(s));
            }
    }

    public static void printf(String format, Object... args) {
        if (isDebugOn)
            if (format != null && args != null) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                Log.e(String.format("%s-->%s", element.getClassName(), element.getMethodName()), String.format(format, args));
            }
    }
}