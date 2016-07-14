package org.lvu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/22/16 10:51 PM.
 */
public class EuropeVideoAdapter extends BasePictureListAdapter {

    private final String URL = "http://m.fapple.com/videos";
    private static final int GET_URL_SUCCESS = 6, GET_URL_FAILURE = 7;

    public EuropeVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(final BaseListAdapter.ViewHolder holder, int position) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            LinearLayout.LayoutParams bottomLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ImmerseUtil.getNavigationBarHeight(mContext));
            footerHolder.bottomView.setLayoutParams(bottomLP);
        } else {
            if (mData.isEmpty())
                return;
            holder.image.setImageBitmap(mData.get(position != 0 && position >= mData.size() ?
                    mData.size() - 1 : position).getBitmap());
            holder.text.setText(mData.get(position != 0 && position >= mData.size() ?
                    mData.size() - 1 : position).getText());
            if (mOnItemClickListener != null)
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setMessage(mContext.getString(R.string.resolving_video_address));
                        dialog.setCancelable(false);
                        dialog.show();
                        HttpUtil.getEuropeVideoUrlByUrl(
                                mData.get(holder.getAdapterPosition() != 0 && holder.getAdapterPosition() >= mData.size() ?
                                        mData.size() - 1 : holder.getAdapterPosition()).getUrl(), new HttpUtil.HttpRequestCallbackListener() {
                                    @Override
                                    public void onSuccess(List<Data> data, String nextPage) {
                                        dialog.dismiss();
                                        Message message = new Message();
                                        message.obj = nextPage;
                                        message.what = GET_URL_SUCCESS;
                                        mHandler.sendMessage(message);
                                    }

                                    @Override
                                    public void onFailure(Exception e, String reason) {
                                        dialog.dismiss();
                                        Message message = new Message();
                                        message.obj = reason;
                                        message.what = GET_URL_FAILURE;
                                        mHandler.sendMessage(message);
                                    }
                                });
                    }
                });
        }
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
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<EuropeVideoAdapter> mClass;

        public MyHandler(EuropeVideoAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case REFRESH_DATA_SUCCESS:
                case SYNC_DATA_SUCCESS:
                case LOAD_MORE_SUCCESS:
                    mClass.get().addData((List<Data>) msg.obj, what);
                    break;
                case GET_URL_FAILURE:
                case GET_URL_SUCCESS:
                    mClass.get().mOnItemClickListener.onClick((String) msg.obj, "");
                    break;
                case SYNC_DATA_FAILURE:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFailure((String) msg.obj);
                    break;
                case LOAD_MORE_FAILURE:
                    if (mClass.get().mOnLoadMoreFinishListener != null)
                        mClass.get().mOnLoadMoreFinishListener.onFailure((String) msg.obj);
                    break;
                case REFRESH_DATA_FAILURE:
                    if (mClass.get().mOnRefreshDataFinishListener != null)
                        mClass.get().mOnRefreshDataFinishListener.onFailure((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
