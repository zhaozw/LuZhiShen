package org.lvu.adapters.SubAdapters.picture;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;
import org.lvu.adapters.BasePictureListAdapter;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class GifPictureAdapter extends EvilComicsAdapter {

    public GifPictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public BasePictureListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BOTTOM && ImmerseUtil.isAboveKITKAT()
                && ImmerseUtil.isHasNavigationBar(mContext))
            return new FooterHolder(mLayoutInflater.inflate(
                    R.layout.recycler_view_item_footer, parent, false));
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
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

        private boolean isGifPlaying;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public boolean isGifPlaying() {
            return isGifPlaying;
        }

        public void setGifPlaying(boolean gifPlaying) {
            isGifPlaying = gifPlaying;
        }

    }
}
