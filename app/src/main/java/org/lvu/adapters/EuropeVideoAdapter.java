package org.lvu.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.lvu.R;
import org.lvu.customize.VideoPlayerManager;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;
import org.video_player.VideoPlayer;

import java.util.List;

/**
 * Created by wuyr on 6/22/16 10:51 PM.
 */
public class EuropeVideoAdapter extends BaseListAdapter {

    private ImageLoader mImageLoader;
    private static int mLastOnClickPosition = -1;
    private VideoPlayer mCurrentPlayer;

    public EuropeVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
        mImageLoader = ImageLoader.getInstance();
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
        super.onBindViewHolder(holder, position);
        if (mData.isEmpty())
            return;
        try {
            Data item = mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position);
            holder.player.setUrl(item.getUrl());
            holder.player.setTitle(item.getText());
            holder.player.hideTitleView();
            holder.player.setPreviewResource(R.drawable.ic_video_pic_loading);
            mImageLoader.loadImage(item.getSrc(), new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisk(true).build(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.player.setPreviewResource(R.drawable.ic_video_pic_load_failed);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.player.setPreviewBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            holder.player.setOnPlayerClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLastOnClickPosition = holder.getAdapterPosition();
                    mCurrentPlayer = holder.player;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseCurrentPlayer(){
        mLastOnClickPosition = -1;
        if (mCurrentPlayer != null) {
            mCurrentPlayer.release();
            mCurrentPlayer = null;
        }
    }

    public static int getLastOnClickPosition(){
        return mLastOnClickPosition;
    }

    private void resetAllPlayers(){
        mLastOnClickPosition = -1;
        mCurrentPlayer = null;
        VideoPlayerManager.getInstance().resetAll();
    }

    @Override
    public void syncData(@NonNull String url) {
        HttpUtil.getEuropeVideoListAsync(1, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        int page = getCurrentPage() + 1;
        if (page > getTotalPages())
            page = getCurrentPage();
        HttpUtil.getEuropeVideoListAsync(page, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page < 1)
            page = 1;
        HttpUtil.getEuropeVideoListAsync(page, mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        HttpUtil.getEuropeVideoListAsync(page, mOnJumpPageCallbackListener);
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
        return new MyHandler(this);
    }

    public static class ViewHolder extends GifPictureAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            player = (VideoPlayer) itemView.findViewById(R.id.player);
        }
    }

    private static class MyHandler extends DefaultHandler {

        MyHandler(BaseListAdapter clazz) {
            super(clazz);
        }
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case REFRESH_DATA_SUCCESS:
                case SYNC_DATA_SUCCESS:
                case LOAD_MORE_SUCCESS:
                case JUMP_PAGE_SUCCESS:
                    ((EuropeVideoAdapter)mClass.get()).resetAllPlayers();
                    break;
                default:
            }
            super.handleMessage(msg);
        }
    }
}
