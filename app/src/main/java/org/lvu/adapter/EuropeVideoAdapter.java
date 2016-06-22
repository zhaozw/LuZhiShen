package org.lvu.adapter;

import android.app.ProgressDialog;
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

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by wuyr on 6/22/16 10:51 PM.
 */
public class EuropeVideoAdapter extends RecyclerView.Adapter<EuropeVideoAdapter.ViewHolder> {
    private Context mContext;
    private int mLayoutId;
    private List<Data> mData;
    private OnItemClickListener mOnItemClickListener;
    private OnLoadMoreFinishListener mOnLoadMoreFinishListener;
    private OnSyncDataFinishListener mOnSyncDataFinishListener;
    private Handler mHandler;
    private String mNextPageUrl;

    public EuropeVideoAdapter(Context context, int layoutId, List<Data> data) {
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
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage("解析视频地址中，请稍等。。。");
                    dialog.setCancelable(false);
                    dialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.obj = HttpUtil.getEuropeVideoUrlByUrl(
                                    mData.get(holder.getAdapterPosition()).getUrl());
                            message.what = 2;
                            dialog.dismiss();
                            mHandler.sendMessage(message);
                        }
                    }.start();
                }
            });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Data> data) {
        mData = data;
        notifyDataSetChanged();
        if (mOnSyncDataFinishListener != null)
            mOnSyncDataFinishListener.onFinish();
    }

    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = "http://m.fapple.com/videos";
        HttpUtil.getEuropeVideoListAsync(url, new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                Message message = new Message();
                message.obj = data;
                mHandler.sendMessage(message);
                mNextPageUrl = nextPage;
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getEuropeVideoListAsync(mNextPageUrl, new HttpUtil.HttpRequestCallbackListener() {
                @Override
                public void onSuccess(List<Data> data, String nextPage) {
                    Message message = new Message();
                    message.obj = data;
                    message.what = 1;
                    mHandler.sendMessage(message);
                    mNextPageUrl = nextPage;
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

        private WeakReference<EuropeVideoAdapter> mClass;

        public MyHandler(EuropeVideoAdapter clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mClass.get().mData.addAll((List<Data>) msg.obj);
                    mClass.get().notifyItemRangeChanged(
                            mClass.get().getDataSize(), ((List) msg.obj).size());
                    break;
                case 2:
                    mClass.get().mOnItemClickListener.onClick((String) msg.obj, "");
                    break;
                default:
                    mClass.get().setData((List<Data>) msg.obj);
                    break;
            }
            if (mClass.get().mOnLoadMoreFinishListener != null && msg.what != 2)
                mClass.get().mOnLoadMoreFinishListener.onFinish();
        }
    }
}
