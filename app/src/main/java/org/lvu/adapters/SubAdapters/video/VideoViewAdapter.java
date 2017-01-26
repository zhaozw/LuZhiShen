package org.lvu.adapters.SubAdapters.video;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.SubBaseAdapter;
import org.lvu.adapters.SubAdapters.picture.GifPictureAdapter;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;
import org.video_player.VideoPlayer;

import java.util.List;


/**
 * Created by wuyr on 1/26/17 9:29 PM.
 */

public class VideoViewAdapter extends SubBaseAdapter {

    private String mTitle;

    public void setTitle(String title) {
        mTitle = title;
    }

    public VideoViewAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BOTTOM && ImmerseUtil.isAboveKITKAT()
                && ImmerseUtil.isHasNavigationBar(mContext))
            return new FooterHolder(mLayoutInflater.inflate(
                    R.layout.recycler_view_item_footer, parent, false));
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseListAdapter.ViewHolder holder, int position) {
        if (!handleFooterHolder(holder)) {
            if (mData.isEmpty())
                return;
            try {
                Data.Vod item = (Data.Vod) mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position);
                if (mTitle == null)
                    mTitle = "";
                holder.player.setTitle(mTitle + "   (第" + (position + 1) + "集)");
                holder.player.setUrl(item.getVod());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getVideoContent(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
    }

    @Override
    public void loadPrevious() {
    }

    @Override
    public void jumpToPage(int page) {
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected String getPageUrl() {
        return null;
    }

    @Override
    protected Handler getHandler() {
        return new DefaultHandler(this);
    }

    public static class ViewHolder extends GifPictureAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            player = (VideoPlayer) itemView.findViewById(R.id.player);
        }
    }
}
