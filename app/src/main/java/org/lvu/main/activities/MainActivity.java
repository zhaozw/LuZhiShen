package org.lvu.main.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapter.MenuListAdapter;
import org.lvu.adapter.SkinChooseAdapter;
import org.lvu.customize.MenuList;
import org.lvu.customize.MySnackBar;
import org.lvu.main.fragments.AsiaPictureFragment;
import org.lvu.main.fragments.ChinaVideoFragment;
import org.lvu.main.fragments.EuropePictureFragment;
import org.lvu.main.fragments.EuropeVideoFragment;
import org.lvu.main.fragments.EvilComicsFragment;
import org.lvu.main.fragments.ExcitedNovelFragment;
import org.lvu.main.fragments.FamilyMessNovelFragment;
import org.lvu.main.fragments.FamilyPhotoFragment;
import org.lvu.main.fragments.JapanVideoFragment;
import org.lvu.main.fragments.LewdWifeNovelFragment;
import org.lvu.main.fragments.NavigationFragment;
import org.lvu.main.fragments.SchoolNovelFragment;
import org.lvu.model.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.Vitamio;

/**
 * Created by wuyr on 6/1/16 3:33 PM.
 */

@SuppressWarnings("ConstantConditions")
public class MainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private View mTopView;
    private MenuList mMenuList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_main_view);
        initViews();
        initImmerse();
        initFragment();
    }

    private void initFragment() {
        showFragment(new NavigationFragment());
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.wait_replace, fragment).commitAllowingStateLoss();
    }

    public void hideToolbar() {
        mToolbar.setVisibility(View.GONE);
    }

    public void showToolbar() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    public void setDrawerLockMode(boolean lock) {
        mDrawerLayout.setDrawerLockMode(lock ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
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

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.menu_navigation);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mMenuList = (MenuList) findViewById(R.id.menu_list);
        mMenuList.setOnItemListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int stringId) {
                Fragment fragment;
                switch (stringId) {
                    case R.string.menu_navigation:
                        fragment = new NavigationFragment();
                        break;
                    case R.string.menu_china_video:
                        fragment = new ChinaVideoFragment();
                        break;
                    case R.string.menu_europe_video:
                        fragment = new EuropeVideoFragment();
                        break;
                    case R.string.menu_japan_video:
                        fragment = new JapanVideoFragment();
                        break;
                    case R.string.menu_family_pic:
                        fragment = new FamilyPhotoFragment();
                        break;
                    case R.string.menu_asia_pic:
                        fragment = new AsiaPictureFragment();
                        break;
                    case R.string.menu_europe_pic:
                        fragment = new EuropePictureFragment();
                        break;
                    case R.string.menu_evil_pics:
                        fragment = new EvilComicsFragment();
                        break;
                    case R.string.menu_gif:
                        fragment = new NavigationFragment();
                        break;
                    case R.string.menu_excited_novel:
                        fragment = new ExcitedNovelFragment();
                        break;
                    case R.string.menu_family_mess_novel:
                        fragment = new FamilyMessNovelFragment();
                        break;
                    case R.string.menu_school_novel:
                        fragment = new SchoolNovelFragment();
                        break;
                    case R.string.menu_lewd_wife_novel:
                        fragment = new LewdWifeNovelFragment();
                        break;
                    default:
                        fragment = null;
                        break;
                }
                if (fragment != null)
                    showFragment(fragment);
                mToolbar.setTitle(stringId);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.change_skin:
                        changeSkin();
                        break;
                    case R.id.exit:
                        exit();
                        break;
                    default:
                }
            }
        });
    }

    private AlertDialog skinDialog;

    private void changeSkin() {
        skinDialog = new AlertDialog.Builder(this, Application.getCurrentSkin()
                .equals(getString(R.string.skin_black)) ?
                R.style.CustomDialogTheme2 : R.style.CustomDialogTheme)
                .setTitle(R.string.choose_skin)
                .setAdapter(new SkinChooseAdapter(this, R.layout.menu_list_item, initData())
                        .setOnItemClickListener(new SkinChooseAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int stringId) {
                                String skin = getString(stringId);
                                getSharedPreferences(MainActivity.class.getName(),
                                        MODE_PRIVATE).edit().putString(SKIN, skin).apply();
                                skinDialog.dismiss();
                                recreate();
                            }
                        }), null).setNegativeButton(R.string.cancel, null).show();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private List<Menu> initData() {
        String curSkin = getSharedPreferences(MainActivity.class.getName(),
                MODE_PRIVATE).getString(SKIN, null);
        if (curSkin == null)
            curSkin = getString(R.string.skin_red);

        List<Menu> result = new ArrayList<>();
        if (!curSkin.equals(getString(R.string.skin_blue)))
            result.add(new Menu(R.color.bluePrimary, R.string.skin_blue));
        if (!curSkin.equals(getString(R.string.skin_red)))
            result.add(new Menu(R.color.redPrimary, R.string.skin_red));
        if (!curSkin.equals(getString(R.string.skin_purple)))
            result.add(new Menu(R.color.purplePrimary, R.string.skin_purple));
        if (!curSkin.equals(getString(R.string.skin_deepOrange)))
            result.add(new Menu(R.color.deepOrangePrimary, R.string.skin_deepOrange));
        if (!curSkin.equals(getString(R.string.skin_green)))
            result.add(new Menu(R.color.greenPrimary, R.string.skin_green));
        if (!curSkin.equals(getString(R.string.skin_brown)))
            result.add(new Menu(R.color.brownPrimary, R.string.skin_brown));
        if (!curSkin.equals(getString(R.string.skin_pink)))
            result.add(new Menu(R.color.pinkPrimary, R.string.skin_pink));
        if (!curSkin.equals(getString(R.string.skin_teal)))
            result.add(new Menu(R.color.tealPrimary, R.string.skin_teal));
        if (!curSkin.equals(getString(R.string.skin_grey)))
            result.add(new Menu(R.color.greyPrimary, R.string.skin_grey));
        if (!curSkin.equals(getString(R.string.skin_black)))
            result.add(new Menu(R.color.blackPrimary, R.string.skin_black));
        return result;
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

    private void changeToLandscape() {
        mTopView.setVisibility(View.GONE);
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        uiFlags |= 0x00001000;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        mMenuList.changeToLandscape();
    }

    private void changeToPortrait() {
        mTopView.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mMenuList.changeToPortrait();
    }

    private OnBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    //上次按下返回键的时间
    private long mLastTime;

    @Override
    public void onBackPressed() {
        if (!mOnBackPressedListener.onBackPressed()) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.closeDrawer(GravityCompat.START);
            else {
                if ((System.currentTimeMillis() - mLastTime) < 2000)
                    finish();
                mLastTime = System.currentTimeMillis();
                MySnackBar.show(findViewById(R.id.coordinator),
                        getString(R.string.press_back_exit), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void exit() {
        new AlertDialog.Builder(this, Application.getCurrentSkin()
                .equals(getString(R.string.skin_black)) ?
                R.style.CustomDialogTheme2 : R.style.CustomDialogTheme)
                .setMessage(R.string.confirm_exit)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton(R.string.no, null).show();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }
}
