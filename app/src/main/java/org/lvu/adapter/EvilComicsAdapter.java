package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class EvilComicsAdapter extends ChinaVideoAdapter {

    public EvilComicsAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "http://www.52kkm.org/xieemanhua/";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getComicsListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getComicsListAsync(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getComicsListAsync(URL, mRefreshDataCallbackListener);
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }
}
