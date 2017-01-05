package org.lvu.main.fragments.view_pager_content;

import android.content.Context;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BaseListAdapterSubs.FamilyPhotoAdapter;
import org.lvu.models.Data;

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
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(Application.getContext().openFileOutput(FamilyPhotoFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(Application.getContext().openFileInput(FamilyPhotoFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
