package org.lvu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

/**
 * Created by wuyr on 2/21/16 4:00 PM.
 */
public class ImmerseUtil {

    private static final int DEFAULT_COLOR = Color.parseColor("#009688");
    private static final String STATUS_BAR_HEIGHT = "status_bar_height",
            NAVIGATION_BAT_HEIGHT = "navigation_bar_height",
            DIMEN = "dimen", ANDROID = "android";

    private ImmerseUtil() {
    }

    public static void setImmerseBar(Activity activity) {
        setImmerseBar(activity, DEFAULT_COLOR);
    }

    public static void setImmerseBar(Activity activity, int color) {
        /*activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        Window window = activity.getWindow();
        // Translucent status bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Translucent navigation bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //ViewGroup vg = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout vg = (LinearLayout) ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
        //statusBar
        View statusBar = new View(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        lp.gravity = Gravity.TOP;
        statusBar.setBackgroundColor(color);
        statusBar.setLayoutParams(lp);
        vg.addView(statusBar,0);
        //navigationBar
        if (isHasNavigationBar(activity)) {
            View navigationBar = new View(activity);
            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, getNavigationBarHeight(activity));
            lp2.gravity = Gravity.BOTTOM;
            navigationBar.setBackgroundColor(color);
            navigationBar.setLayoutParams(lp2);
            vg.addView(navigationBar);
            vg.invalidate();

        }
    }

    public static boolean isHasNavigationBar(Context context) {
        boolean isHasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", ANDROID);
        if (id > 0)
            isHasNavigationBar = rs.getBoolean(id);
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                isHasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                isHasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.w(ImmerseUtil.class.getSimpleName(), e.toString(), e);
        }
        return isHasNavigationBar;
    }

    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier(NAVIGATION_BAT_HEIGHT, DIMEN, ANDROID);
        if (id > 0 && isHasNavigationBar(context))
            navigationBarHeight = rs.getDimensionPixelSize(id);
        return navigationBarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(STATUS_BAR_HEIGHT, DIMEN, ANDROID);
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isAboveKITKAT(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
