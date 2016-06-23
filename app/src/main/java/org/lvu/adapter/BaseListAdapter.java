package org.lvu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 3:15 PM.
 */
public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListAdapter.ViewHolder> {

    protected static final int SYNC_DATA = 0, LOAD_MORE = 1, REFRESH_DATA = 2;
    protected Context mContext;
    protected int mLayoutId;
    protected List<Data> mData;
    protected OnItemClickListener mOnItemClickListener;
    protected OnLoadMoreFinishListener mOnLoadMoreFinishListener;
    protected OnSyncDataFinishListener mOnSyncDataFinishListener;
    protected OnRefreshDataFinishListener mOnRefreshDataFinishListener;
    protected String mNextPageUrl;
    protected HttpUtil.HttpRequestCallbackListener mSyncDataCallbackListener,
            mLoadMoreCallbackListener, mRefreshDataCallbackListener;
    protected Handler mHandler;

    public BaseListAdapter(Context context, int layoutId, List<Data> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mHandler = getHandler();
        mSyncDataCallbackListener = new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                sendMessage(SYNC_DATA, data, nextPage);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        };
        mLoadMoreCallbackListener = new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                sendMessage(LOAD_MORE, data, nextPage);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        };
        mRefreshDataCallbackListener = new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                sendMessage(REFRESH_DATA, data, nextPage);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.image.setImageBitmap(mData.get(position).getBitmap());
        holder.text.setText(mData.get(position).getText());
        if (mOnItemClickListener != null)
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Data data = mData.get(holder.getAdapterPosition());
                    mOnItemClickListener.onClick(data.getUrl(), data.getText());
                }
            });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected void setData(List<Data> data) {
        mData = data;
        notifyDataSetChanged();
        if (mOnSyncDataFinishListener != null)
            mOnSyncDataFinishListener.onFinish();
    }

    private void sendMessage(int what, List<Data> data, String nextPage) {
        Message message = new Message();
        message.obj = data;
        message.what = what;
        mHandler.sendMessage(message);
        mNextPageUrl = nextPage;
    }

    public abstract void syncData(@NonNull String url);

    public abstract void loadMore();

    public abstract void refreshData();

    protected abstract Handler getHandler();

    public int getDataSize() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(String url, String title);
    }

    public void setOnSyncDataFinishListener(OnSyncDataFinishListener listener) {
        mOnSyncDataFinishListener = listener;
    }

    public interface OnSyncDataFinishListener {
        void onFinish();
    }

    public void setOnLoadMoreFinishListener(OnLoadMoreFinishListener listener) {
        mOnLoadMoreFinishListener = listener;
    }

    public interface OnLoadMoreFinishListener {
        void onFinish();
    }

    public void setOnRefreshDataFinishListener(OnRefreshDataFinishListener listener){
        mOnRefreshDataFinishListener = listener;
    }

    public interface OnRefreshDataFinishListener{
        void onFinish();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        CardView root;
        ImageView image;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.card_view);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
