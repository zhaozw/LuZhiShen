package org.lvu;

import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.bugly.Bugly;

import org.lvu.customize.AuthImageDownloader;
import org.lvu.main.activities.ErrorActivity;
import org.lvu.main.activities.MainActivity;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import okhttp3.OkHttpClient;

/**
 * Created by wuyr on 3/31/16 6:17 PM.
 */
public class Application extends android.app.Application {

    private static Context mContext;
    private static OkHttpClient mClient;

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
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new AuthImageDownloader(this)).threadPoolSize(7).build());
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getCurrentSkin(){
        return mContext.getSharedPreferences(MainActivity.class.getName(),
                MODE_PRIVATE).getString("skin", "");
    }

    public static synchronized OkHttpClient getOkHttpClient() {
        if (mClient == null)
            mClient = new OkHttpClient();
        return mClient;
    }
}
