package org.lvu.main.fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.EuropeVideoAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class EuropeVideoFragment extends ChinaVideoFragment {

    @Override
    protected BaseListAdapter getAdapter() {
        return new EuropeVideoAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }
}
