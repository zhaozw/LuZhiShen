package org.lvu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.lvu.R;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class GifPictureAdapter extends EvilComicsAdapter {

    public GifPictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "http://www.lovefou.com/dongtaitu/";
    }

    @Override
    protected String getPageUrl() {
        return "http://www.lovefou.com/dongtaitu/list_%s.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getGifList(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getGifList(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getGifList(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            String pageUrl = getPageUrl();
            HttpUtil.getGifList(String.format(pageUrl, String.valueOf(page)), mOnJumpPageCallbackListener);
        }
    }

    public static class ViewHolder extends BasePictureListAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress_bar);
        }
    }
}
