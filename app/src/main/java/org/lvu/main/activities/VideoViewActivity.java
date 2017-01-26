package org.lvu.main.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapters.SubAdapters.video.VideoViewAdapter;
import org.lvu.models.Data;
import org.video_player.PlayManager;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by wuyr on 1/25/17 8:42 PM.
 */

public class VideoViewActivity extends PicturesViewActivity {

    @Override
    public void onBackPressed() {
        PlayManager.getInstance().release();
        super.onBackPressed();
    }

    @Override
    void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new VideoViewAdapter(this, R.layout.adapter_video_item, new ArrayList<Data>());
        ((VideoViewAdapter) mAdapter).setTitle(mToolbar.getTitle().toString());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(mAdapter);
        animationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(animationAdapter);
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                if (((VideoViewAdapter.ViewHolder) holder).player != null) {
                    ((VideoViewAdapter.ViewHolder) holder).player.resetPlayerData();
                    ((VideoViewAdapter.ViewHolder) holder).player.release();
                }
            }
        });
    }
}
