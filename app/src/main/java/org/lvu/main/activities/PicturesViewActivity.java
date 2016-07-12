package org.lvu.main.activities;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.PicturesViewAdapter;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.MySnackBar;
import org.lvu.model.Data;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 11:23 PM.
 */
public class PicturesViewActivity extends BaseActivity {

    public static final String URL = "url", TITLE = "title";
    private View mTopView;
    private CircleProgressBar mLoadMoreBar;
    private PicturesViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_view);
        initViews();
        initImmerse();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getIntent().getStringExtra(TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PicturesViewAdapter(this, R.layout.activity_pictures_item, new ArrayList<Data>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.syncData(getIntent().getStringExtra(URL));
        mAdapter.setOnSyncDataFinishListener(new BaseListAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }

            @Override
            public void onFailure() {
                hideLoadMoreBar();
                MySnackBar.show(findViewById(R.id.coordinator),
                        getString(R.string.get_data_failure), Snackbar.LENGTH_INDEFINITE);
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
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        uiFlags |= 0x00001000;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        mAdapter.changeToLandscape();
    }

    private void changeToPortrait() {
        mTopView.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mAdapter.changeToPortrait();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                System.gc();
                break;
            default:
        }
        return true;
    }
}