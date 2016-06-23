package org.lvu.main.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.GifPictureAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:39 PM.
 */
public class GifPictureFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new GifPictureAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }
}
