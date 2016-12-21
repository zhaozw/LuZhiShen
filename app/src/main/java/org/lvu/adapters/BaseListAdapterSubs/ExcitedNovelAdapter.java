package org.lvu.adapters.BaseListAdapterSubs;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:18 PM.
 */
public class ExcitedNovelAdapter extends EuropePictureAdapter {

    public ExcitedNovelAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl(){
        return "http://fv3333.com/html/part/17.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/17_%s.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getNovelListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getNovelListAsync(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getNovelListAsync(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }
}
