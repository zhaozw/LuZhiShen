package org.lvu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;
import org.lvu.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/24/16 12:08 AM.
 */
public class PicturesViewAdapter extends BasePictureListAdapter {

    //item类型
    private static final int ITEM_TYPE_CONTENT = 0, ITEM_TYPE_BOTTOM = 1;
    private int mBottomCount;//底部View个数

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
        } else
            holder.image.setImageBitmap(mData.get(position >= mData.size() ?
                    mData.size() - 1 : position).getBitmap());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BOTTOM && ImmerseUtil.isAboveKITKAT()
                && ImmerseUtil.isHasNavigationBar(mContext))
            return new FooterHolder(mLayoutInflater.inflate(
                    R.layout.recycler_view_item_footer, parent, false));
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void syncData(@NonNull String url) {
        HttpUtil.getPicturesAsync(url, mSyncDataCallbackListener);
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    //判断当前item类型
    @Override
    public int getItemViewType(int position) {
        return mBottomCount != 0 && position >= getContentItemCount() ?
                ITEM_TYPE_BOTTOM : ITEM_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return getContentItemCount() + mBottomCount;
    }

    //内容长度
    public int getContentItemCount() {
        return mData.size();
    }

    public void changeToLandscape() {
        mBottomCount = 0;
    }

    public void changeToPortrait() {
        mBottomCount = ImmerseUtil.isAboveKITKAT() && ImmerseUtil.isHasNavigationBar(mContext) ? 1 : 0;
        LogUtil.print(mBottomCount);
    }

    private static class FooterHolder extends ViewHolder {

        View bottomView;

        public FooterHolder(View itemView) {
            super(itemView);
            bottomView = itemView.findViewById(R.id.navigation_bar_view);
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<PicturesViewAdapter> mClass;

        public MyHandler(PicturesViewAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYNC_DATA_SUCCESS:
                    if (msg.obj == null) {
                        if (mClass.get().mOnSyncDataFinishListener != null)
                            mClass.get().mOnSyncDataFinishListener.onFinish();
                        return;
                    }
                    mClass.get().mData.addAll((List<Data>) msg.obj);
                    mClass.get().notifyItemRangeChanged(
                            mClass.get().getDataSize(), ((List) msg.obj).size());
                    break;
                case SYNC_DATA_FAILURE:
                    if (mClass.get().mOnSyncDataFinishListener != null)
                        mClass.get().mOnSyncDataFinishListener.onFailure();
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
}
