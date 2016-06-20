package org.lvu.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.lvu.R;
import org.lvu.customize.VideoPlayer;

import io.vov.vitamio.Vitamio;

/**
 * Created by wuyr on 6/19/16 1:05 PM.
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_test_view);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setVisibility(View.GONE);
                VideoPlayer player = (VideoPlayer) findViewById(R.id.vp);
                player.setActivity(TestActivity.this);
                player.setUrlPlay("http://www.bilibilijj.com/FreeDown/DownLoad/1466448961/mp4/2601561.D9B66800F4746A6C293D905EF77BBBEE");
                player.setOnScreenOrientationChangedListener(new VideoPlayer.OnScreenOrientationChangedListener() {
                    @Override
                    public void onChangedToPortrait() {
                        v.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onChangeToLandscape() {
                    }
                });
            }
        });
    }
}
