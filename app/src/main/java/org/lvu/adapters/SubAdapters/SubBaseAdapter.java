package org.lvu.adapters.SubAdapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BasePictureListAdapter;
import org.lvu.models.Data;
import org.lvu.models.Row;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 1/26/17 12:50 AM.
 */

public abstract class SubBaseAdapter extends BasePictureListAdapter {

    public SubBaseAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(BaseListAdapter.ViewHolder holder, int position) {
        initDefaultItemData(holder, position);
    }

    @Override
    protected void initDefaultItemData(BaseListAdapter.ViewHolder holder, int position) {
        if (!handleFooterHolder(holder)) {
            if (mData.isEmpty())
                return;
            try {
                Data item = mData.get(0);
                Row row = item.getRows().get(position);
                holder.text.setText(row.getTitle());
                holder.date.setText(row.getDate());
                holder.favorites.setImageResource(item.isFavorites() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
                initItemOnClickListener(holder);
                initItemLongClickListener(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initItemOnClickListener(final BaseListAdapter.ViewHolder holder) {
        if (mOnItemClickListener != null)
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = holder.getAdapterPosition();
                        Data data = mData.get(0);
                        Row row = data.getRows().get(pos);
                        mOnItemClickListener.onClick(HttpUtil.BASE_URL + data.getRows().get(pos).getJsonUrl(), row.getTitle(), pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getDataListAsync(url, "1", mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getDataListAsync(URL, String.valueOf(getCurrentPage() + 1), mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        HttpUtil.getDataListAsync(URL, String.valueOf(getCurrentPage() - 1), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            HttpUtil.getDataListAsync(URL, String.valueOf(page), mOnJumpPageCallbackListener);
        }
    }

    @Override
    protected String getPageUrl() {
        return "";
    }

    @Override
    protected Handler getHandler() {
        return new DefaultHandler(this);
    }
}
