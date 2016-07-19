package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class ChinaVideoAdapter extends EuropeVideoAdapter {

    public ChinaVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "https://www.vmfh.info/sj/vod-show-id-1-p-1.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getChinaVideoListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getChinaVideoListAsync(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getChinaVideoListAsync(URL, mRefreshDataCallbackListener);
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    protected void getVideoUrlByUrl(final BaseListAdapter.ViewHolder holder) {
        if (mData.isEmpty())
            return;
        try {
            HttpUtil.getChinaVideoUrlByUrl(
                    mData.get(holder.getAdapterPosition() != 0 && holder.getAdapterPosition() >= mData.size() ?
                            mData.size() - 1 : holder.getAdapterPosition()).getUrl(), mCallBackListener);
        }catch (Exception e){
            e.printStackTrace();
        }
     }

}
