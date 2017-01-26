package org.lvu.adapters.SubAdapters.picture;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.lvu.adapters.BasePictureListAdapter;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class EvilComicsAdapter extends BasePictureListAdapter {

    public EvilComicsAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "http://www.52kkm.org/xieemanhua/";
    }

    @Override
    protected String getPageUrl() {
        return "http://www.52kkm.org/xieemanhua/list_1_%s.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getComicsListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getComicsListAsync(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getComicsListAsync(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            String pageUrl = getPageUrl();
            HttpUtil.getComicsListAsync(String.format(pageUrl, String.valueOf(page)), mOnJumpPageCallbackListener);
        }
    }

    @Override
    protected Handler getHandler() {
        return new DefaultHandler(this);
    }

}
