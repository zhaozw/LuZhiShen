package org.lvu.main.activities;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.MySnackBar;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 11:37 PM.
 */
@SuppressWarnings("ConstantConditions")
public class NovelViewActivity extends BaseActivity {

    private View mBottomView;
    private Toolbar mToolbar;
    protected CircleProgressBar mLoadMoreBar;
    private TextView mContent;
    protected Handler mHandler;
    private File dir = getExternalCacheDir();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_view);
        mHandler = new MyHandler(this);
        initViews();
        initImmerse();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = TextUtils.isEmpty(getIntent().getStringExtra(PicturesViewActivity.TITLE)) ?
                "全部内容" : getIntent().getStringExtra(PicturesViewActivity.TITLE);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContent = (TextView) findViewById(R.id.text_view);

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

        startSyncData();
    }

    protected void startSyncData() {
        final String url = getIntent().getStringExtra(PicturesViewActivity.URL);
        File bitmapFile = new File(dir, String.valueOf(url.hashCode()));
        if (bitmapFile.exists()) {
            BufferedReader br = null;
            try {
                StringBuilder content = new StringBuilder();
                String buff;
                br = new BufferedReader(new FileReader(bitmapFile));
                while ((buff = br.readLine()) != null)
                    content.append(buff).append("\n");
                mContent.setText(content);
            } catch (Exception e) {
                e.printStackTrace();
                MySnackBar.show(findViewById(R.id.root_view), getString(R.string.load_data_fail), Snackbar.LENGTH_SHORT);
            } finally {
                mLoadMoreBar.setVisibility(View.GONE);
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            HttpUtil.getNovelContentAsync(url, new HttpUtil.HttpRequestCallbackListener() {
                @Override
                public void onSuccess(List<Data> tmp, String textContent) {
                    new WriteDataThread(textContent, String.valueOf(url.hashCode())).start();
                    Message message = new Message();
                    message.obj = textContent;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onFailure(Exception e, String reason) {
                    if (reason.equals("无可用网络。\t(向右滑动清除)")) {
                        try {
                            mLoadMoreBar.setVisibility(View.GONE);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else
                        mLoadMoreBar.post(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadMoreBar();
                            }
                        });
                    MySnackBar.show(findViewById(R.id.root_view), reason, Snackbar.LENGTH_INDEFINITE,
                            getString(R.string.back), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                }
            });
        }
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

    private Animation hideAnimation;

    protected void hideLoadMoreBar() {
        if (hideAnimation == null)
            initHideAnimation();
        try {
            mLoadMoreBar.startAnimation(hideAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHideAnimation() {
        hideAnimation = new ScaleAnimation(
                1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideAnimation.setDuration(250);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
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
    }

    private void changeToLandscape() {
        mToolbar.setPadding(0, 0, 0, 0);
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
        mToolbar.setPadding(0, ImmerseUtil.getStatusBarHeight(this), 0, 0);
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
        if (mContent != null) {
            mContent.setText("");
            mContent = null;
            System.gc();
        }
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

    private static class MyHandler extends Handler {

        private WeakReference<NovelViewActivity> mClass;

        MyHandler(NovelViewActivity clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mClass.get().mContent != null) {
                mClass.get().mContent.setText((String) msg.obj);
                mClass.get().hideLoadMoreBar();
            } else {
                msg.obj = null;
                System.gc();
            }
        }
    }

    private class WriteDataThread extends Thread {

        private String data, name;

        WriteDataThread(String data, String name) {
            this.data = data;
            this.name = name;
        }

        @Override
        public void run() {
            FileWriter fw = null;
            try {
                if (data == null || data.isEmpty())
                    throw new IOException("data is empty");

                if (name == null || name.isEmpty())
                    throw new IOException("name is empty");

                fw = new FileWriter(new File(dir, name));
                fw.write(data);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
