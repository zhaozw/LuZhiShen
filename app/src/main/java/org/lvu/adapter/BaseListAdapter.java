package org.lvu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:39 PM.
 */
public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListAdapter.ViewHolder> {

    public static final int SYNC_DATA_SUCCESS = 0, LOAD_MORE_SUCCESS = 1, REFRESH_DATA_SUCCESS = 2,
            SYNC_DATA_FAILURE = 3, LOAD_MORE_FAILURE = 4, REFRESH_DATA_FAILURE = 5;
    protected final String URL;

    //item类型
    protected static final int ITEM_TYPE_CONTENT = 0, ITEM_TYPE_BOTTOM = 1;
    private int mBottomCount;//底部View个数
    protected Context mContext;
    LayoutInflater mLayoutInflater;
    int mLayoutId;
    protected List<Data> mData;
    OnItemClickListener mOnItemClickListener;
    protected OnLoadMoreFinishListener mOnLoadMoreFinishListener;
    protected OnSyncDataFinishListener mOnSyncDataFinishListener;
    protected OnRefreshDataFinishListener mOnRefreshDataFinishListener;
    protected String mNextPageUrl;
    protected HttpUtil.HttpRequestCallbackListener mSyncDataCallbackListener,
            mLoadMoreCallbackListener, mRefreshDataCallbackListener;
    Handler mHandler;
    private boolean isOwnerDestroyed;

    public BaseListAdapter(Context context, int layoutId, List<Data> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mHandler = getHandler();
        mLayoutInflater = LayoutInflater.from(context);
        URL = getUrl();
        mSyncDataCallbackListener = new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(SYNC_DATA_SUCCESS, data, nextPage);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {

            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(SYNC_DATA_FAILURE, reason);
            }
        };
        mLoadMoreCallbackListener = new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(LOAD_MORE_SUCCESS, data, nextPage);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {

            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(LOAD_MORE_FAILURE, reason);
            }
        };
        mRefreshDataCallbackListener = new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(REFRESH_DATA_SUCCESS, data, nextPage);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {

            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(REFRESH_DATA_FAILURE, reason);
            }
        };
    }

    private void sendSuccessMessage(int what, List<Data> data, String nextPage) {
        mNextPageUrl = nextPage;
        Message message = Message.obtain();
        message.obj = data;
        message.what = what;
        mHandler.sendMessage(message);
    }

    private void sendFailureMessage(int what, String reason) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = reason;
        mHandler.sendMessage(message);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BOTTOM && ImmerseUtil.isAboveKITKAT()
                && ImmerseUtil.isHasNavigationBar(mContext))
            return new FooterHolder(mLayoutInflater.inflate(
                    R.layout.recycler_view_item_footer, parent, false));
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
                holder.text.setText(mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position).getText());
                if (mOnItemClickListener != null)
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int pos = holder.getAdapterPosition();
                                Data data = mData.get(pos != 0 && pos >= mData.size() ?
                                        mData.size() - 1 : pos);
                                mOnItemClickListener.onClick(data.getUrl(), data.getText(), pos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void onViewAttachedToWindow(BaseListAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder))
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        return layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams;
    }

    private void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (isFooter(position)) {
            StaggeredGridLayoutManager.LayoutParams p =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    private boolean isFooter(int position) {
        return mBottomCount != 0 && position == getContentItemCount();
    }

    //内容长度
    private int getContentItemCount() {
        return mData.size();
    }

    public void changeToLandscape() {
        mBottomCount = 0;
    }

    public void changeToPortrait() {
        mBottomCount = ImmerseUtil.isAboveKITKAT() && ImmerseUtil.isHasNavigationBar(mContext) ? 1 : 0;
    }

    public void addData(List<Data> data, int what) {
        if (data != null) {
            mData.addAll(data);
            notifyItemRangeChanged(getDataSize(), data.size());
            switch (what) {
                case SYNC_DATA_SUCCESS:
                    if (mOnSyncDataFinishListener != null)
                        mOnSyncDataFinishListener.onFinish();
                    break;
                case LOAD_MORE_SUCCESS:
                    if (mOnLoadMoreFinishListener != null)
                        mOnLoadMoreFinishListener.onFinish();
                    break;
                case REFRESH_DATA_SUCCESS:
                    if (mOnRefreshDataFinishListener != null)
                        mOnRefreshDataFinishListener.onFinish();
                    break;
                default:
                    break;
            }
        }
    }

    public void saveDataToStorage(OutputStream os) {
        if (mData == null || mData.isEmpty())
            return;
        ObjectOutputStream oos = null;
        try {
            mData.get(0).setNextPageUrl(mNextPageUrl);
            oos = new ObjectOutputStream(os);
            oos.writeObject(mData);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void restoreDataFromStorage(InputStream is) {
        restoreDataFromStorage(is, new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String args) {
                mSyncDataCallbackListener.onSuccess(data, args);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {

            }

            @Override
            public void onFailure(Exception e, String reason) {
                syncData("");
            }
        });
    }

    protected void restoreDataFromStorage(final InputStream is, final HttpUtil.HttpRequestCallbackListener listener) {
        new Thread(){
            @Override
            public void run() {
                List<Data> result;
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(is);
                    result = (List<Data>) ois.readObject();
                    listener.onSuccess(result, result.get(0).getNextPageUrl());
                } catch (Exception e) {
                    listener.onFailure(e, "");
                } finally {
                    if (ois != null) {
                        try {
                            ois.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public abstract void syncData(@NonNull String url);

    public abstract void loadMore();

    public abstract void refreshData();

    protected abstract String getUrl();

    protected abstract Handler getHandler();

    public int getDataSize() {
        return mData.size();
    }

    public Data getItem(int pos) {
        return mData.get(pos);
    }

    public void clearData() {
        mData = null;
        mData = new ArrayList<>();
        notifyDataSetChanged();
        System.gc();
    }

    public void setOwnerIsDestroyed() {
        isOwnerDestroyed = true;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(String url, String title, int position);
    }

    public void setOnSyncDataFinishListener(OnSyncDataFinishListener listener) {
        mOnSyncDataFinishListener = listener;
    }

    public interface OnSyncDataFinishListener {
        void onFinish();

        void onFailure(String reason);
    }

    public void setOnLoadMoreFinishListener(OnLoadMoreFinishListener listener) {
        mOnLoadMoreFinishListener = listener;
    }

    public interface OnLoadMoreFinishListener extends OnSyncDataFinishListener {
    }

    public void setOnRefreshDataFinishListener(OnRefreshDataFinishListener listener) {
        mOnRefreshDataFinishListener = listener;
    }

    public interface OnRefreshDataFinishListener extends OnSyncDataFinishListener {
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        CardView root;
        public TextView text;
        public ImageView image;
        public View progress;

        public ViewHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.card_view);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    static class FooterHolder extends GifPictureAdapter.ViewHolder {

        View bottomView;

        FooterHolder(View itemView) {
            super(itemView);
            bottomView = itemView.findViewById(R.id.navigation_bar_view);
        }
    }
}
