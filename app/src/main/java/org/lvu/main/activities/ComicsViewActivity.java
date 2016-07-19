package org.lvu.main.activities;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import org.lvu.R;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.MySnackBar;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 11:35 PM.
 */
public class ComicsViewActivity extends BaseActivity {

    private View mTopView, mBottomView;
    private CircleProgressBar mLoadMoreBar;
    private ImageView mContent;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comics_view);
        mHandler = new MyHandler(this);
        initViews();
        initImmerse();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getIntent().getStringExtra(PicturesViewActivity.TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContent = (ImageView) findViewById(R.id.image_view);

        HttpUtil.getComicsContentAsync(getIntent().getStringExtra(PicturesViewActivity.URL), new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                Message message = new Message();
                message.obj = data.get(0).getBitmap();
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                hideLoadMoreBar();
                MySnackBar.show(findViewById(R.id.coordinator), reason, Snackbar.LENGTH_INDEFINITE);
            }
        });

        mLoadMoreBar = (CircleProgressBar) findViewById(R.id.progressbar);
        if (ImmerseUtil.isHasNavigationBar(this)) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mLoadMoreBar.getLayoutParams();
            lp.bottomMargin += ImmerseUtil.getNavigationBarHeight(this);
            mLoadMoreBar.setLayoutParams(lp);
        }
        mLoadMoreBar.setColorSchemeResources(R.color.menu_text_color);
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorPrimary),
                getResources().getColor(R.color.bluePrimary));
        mLoadMoreBar.setBackgroundColor(color);
        a.recycle();
    }

    private void initImmerse() {
        if (ImmerseUtil.isAboveKITKAT()) {
            /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //init topView
            mTopView = findViewById(R.id.status_bar_view);
            AppBarLayout.LayoutParams topLP = new AppBarLayout.LayoutParams(
                    AppBarLayout.LayoutParams.MATCH_PARENT, ImmerseUtil.getStatusBarHeight(this));
            mTopView.setLayoutParams(topLP);
            if (ImmerseUtil.isHasNavigationBar(this)) {
                //init bottomView
                mBottomView = findViewById(R.id.navigation_bar_view);
                AppBarLayout.LayoutParams bottomLP = new AppBarLayout.LayoutParams(
                        AppBarLayout.LayoutParams.MATCH_PARENT, ImmerseUtil.getNavigationBarHeight(this));
                mBottomView.setLayoutParams(bottomLP);
            }
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE)
                changeToLandscape();
            else changeToPortrait();
        }
    }

    private void hideLoadMoreBar() {
        ScaleAnimation animation = new ScaleAnimation(
                1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadMoreBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadMoreBar.startAnimation(animation);
    }

    private void changeToLandscape() {
        mTopView.setVisibility(View.GONE);
        if (mBottomView != null)
            mBottomView.setVisibility(View.GONE);
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        uiFlags |= 0x00001000;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    private void changeToPortrait() {
        mTopView.setVisibility(View.VISIBLE);
        if (mBottomView != null)
            mBottomView.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ImmerseUtil.isAboveKITKAT()) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                changeToLandscape();
            else
                changeToPortrait();
        }
    }

    @Override
    public void onBackPressed() {
        releaseImageViewResource(mContent);
        mContent = null;
        System.gc();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    public void releaseImageViewResource(ImageView imageView) {
        if (imageView == null) return;
        try {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    imageView.setImageBitmap(null);
                    bitmap.recycle();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<ComicsViewActivity> mClass;

        public MyHandler(ComicsViewActivity clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mClass.get().mContent != null) {
                mClass.get().mContent.setImageBitmap((Bitmap) msg.obj);
                mClass.get().hideLoadMoreBar();
            } else {
                msg.obj = null;
                System.gc();
            }
        }
    }
}
