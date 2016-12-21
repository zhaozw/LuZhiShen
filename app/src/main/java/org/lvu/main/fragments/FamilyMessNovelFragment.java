package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.FamilyMessNovelAdapter;
import org.lvu.models.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:42 PM.
 */
public class FamilyMessNovelFragment extends ExcitedNovelFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new FamilyMessNovelAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(FamilyMessNovelFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(FamilyMessNovelFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
