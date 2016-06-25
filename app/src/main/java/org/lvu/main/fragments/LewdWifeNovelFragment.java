package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.LewdWifeNovelAdapter;
import org.lvu.model.Data;

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
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
