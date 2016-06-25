package org.lvu.main.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.ExcitedNovelAdapter;
import org.lvu.main.activities.NovelViewActivity;
import org.lvu.main.activities.PicturesViewActivity;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:41 PM.
 */
public class ExcitedNovelFragment extends BaseListFragment  {
    @Override
    protected BaseListAdapter getAdapter() {
        return new ExcitedNovelAdapter(getActivity(), R.layout.adapter_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                Intent intent = new Intent(getActivity(), NovelViewActivity.class);
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
}
