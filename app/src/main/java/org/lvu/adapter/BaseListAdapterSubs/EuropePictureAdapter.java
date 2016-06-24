package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import org.lvu.adapter.BaseListAdapter;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:15 PM.
 */
public class EuropePictureAdapter extends BaseListAdapter {

    private final String URL;

    public EuropePictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
        URL = getUrl();
    }

    protected String getUrl(){
        return "http://0pmp.com/html/11/";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getPicturesListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getPicturesListAsync(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getPicturesListAsync(URL, mRefreshDataCallbackListener);
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<EuropePictureAdapter> mClass;

        public MyHandler(EuropePictureAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYNC_DATA_SUCCESS:
                    mClass.get().setData((List<Data>) msg.obj);
                    break;
                case LOAD_MORE_SUCCESS:
                    mClass.get().mData.addAll((List<Data>)msg.obj);
                    mClass.get().notifyItemRangeChanged(
                            mClass.get().getDataSize(), ((List) msg.obj).size());
                    break;
                case REFRESH_DATA_SUCCESS:
                    mClass.get().mData = new ArrayList<>();
                    mClass.get().notifyDataSetChanged();
                    mClass.get().setData((List<Data>) msg.obj);
                    if (mClass.get().mOnRefreshDataFinishListener != null)
                        mClass.get().mOnRefreshDataFinishListener.onFinish();
                    break;
                case SYNC_DATA_FAILURE:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFailure();
                    break;
                case LOAD_MORE_FAILURE:
                    if (mClass.get().mOnLoadMoreFinishListener != null)
                        mClass.get().mOnLoadMoreFinishListener.onFailure();
                    break;
                case REFRESH_DATA_FAILURE:
                    if (mClass.get().mOnRefreshDataFinishListener != null)
                        mClass.get().mOnRefreshDataFinishListener.onFailure();
                    break;
                default:
                    break;
            }
            if (mClass.get().mOnLoadMoreFinishListener != null)
                mClass.get().mOnLoadMoreFinishListener.onFinish();
        }
    }
}
