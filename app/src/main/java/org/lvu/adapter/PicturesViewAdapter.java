package org.lvu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/24/16 12:08 AM.
 */
public class PicturesViewAdapter extends BasePictureListAdapter {

    public PicturesViewAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(BaseListAdapter.ViewHolder holder, int position) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            LinearLayout.LayoutParams bottomLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ImmerseUtil.getNavigationBarHeight(mContext));
            footerHolder.bottomView.setLayoutParams(bottomLP);
        } else {
            if (mData.isEmpty())
                return;
            try {
                mImageLoader.displayImage(mData.get(position != 0 && position >= mData.size() ?
                                mData.size() - 1 : position).getSrc(), holder.image,
                        new DisplayImageOptions.Builder()
                                .showImageOnFail(R.drawable.ic_pic_bad)
                                .showImageOnLoading(R.drawable.ic_pic_loading)
                                .showImageForEmptyUri(R.drawable.ic_pic_bad)
                                .cacheInMemory(true).cacheOnDisk(true).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void loadMore() {
        //do nothing
    }

    @Override
    public void refreshData() {
        //do nothing
    }

    @Override
    protected String getUrl() {
        //do nothing
        return null;
    }
}
