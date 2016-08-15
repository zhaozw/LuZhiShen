package org.lvu.main.fragments;

import android.support.v7.widget.LinearLayoutManager;
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
        return new GifPictureAdapter(getActivity(), R.layout.adapter_gif_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(final String url, String title) {
                /*int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = mRecyclerView.getChildAt(position - firstItemPosition+1);
                    if (null != mRecyclerView.getChildViewHolder(view)){
                        ProductsViewHolder viewHolder = (ProductsViewHolder)mRecyclerView.getChildViewHolder(view);

                        //do something
                    }*/

                /****************************************************************/
                /*
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get("http://cdn.duitang.com/uploads/item/201311/20/20131120213622_mJCUy.thumb.600_0.gif", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,final byte[] responseBody) {
                LogUtil.print("responseBody.length  " + responseBody.length);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
                 */
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
