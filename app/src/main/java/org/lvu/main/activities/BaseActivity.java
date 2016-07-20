package org.lvu.main.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.lvu.R;

import java.util.HashMap;
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
}
