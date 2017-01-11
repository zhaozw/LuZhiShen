package org.lvu.main.fragments.view_pager_content.new_content_fragments.video;

import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.newAdapters.video.Video1Adapter;
import org.lvu.main.fragments.view_pager_content.ChinaVideoFragment;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 1/10/17 10:13 PM.
 */

public class Video1Fragment extends ChinaVideoFragment {

    @Override
    protected void init() {
        super.init();
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                ((Video1Adapter.ViewHolder) holder).releasePlayer();
            }
        });
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new Video1Adapter(getActivity(), R.layout.adapter_video_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(Video1Fragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(Video1Fragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
