package org.lvu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;
import org.lvu.main.fragments.GifPictureFragment;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class GifPictureAdapter extends EvilComicsAdapter {

    private ExecutorService mThreadPool;

    public GifPictureAdapter(Context context, int layoutId, List<Data> data) {
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
        if (mData.isEmpty())
            return;
        final String gifUrl = mData.get(position != 0 && position >= mData.size() ?
                mData.size() - 1 : position).getUrl();
        if (GifPictureFragment.isThisGifLoaded(gifUrl)) {
            initDefaultItemData(holder, position);
            if (mThreadPool == null)
                mThreadPool = Executors.newFixedThreadPool(4);
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final GifDrawable gd = new GifDrawable(GifPictureFragment.getGifFileByUrl(gifUrl));
                        holder.image.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    holder.image.setImageDrawable(gd);
                                    ((GifPictureAdapter.ViewHolder) holder).setGifPlaying(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    initItemImage(holder, holder.getAdapterPosition());
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        initItemImage(holder, holder.getAdapterPosition());
                    }
                }
            });
        } else super.onBindViewHolder(holder, position);
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
            progress = itemView.findViewById(R.id.progress_bar);
        }

        public boolean isGifPlaying() {
            return isGifPlaying;
        }

        public void setGifPlaying(boolean isGifPlaying) {
            this.isGifPlaying = isGifPlaying;
        }
    }
}
