package org.lvu.main.fragments.view_pager_content;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.JapanVideoAdapter;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class JapanVideoFragment extends ChinaVideoFragment  {

    @Override
    protected BaseListAdapter getAdapter() {
        return new JapanVideoAdapter(getActivity(), R.layout.adapter_video_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(JapanVideoFragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(JapanVideoFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
