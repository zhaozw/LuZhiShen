package org.lvu.adapters.BaseListAdapterSubs;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import org.lvu.adapters.BaseListAdapter;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:15 PM.
 */
public class EuropePictureAdapter extends BaseListAdapter {

    public EuropePictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "http://fv3333.com/html/part/11.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/11_%s.html";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getPicturesListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getPicturesListAsync(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getPicturesListAsync(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            String pageUrl = getPageUrl();
            HttpUtil.getPicturesListAsync(String.format(pageUrl, String.valueOf(page)), mOnJumpPageCallbackListener);
        }
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<EuropePictureAdapter> mClass;

        MyHandler(EuropePictureAdapter clazz) {
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
                case JUMP_PAGE_SUCCESS:
                    mClass.get().addData((List<Data>) msg.obj, what);
                    break;
                case SYNC_DATA_FAILURE:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFailure((String) msg.obj);
                    break;
                case LOAD_MORE_FAILURE:
                    if (mClass.get().mOnLoadNextFinishListener != null)
                        mClass.get().mOnLoadNextFinishListener.onFailure((String) msg.obj);
                    break;
                case REFRESH_DATA_FAILURE:
                    if (mClass.get().mOnLoadPreviousFinishListener != null)
                        mClass.get().mOnLoadPreviousFinishListener.onFailure((String) msg.obj);
                    break;
                case JUMP_PAGE_FAILURE:
                    if (mClass.get().mOnJumpPageFinishListener != null)
                        mClass.get().mOnJumpPageFinishListener.onFailure((String) msg.obj);
                default:
                    break;
            }
        }
    }
}
