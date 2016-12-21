package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.AsiaPictureAdapter;
import org.lvu.models.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:36 PM.
 */
public class AsiaPictureFragment extends EuropePictureFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new AsiaPictureAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(AsiaPictureFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(AsiaPictureFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
