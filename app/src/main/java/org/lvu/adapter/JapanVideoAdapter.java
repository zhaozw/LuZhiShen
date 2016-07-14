package org.lvu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class JapanVideoAdapter extends BasePictureListAdapter {

    private final String URL = "http://a5408977.gotoip55.com/?s=vod-show-id-39.html";

    public JapanVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getJapanVideoListAsync(mData.size(), url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        HttpUtil.getJapanVideoListAsync(mData.size(), URL, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getJapanVideoListAsync(0, URL, mRefreshDataCallbackListener);
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<JapanVideoAdapter> mClass;

        public MyHandler(JapanVideoAdapter clazz) {
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
