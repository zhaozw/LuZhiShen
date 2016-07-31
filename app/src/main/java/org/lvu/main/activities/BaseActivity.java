package org.lvu.main.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import org.lvu.R;
import org.lvu.adapter.SkinChooseAdapter;
import org.lvu.model.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuyr on 6/23/16 11:22 PM.
 */
public class BaseActivity extends AppCompatActivity {

    protected final String SKIN = "skin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSkins();
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

    protected int getSkin(String name) {
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
        data.put(getString(R.string.skin_black), R.style.AppTheme_Dark);
        return data.get(name);
    }



    private AlertDialog skinDialog;
    private List<Menu> skinData;

    protected void changeSkin() {
        if (skinData == null)
            skinData = getSkinData();
        if (skinDialog == null) {
            skinDialog = new AlertDialog.Builder(this).setTitle(R.string.choose_skin)
                    .setAdapter(new SkinChooseAdapter(this, R.layout.menu_list_item, skinData)
                        /*.setOnItemClickListener(new SkinChooseAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int stringId) {
                                String skin = getString(stringId);
                                getSharedPreferences(MainActivity.class.getName(),
                                        MODE_PRIVATE).edit().putString(SKIN, skin).apply();
                                skinDialog.dismiss();
                                recreate();
                            }
                        })*/, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String skin = getString(skinData.get(which).getNameId());
                                    getSharedPreferences(MainActivity.class.getName(),
                                            MODE_PRIVATE).edit().putString(SKIN, skin).apply();
                                    skinDialog.dismiss();
                                    recreate();
                                }
                            }).setNegativeButton(R.string.cancel, null).create();
        }
        if (skinDialog.isShowing())
            return;
        skinDialog.show();
    }

    private List<Menu> getSkinData() {
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
            result.add(new Menu(R.color.darkPrimary, R.string.skin_black));
        return result;
    }

    private float mDownX, mDownY, mCurrentX, mCurrentY;
    private boolean mRightSlideFlag, isFlagChanged;

    protected void enableSwipeBack(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mRightSlideFlag = false;
                        isFlagChanged = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX = event.getX();
                        mCurrentY = event.getY();
                        if (isRightSlide() && !isFlagChanged) {
                            mRightSlideFlag = true;
                            isFlagChanged = true;
                        } else if (!isRightSlide() && mRightSlideFlag)
                            mRightSlideFlag = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isRightSlide() && mRightSlideFlag)
                            onBackPressed();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private boolean isRightSlide() {
        return mCurrentX - mDownX > 160 && mCurrentY - mDownY < 25 && mCurrentY - mDownY > -25;
    }
}
