package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 7/26/16 9:53 PM.
 */
public class FunnyJokeAdapter extends EuropePictureAdapter {

    private final String URL = "http://www.laifudao.com/wangwen/";

    public FunnyJokeAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getJokeListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getJokeListAsync(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getJokeListAsync(URL, mRefreshDataCallbackListener);
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }
}
