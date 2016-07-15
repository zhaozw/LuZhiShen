package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.SchoolNovelAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 7/16/16 1:16 AM.
 */
public class SchoolNovelFragment extends ExcitedNovelFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new SchoolNovelAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }
}
