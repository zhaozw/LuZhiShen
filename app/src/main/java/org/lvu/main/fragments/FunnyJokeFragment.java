package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.FunnyJokeAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 7/26/16 9:52 PM.
 */
public class FunnyJokeFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new FunnyJokeAdapter(getActivity(), R.layout.adapter_list_item,new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
