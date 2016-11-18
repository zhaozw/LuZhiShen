package org.lvu.main.fragments;

import android.content.Context;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.FamilyPhotoAdapter;
import org.lvu.model.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:36 PM.
 */
public class FamilyPhotoFragment extends EuropePictureFragment  {
    @Override
    protected BaseListAdapter getAdapter() {
        return new FamilyPhotoAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(FamilyPhotoFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(FamilyPhotoFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
