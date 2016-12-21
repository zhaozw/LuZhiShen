package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.SchoolNovelAdapter;
import org.lvu.models.Data;

import java.io.FileNotFoundException;
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
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(SchoolNovelFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(SchoolNovelFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
