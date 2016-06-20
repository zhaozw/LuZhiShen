package org.lvu;

import android.content.Context;

import org.lvu.main.activity.MainActivity;

/**
 * Created by wuyr on 3/31/16 6:17 PM.
 */
public class Application extends android.app.Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getCurrentSkin(){
        return mContext.getSharedPreferences(MainActivity.class.getName(),
                MODE_PRIVATE).getString("skin", "");
    }
}
