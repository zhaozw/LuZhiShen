package org.lvu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/22/16 10:51 PM.
 */
public class EuropeVideoAdapter extends BasePictureListAdapter {

    protected final String URL;
    private static final int GET_URL_SUCCESS = 8, GET_URL_FAILURE = 9;
    private AlertDialog mDialog;
    private boolean isUserCanceled;
    private int clickedPosition;

    HttpUtil.HttpRequestCallbackListener mCallBackListener = new HttpUtil.HttpRequestCallbackListener() {
        @Override
        public void onSuccess(List<Data> data, String nextPage) {
            if (!isUserCanceled) {
                mDialog.dismiss();
                Message message = Message.obtain();
                message.obj = nextPage;
                message.what = GET_URL_SUCCESS;
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void onSuccess(Bitmap bitmap) {

        }

        @Override
        public void onFailure(Exception e, String reason) {
            mDialog.dismiss();
            Message message = Message.obtain();
            message.obj = reason;
            message.what = GET_URL_FAILURE;
            mHandler.sendMessage(message);
        }
    };

    public EuropeVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
        URL = getUrl();
        initDialog();
    }

    private void initDialog() {
        mDialog = new AlertDialog.Builder(mContext)
                .setCancelable(false).setNegativeButton(mContext.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDialog.dismiss();
                                isUserCanceled = true;
                            }
                        })
                .setView(R.layout.dialog_resolving_video_address_view).create();
    }

    @Override
    protected String getUrl() {
        return "http://m.fapple.com/videos";
    }

    @Override
    protected String getPageUrl() {
        return "http://m.fapple.com/videos?p=%s";
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
            try {
                mImageLoader.displayImage(mData.get(position != 0 && position >= mData.size() ?
                                mData.size() - 1 : position).getSrc(), holder.image,
                        new DisplayImageOptions.Builder()
                                .showImageOnFail(R.drawable.ic_pic_bad)
                                .showImageOnLoading(R.drawable.ic_pic_loading)
                                .showImageForEmptyUri(R.drawable.ic_pic_bad)
                                .cacheInMemory(true).cacheOnDisk(true).build());
                holder.text.setText(mData.get(position != 0 && position >= mData.size() ?
                        mData.size() - 1 : position).getText());
                if (mOnItemClickListener != null)
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isUserCanceled = false;
                            mDialog.show();
                            clickedPosition = holder.getAdapterPosition();
                            getVideoUrlByUrl(holder);
                        }
                    });
                if (mOnItemLongClickListener != null)
                    holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                int pos = holder.getAdapterPosition();
                                return mOnItemLongClickListener.onLongClick(mData.get(pos != 0 && pos >= mData.size() ?
                                        mData.size() - 1 : pos));
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void getVideoUrlByUrl(final BaseListAdapter.ViewHolder holder) {
        if (mData.isEmpty())
            return;
        try {
            HttpUtil.getEuropeVideoUrlByUrl(
                    mData.get(holder.getAdapterPosition() != 0 && holder.getAdapterPosition() >= mData.size() ?
                            mData.size() - 1 : holder.getAdapterPosition()).getUrl(), mCallBackListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getEuropeVideoListAsync(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getEuropeVideoListAsync(mNextPageUrl, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page <= 1) {
            syncData("");
            return;
        }
        String pageUrl = getPageUrl();
        HttpUtil.getEuropeVideoListAsync(String.format(pageUrl, String.valueOf(page)), mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        if (page == 1)
            syncData("");
        else {
            String pageUrl = getPageUrl();
            HttpUtil.getEuropeVideoListAsync(String.format(pageUrl, String.valueOf(page)), mOnJumpPageCallbackListener);
        }
    }

    @Override
    protected Handler getHandler() {
        return new MyHandler(this);
    }

    private static class MyHandler extends Handler {

        private WeakReference<EuropeVideoAdapter> mClass;

        MyHandler(EuropeVideoAdapter clazz) {
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
                case GET_URL_FAILURE:
                case GET_URL_SUCCESS:
                    mClass.get().mOnItemClickListener.onClick((String) msg.obj, "", mClass.get().clickedPosition);
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
                    break;
                default:
                    break;
            }
        }
    }
}
