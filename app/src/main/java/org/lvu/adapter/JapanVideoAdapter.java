package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class JapanVideoAdapter extends EuropeVideoAdapter {

    public JapanVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "https://www.haoxxoo09.com/category/16.html";
    }

    @Override
    protected String getPageUrl() {
        return "https://www.haoxxoo09.com/category/16-%s.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getJapanVideoListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getJapanVideoListAsync(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getJapanVideoListAsync(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            String pageUrl = getPageUrl();
            HttpUtil.getJapanVideoListAsync(String.format(pageUrl, String.valueOf(page)), mOnJumpPageCallbackListener);
        }
    }

    @Override
    protected void getVideoUrlByUrl(final BaseListAdapter.ViewHolder holder) {
        if (mData.isEmpty())
            return;
        try {
            HttpUtil.getJapanVideoUrlByUrl(
                    mData.get(holder.getAdapterPosition() != 0 && holder.getAdapterPosition() >= mData.size() ?
                            mData.size() - 1 : holder.getAdapterPosition()).getUrl(), mCallBackListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
