package org.lvu.main.fragments.view_pager_content;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.ChinaVideoAdapter;
import org.lvu.adapters.EuropeVideoAdapter;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:30 PM.
 */
public class ChinaVideoFragment extends BaseListFragment {

    @Override
    protected void init() {
        super.init();
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                   /* EuropeVideoAdapter.ViewHolder holder = (EuropeVideoAdapter.ViewHolder)
                            mAdapter.getHolderByPosition(mRecyclerView,
                                    ((EuropeVideoAdapter)mAdapter).getLastOnClickPosition());
                    if (holder == null)
                        ((EuropeVideoAdapter)mAdapter).releaseCurrentPlayer();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (EuropeVideoAdapter.getLastOnClickPosition() != -1 && manager.findFirstCompletelyVisibleItemPosition() != -1 && manager.findLastCompletelyVisibleItemPosition() != -1) {
                    int lastOnClickPos = EuropeVideoAdapter.getLastOnClickPosition();
                    if((manager.findFirstCompletelyVisibleItemPosition() > lastOnClickPos
                            && manager.findFirstVisibleItemPosition() != lastOnClickPos)
                            || (manager.findLastCompletelyVisibleItemPosition() < lastOnClickPos
                            && manager.findLastVisibleItemPosition() != lastOnClickPos)) {
                        ((EuropeVideoAdapter) mAdapter).releaseCurrentPlayer();
                    }
                }
            }
        });*/
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                ((EuropeVideoAdapter.ViewHolder)holder).releasePlayer();
            }
        });
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new ChinaVideoAdapter(getActivity(), R.layout.adapter_video_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return null;
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
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(ChinaVideoFragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(ChinaVideoFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
