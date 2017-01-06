package org.lvu.main.fragments.view_pager_content;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.LewdWifeNovelAdapter;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:42 PM.
 */
public class LewdWifeNovelFragment extends ExcitedNovelFragment  {
    @Override
    protected BaseListAdapter getAdapter() {
        return new LewdWifeNovelAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(LewdWifeNovelFragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(LewdWifeNovelFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
