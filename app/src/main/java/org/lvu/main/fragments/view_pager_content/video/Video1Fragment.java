package org.lvu.main.fragments.view_pager_content.video;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.video.Video1Adapter;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 1/10/17 10:13 PM.
 */

public class Video1Fragment extends BaseListFragment {

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        //return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected void longClickLogic(Data data) {

    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new Video1Adapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
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
