package org.lvu.adapters.SubAdapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.BasePictureListAdapter;
import org.lvu.models.Data;
import org.lvu.models.Row;
import org.lvu.utils.HttpUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
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
                Row row = (Row) mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position);
                holder.text.setText(row.getTitle());
                holder.date.setText(row.getDate());
                holder.favorites.setImageResource(row.isFavorites() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
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
                        int position = holder.getAdapterPosition();
                        Row row = (Row) mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position);
                        mOnItemClickListener.onClick(HttpUtil.BASE_URL + row.getJsonUrl(), row.getTitle(), position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void saveDataToStorage(BufferedWriter writer) {
        if (writer == null) return;
        if (mData == null || mData.isEmpty()) {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        List<Data> datas = new ArrayList<>();
        Data data = new Data();
        data.setNextPageUrl(mNextPageUrl);
        data.setCurrentPage(mCurrentPage);
        data.setTotalPages(mTotalPages);
        List<Row> rows = new ArrayList<>();
        for (Data tmp : mData)
            rows.add((Row) tmp);
        data.setRows(rows);
        datas.add(data);
        Gson gson = new Gson();
        String json = gson.toJson(datas);
        try {
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void restoreDataFromStorage(final BufferedReader reader, final HttpUtil.HttpRequestCallbackListener listener) {
        if (reader == null) return;
        new Thread() {
            @Override
            public void run() {
                try {
                    List<Data> result = new ArrayList<>();
                    String json;
                    Gson gson = new Gson();
                    String tmp;
                    StringBuilder sb = new StringBuilder();
                    while ((tmp = reader.readLine()) != null)
                        sb.append(tmp);
                    json = sb.toString();
                    List<Data> datas = gson.fromJson(json, new TypeToken<ArrayList<Data>>() {
                    }.getType());
                    Data data = datas.get(0);
                    List<Row> rows = data.getRows();
                    for (Row row : rows)
                        result.add(row);
                    mCurrentPage = data.getCurrentPage();
                    mTotalPages = data.getTotalPages();
                    listener.onSuccess(result, data.getNextPageUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure(e, "");
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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
