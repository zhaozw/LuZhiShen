package org.lvu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/22/16 10:51 PM.
 */
public class EuropeVideoAdapter extends BaseListAdapter {

    private final String URL = "http://m.fapple.com/videos";
    private static final int GET_URL = 3;

    public EuropeVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.image.setImageBitmap(mData.get(position).getBitmap());
        holder.text.setText(mData.get(position).getText());
        if (mOnItemClickListener != null)
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage(mContext.getString(R.string.resolving_video_address));
                    dialog.setCancelable(false);
                    dialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.obj = HttpUtil.getEuropeVideoUrlByUrl(
                                    mData.get(holder.getAdapterPosition()).getUrl());
                            message.what = GET_URL;
                            dialog.dismiss();
                            mHandler.sendMessage(message);
                        }
                    }.start();
                }
            });
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getEuropeVideoListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getEuropeVideoListAsync(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getEuropeVideoListAsync(URL, mRefreshDataCallbackListener);
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    static class MyHandler extends Handler {

        private WeakReference<EuropeVideoAdapter> mClass;

        public MyHandler(EuropeVideoAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYNC_DATA:
                    mClass.get().setData((List<Data>) msg.obj);
                    break;
                case LOAD_MORE:
                    mClass.get().mData.addAll((List<Data>) msg.obj);
                    mClass.get().notifyItemRangeChanged(
                            mClass.get().getDataSize(), ((List) msg.obj).size());
                    break;
                case REFRESH_DATA:
                    mClass.get().mData = new ArrayList<>();
                    mClass.get().notifyDataSetChanged();
                    mClass.get().setData((List<Data>) msg.obj);
                    if (mClass.get().mOnRefreshDataFinishListener != null)
                        mClass.get().mOnRefreshDataFinishListener.onFinish();
                    break;
                case GET_URL:
                    mClass.get().mOnItemClickListener.onClick((String) msg.obj, "");
                    break;
                default:
                    break;
            }
            if (mClass.get().mOnLoadMoreFinishListener != null && msg.what != GET_URL)
                mClass.get().mOnLoadMoreFinishListener.onFinish();
        }
    }
}
