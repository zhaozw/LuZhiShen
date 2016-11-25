package org.lvu.main.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.EuropePictureAdapter;
import org.lvu.main.activities.PicturesViewActivity;
import org.lvu.model.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:37 PM.
 */
public class EuropePictureFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new EuropePictureAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title, int position) {
                Intent intent = new Intent(getActivity(), PicturesViewActivity.class);
                intent.putExtra(PicturesViewActivity.URL, url);
                intent.putExtra(PicturesViewActivity.TITLE, title);
                startActivity(intent);
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(EuropePictureFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(EuropePictureFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
