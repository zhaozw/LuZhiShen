package org.lvu.main.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapter.MenuListAdapter;
import org.lvu.adapter.MenuListAdapter2;
import org.lvu.customize.MenuList;
import org.lvu.customize.MySnackBar;
import org.lvu.main.fragments.ListFragment;
import org.lvu.model.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.Vitamio;

/**
 * Created by wuyr on 6/1/16 3:33 PM.
 */
public class MainActivity extends AppCompatActivity {
    private final String SKIN = "skin";
    //fragment tag
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private View mTopView;
    private MenuList mMenuList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        initSkins();
        setContentView(R.layout.activity_home);
        initViews();
        initImmerse();
        initFragments();
    }

    private void initFragments() {
        ListFragment nav = new ListFragment();
        Bundle navB = new Bundle();
        navB.putInt(ListFragment.LIST_TYPE, ListFragment.NAVIGATION);
        nav.setArguments(navB);
        showFragment(nav);
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
                ListFragment fragment = new ListFragment();
                Bundle bundle = new Bundle();
                switch (stringId) {
                    case R.string.menu_navigation:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.NAVIGATION);
                        break;
                    case R.string.menu_china_video:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.CHINA_V);
                        break;
                    case R.string.menu_europe_video:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.EUROPE_V);
                        break;
                    case R.string.menu_japan_video:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.JAPAN_V);
                        break;
                    case R.string.menu_family_take_pic:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.FAMILY_P);
                        break;
                    case R.string.menu_asia_pic:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.ASIA_P);
                        break;
                    case R.string.menu_europe_pic:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.EUROPE_P);
                        break;
                    case R.string.menu_evil_pics:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.EVIL_P);
                        break;
                    case R.string.menu_japan_pics:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.JAPAN_P);
                        break;
                    case R.string.menu_gif:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.GIF);
                        break;
                    case R.string.menu_excited_novel:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.EXCITED_N);
                        break;
                    case R.string.menu_family_mess_novel:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.FAMILY_N);
                        break;
                    case R.string.menu_lewd_wife_novel:
                        bundle.putInt(ListFragment.LIST_TYPE, ListFragment.WIFE_N);
                        break;
                    default:
                }
                fragment.setArguments(bundle);
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
                .setAdapter(new MenuListAdapter2(this, R.layout.menu_list_item, initData())
                        .setOnItemClickListener(new MenuListAdapter2.OnItemClickListener() {
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
        ;
        if (!curSkin.equals(getString(R.string.skin_black)))
            result.add(new Menu(R.color.blackPrimary, R.string.skin_black));
        return result;
    }

    private void initSkins() {
        SharedPreferences preferences = getSharedPreferences(
                MainActivity.class.getName(), MODE_PRIVATE);
        String curSkin = preferences.getString(SKIN, "");
        if (curSkin.isEmpty()) {
            curSkin = getString(R.string.skin_red);
            preferences.edit().putString(SKIN, curSkin).apply();
        }
        try {
            setTheme(getSkin(curSkin));
        } catch (Exception e) {
            e.printStackTrace();
            curSkin = getString(R.string.skin_red);
            preferences.edit().putString(SKIN, curSkin).apply();
            setTheme(getSkin(curSkin));
        }
    }

    private int getSkin(String name) {
        Map<String, Integer> data = new HashMap<>();
        data.put(getString(R.string.skin_blue), R.style.AppTheme_Blue);
        data.put(getString(R.string.skin_red), R.style.AppTheme_Red);
        data.put(getString(R.string.skin_purple), R.style.AppTheme_Purple);
        data.put(getString(R.string.skin_deepOrange), R.style.AppTheme_Deep_Orange);
        data.put(getString(R.string.skin_green), R.style.AppTheme_Green);
        data.put(getString(R.string.skin_brown), R.style.AppTheme_Brown);
        data.put(getString(R.string.skin_pink), R.style.AppTheme_Pink);
        data.put(getString(R.string.skin_teal), R.style.AppTheme_Teal);
        data.put(getString(R.string.skin_grey), R.style.AppTheme_Grey);
        data.put(getString(R.string.skin_black), R.style.AppTheme_Black);
        return data.get(name);
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
        if (mOnBackPressedListener != null) {
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
