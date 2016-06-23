package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.EuropePictureAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:37 PM.
 */
public class EuropePictureFragment extends BaseListFragment  {
    @Override
    protected BaseListAdapter getAdapter() {
        return new EuropePictureAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
