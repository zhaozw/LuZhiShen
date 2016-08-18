package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BasePictureListAdapter;
import org.lvu.adapter.GifPictureAdapter;
import org.lvu.model.Data;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by wuyr on 6/23/16 2:39 PM.
 */
public class GifPictureFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new GifPictureAdapter(getActivity(), R.layout.adapter_picture_list_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title, final int position) {
                int firstItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = mRecyclerView.getChildAt(position - firstItemPosition);
                    if (null != mRecyclerView.getChildViewHolder(view)) {

                        final BasePictureListAdapter.ViewHolder holder = (BasePictureListAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);

                        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                        asyncHttpClient.get(mAdapter.getItem(position).getUrl(), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                try {
                                    GifDrawable gd = new GifDrawable(responseBody);
                                    holder.image.setImageDrawable(gd);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                error.printStackTrace();
                            }
                        });
                    }
                }
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
