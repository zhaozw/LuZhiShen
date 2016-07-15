package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.JapanVideoAdapter;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class JapanVideoFragment extends ChinaVideoFragment  {

    @Override
    protected BaseListAdapter getAdapter() {
        return new JapanVideoAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }
}
