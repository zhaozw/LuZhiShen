package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.JapanVideoAdapter;
import org.lvu.models.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class JapanVideoFragment extends ChinaVideoFragment  {

    @Override
    protected BaseListAdapter getAdapter() {
        return new JapanVideoAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(JapanVideoFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(JapanVideoFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
