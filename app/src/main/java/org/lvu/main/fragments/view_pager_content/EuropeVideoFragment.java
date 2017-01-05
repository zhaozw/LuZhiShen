package org.lvu.main.fragments.view_pager_content;

import android.content.Context;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.EuropeVideoAdapter;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class EuropeVideoFragment extends ChinaVideoFragment {

    @Override
    protected BaseListAdapter getAdapter() {
        return new EuropeVideoAdapter(getActivity(), R.layout.adapter_video_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(Application.getContext().openFileOutput(EuropeVideoFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(Application.getContext().openFileInput(EuropeVideoFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
