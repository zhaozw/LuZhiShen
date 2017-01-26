package org.lvu.main.fragments.view_pager_content.picture;

import android.graphics.drawable.Animatable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.facebook.drawee.controller.BaseControllerListener;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.picture.GifPictureAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.main.activities.MainActivity;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.models.Data;

import java.util.ArrayList;

/**
 * Created by wuyr on 6/23/16 2:39 PM.
 */
public class GifPictureFragment extends BaseListFragment {
    @Override
    protected void init() {
        super.init();
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                ((GifPictureAdapter.ViewHolder) holder).setGifPlaying(false);
            }
        });
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new GifPictureAdapter(getActivity(), R.layout.adapter_gif_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String text, final int position) {
                final GifPictureAdapter.ViewHolder holder = (GifPictureAdapter.ViewHolder) mAdapter.getHolderByPosition(mRecyclerView, position);
                if (holder != null && !holder.isGifPlaying()) {
                    holder.setGifPlaying(true);
                    holder.image.setImageURI(url, new BaseControllerListener() {
                        @Override
                        public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                            holder.setGifPlaying(true);
                            mRecyclerView.smoothScrollToPosition(position);
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            holder.setGifPlaying(false);
                            MySnackBar.show(((MainActivity) getActivity()).getRootView(), getString(R.string.load_pic_fail), Snackbar.LENGTH_SHORT);
                        }
                    });
                }
            }
        };
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return new BaseListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(Data item) {
                showDialog(item, getString(R.string.download_this_gif));
                return true;
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void longClickLogic(Data data) {
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(openFileOutput(GifPictureFragment.class.getSimpleName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(openFileInput(GifPictureFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }
}
