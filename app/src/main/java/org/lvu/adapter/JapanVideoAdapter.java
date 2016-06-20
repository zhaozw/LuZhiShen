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
import org.lvu.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class JapanVideoAdapter extends RecyclerView.Adapter<JapanVideoAdapter.ViewHolder> {

    private Context mContext;
    private int mLayoutId;
    private List<Data> mData;
    private OnItemClickListener mOnItemClickListener;
    private OnLoadMoreFinishListener mOnLoadMoreFinishListener;
    private OnSyncDataFinishListener mOnSyncDataFinishListener;
    private Handler mHandler;
    private String mNextPageUrl;

    public JapanVideoAdapter(Context context, int layoutId, List<Data> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mHandler = new MyHandler(this);
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

    public void setData(List<Data> data) {
        mData.addAll(data);
        notifyItemRangeChanged(getDataSize(), data.size());
    }

    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = "http://a5408977.gotoip55.com/?s=vod-show-id-39.html";
        HttpUtil.getVideoListAsync2(mData.size(), url, new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                Message message = new Message();
                message.obj = data;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadMore() {
        // TODO: 6/18/16 handler url
            HttpUtil.getVideoListAsync2(mData.size(),
                    "http://a5408977.gotoip55.com/?s=vod-show-id-39.html",
                    new HttpUtil.HttpRequestCallbackListener() {
                        @Override
                        public void onSuccess(List<Data> data, String nextPage) {
                            Message message = new Message();
                            message.obj = data;
                            message.what = 1;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                        }
                    });
    }

    public int getDataSize() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(String url, String title);
    }

    public void setOnLoadMoreFinishListener(OnLoadMoreFinishListener listener) {
        mOnLoadMoreFinishListener = listener;
    }

    public interface OnLoadMoreFinishListener {
        void onFinish();
    }

    public void setOnSyncDataFinishListener(OnSyncDataFinishListener listener) {
        mOnSyncDataFinishListener = listener;
    }

    public interface OnSyncDataFinishListener {
        void onFinish();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

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

    static class MyHandler extends Handler {

        private WeakReference<JapanVideoAdapter> mClass;

        public MyHandler(JapanVideoAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            LogUtil.print(msg.what);
            if (msg.what == 1) {
                if (mClass.get().mOnLoadMoreFinishListener != null)
                    mClass.get().mOnLoadMoreFinishListener.onFinish();
            }else {
                if (mClass.get().mOnSyncDataFinishListener != null)
                    mClass.get().mOnSyncDataFinishListener.onFinish();
            }
            mClass.get().setData((List<Data>) msg.obj);
        }
    }

}
