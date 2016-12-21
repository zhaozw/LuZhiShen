package org.lvu.main.fragments;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.ChinaVideoAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.main.activities.MainActivity;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class ChinaVideoFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new ChinaVideoAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String text, int position) {
                if (url.equals(HttpUtil.REASON_NO_INTERNET_CONNECT) || url.equals(HttpUtil.REASON_CONNECT_SERVER_FAILURE)) {
                    MySnackBar.show(mRootView.findViewById(R.id.coordinator), url, Snackbar.LENGTH_LONG);
                    return;
                }
                if (mJumpBar.getVisibility() == View.VISIBLE)
                    mJumpBar.setVisibility(View.GONE);
                if (getActivity() == null)
                    return;
                ((MainActivity) getActivity()).setDrawerLockMode(true);
                ((MainActivity) getActivity()).hideToolbar();
                mPlayer.setUrlPlay(url);
                mPlayer.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return new BaseListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(Data item) {
                showDialog(item,getString(R.string.download_this_video));
                return true;
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        //return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void longClickLogic(Data data) {

    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(ChinaVideoFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(ChinaVideoFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
