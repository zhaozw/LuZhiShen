package org.lvu.main.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.lvu.R;
import org.lvu.adapter.MenuListAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.customize.NavigationList;
import org.lvu.model.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 7/26/16 10:59 PM.
 */
@SuppressWarnings("ConstantConditions")
public class NavigationActivity extends BaseActivity {

    public static final String STRING_ID = "StringId";
    private View mBottomView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        initViews();
        initImmerse();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        NavigationList videoArea = (NavigationList) findViewById(R.id.area_video),
                pictureArea = (NavigationList) findViewById(R.id.area_picture),
                textArea = (NavigationList) findViewById(R.id.area_text);
        videoArea.setOnItemClickListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int stringId) {
                handleOnClick(stringId);
            }
        });
        pictureArea.setOnItemClickListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int stringId) {
                handleOnClick(stringId);
            }
        });
        textArea.setOnItemClickListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int stringId) {
                handleOnClick(stringId);
            }
        });
        videoArea.setData(initVideoAreaData());
        pictureArea.setData(initPictureAreaData());
        textArea.setData(initTextAreaData());
    }

    private void handleOnClick(int stringId) {
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        intent.putExtra(STRING_ID, stringId);
        startActivity(intent);
        finish();
    }

    public static int getPositionById(int stringId) {
        int position;
        switch (stringId) {
            case R.string.menu_china_video:
                position = 0;
                break;
            case R.string.menu_europe_video:
                position = 1;
                break;
            case R.string.menu_japan_video:
                position = 2;
                break;
            case R.string.menu_family_pic:
                position = 3;
                break;
            case R.string.menu_asia_pic:
                position = 4;
                break;
            case R.string.menu_europe_pic:
                position = 5;
                break;
            case R.string.menu_evil_pics:
                position = 6;
                break;
            case R.string.menu_gif:
                position = 7;
                break;
            case R.string.menu_excited_novel:
                position = 8;
                break;
            case R.string.menu_family_mess_novel:
                position = 9;
                break;
            case R.string.menu_school_novel:
                position = 10;
                break;
            case R.string.menu_lewd_wife_novel:
                position = 11;
                break;
            case R.string.menu_funny_joke:
                position = 12;
                break;
            default:
                position = -1;
                break;
        }
        return position;
    }

    private List<Menu> initVideoAreaData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_video, R.string.menu_china_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_europe_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_japan_video));
        return result;
    }

    private List<Menu> initPictureAreaData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_family_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_asia_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_europe_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_evil_pics));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_gif));
        return result;
    }

    private List<Menu> initTextAreaData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_book, R.string.menu_excited_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_family_mess_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_school_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_lewd_wife_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_funny_joke));
        return result;
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

    //上次按下返回键的时间
    private long mLastTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mLastTime) < 2000)
            finish();
        mLastTime = System.currentTimeMillis();
        MySnackBar.show(findViewById(R.id.coordinator),
                getString(R.string.press_back_exit), Snackbar.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.activity_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_skin:
                changeSkin();
                return true;
            default:
                break;
        }
        return false;
    }
}
