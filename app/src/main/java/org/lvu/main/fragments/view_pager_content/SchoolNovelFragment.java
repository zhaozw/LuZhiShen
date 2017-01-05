package org.lvu.main.fragments.view_pager_content;

import android.content.Context;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.SchoolNovelAdapter;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 7/16/16 1:16 AM.
 */
public class SchoolNovelFragment extends ExcitedNovelFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new SchoolNovelAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(Application.getContext().openFileOutput(SchoolNovelFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(Application.getContext().openFileInput(SchoolNovelFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
