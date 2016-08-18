package org.lvu.main.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.FunnyJokeAdapter;
import org.lvu.main.activities.MoreJokeActivity;
import org.lvu.main.activities.PicturesViewActivity;
import org.lvu.model.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 7/26/16 9:52 PM.
 */
public class FunnyJokeFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new FunnyJokeAdapter(getActivity(), R.layout.adapter_joke_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title, int position) {
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), MoreJokeActivity.class);
                    intent.putExtra(PicturesViewActivity.URL, url);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
