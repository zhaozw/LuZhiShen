package org.lvu.main.fragments;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.EvilComicsAdapter;
import org.lvu.main.activities.ComicsViewActivity;
import org.lvu.main.activities.PicturesViewActivity;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:38 PM.
 */
public class EvilComicsFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new EvilComicsAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                Intent intent = new Intent(getActivity(), ComicsViewActivity.class);
                intent.putExtra(PicturesViewActivity.URL, url);
                intent.putExtra(PicturesViewActivity.TITLE, title);
                startActivity(intent);
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }
}
