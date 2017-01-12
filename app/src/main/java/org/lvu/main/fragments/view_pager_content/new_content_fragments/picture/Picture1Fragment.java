package org.lvu.main.fragments.view_pager_content.new_content_fragments.picture;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.newAdapters.picture.Picture1Adapter;
import org.lvu.customize.MyStaggeredGridLayoutManager;
import org.lvu.main.fragments.view_pager_content.EuropePictureFragment;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 1/10/17 10:16 PM.
 */

public class Picture1Fragment extends EuropePictureFragment {
    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new MyStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //return new LinearLayoutManager(getActivity());
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new Picture1Adapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(Picture1Fragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(Picture1Fragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
