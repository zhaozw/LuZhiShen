package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapterSubs.AsiaPictureAdapter;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.model.Data;

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
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
