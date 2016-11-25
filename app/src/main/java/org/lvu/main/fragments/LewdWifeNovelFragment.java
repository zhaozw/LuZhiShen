package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.LewdWifeNovelAdapter;
import org.lvu.model.Data;

import java.io.FileNotFoundException;
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
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(LewdWifeNovelFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(LewdWifeNovelFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
