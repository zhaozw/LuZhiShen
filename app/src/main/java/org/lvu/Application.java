package org.lvu;

import android.content.Context;

import com.tencent.bugly.Bugly;

import org.lvu.main.activities.ErrorActivity;
import org.lvu.main.activities.MainActivity;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by wuyr on 3/31/16 6:17 PM.
 */
public class Application extends android.app.Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //Install CustomActivityOnCrash
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
        //Now initialize your error handlers as normal
        //i.e., ACRA.init(this);
        //or Fabric.with(this, new Crashlytics())
        Bugly.init(getApplicationContext(), "900037586", false);
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getCurrentSkin(){
        return mContext.getSharedPreferences(MainActivity.class.getName(),
                MODE_PRIVATE).getString("skin", "");
    }
}
