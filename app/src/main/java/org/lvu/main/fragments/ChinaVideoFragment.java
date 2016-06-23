package org.lvu.main.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.ChinaVideoAdapter;
import org.lvu.main.activities.MainActivity;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class ChinaVideoFragment extends BaseListFragment{

    @Override
    protected BaseListAdapter getAdapter() {
        return new ChinaVideoAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                if (mLoadMoreBar.getVisibility() == View.VISIBLE)
                    mLoadMoreBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).setDrawerLockMode(true);
                ((MainActivity) getActivity()).hideToolbar();
                mPlayer.setUrlPlay(url);
                mPlayer.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }
}
