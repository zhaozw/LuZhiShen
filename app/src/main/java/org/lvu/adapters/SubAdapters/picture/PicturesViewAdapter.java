package org.lvu.adapters.SubAdapters.picture;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.SubBaseAdapter;
import org.lvu.models.Data;
import org.lvu.models.Row;
import org.lvu.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/24/16 12:08 AM.
 */
public class PicturesViewAdapter extends SubBaseAdapter {

    public PicturesViewAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(final BaseListAdapter.ViewHolder holder, int position) {
        if (!handleFooterHolder(holder)) {
            if (mData.isEmpty())
                return;
            initItemImage(holder, position);
            initItemLongClickListener(holder);
        }
    }

    @Override
    protected void initItemImage(BaseListAdapter.ViewHolder holder, int position) {
        if (mData.isEmpty())
            return;
        try {
            holder.image.setImageURI(Uri.parse(HttpUtil.handleSpacesUrl(((Row)
                    mData.get(position != 0 && position >= mData.size()
                            ? mData.size() - 1 : position)).getImg())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncData(@NonNull String url) {
        HttpUtil.getPicturesAsync(url, mSyncDataCallbackListener);
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<PicturesViewAdapter> mClass;

        MyHandler(PicturesViewAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYNC_DATA_SUCCESS:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFinish();
                    mClass.get().mData.addAll((List<Data>) msg.obj);
                    mClass.get().notifyItemRangeChanged(
                            mClass.get().getDataSize(), ((List) msg.obj).size());
                    break;
                case SYNC_DATA_FAILURE:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFailure((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void loadNext() {
        //do nothing
    }

    @Override
    public void loadPrevious() {
        //do nothing
    }

    @Override
    protected String getUrl() {
        //do nothing
        return null;
    }

    @Override
    protected String getPageUrl() {
        return null;
    }

    @Override
    public void jumpToPage(int page) {
        //do nothing
    }
}
