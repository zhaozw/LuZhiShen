package org.video_player;

import android.util.Log;

/**
 * Created by wuyr on 6/8/16 6:54 PM.
 */
class LogUtil {

    private static boolean isDebugOn = false;

    static void print(Object s) {
        if (isDebugOn)
            if (s != null) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                Log.i(String.format("%s-->%s", element.getClassName(), element.getMethodName()), String.valueOf(s));
            }
    }

    static void printf(String format, Object... args) {
        if (isDebugOn)
            if (format != null && args != null) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                Log.i(String.format("%s-->%s", element.getClassName(), element.getMethodName()), String.format(format, args));
            }
    }
}