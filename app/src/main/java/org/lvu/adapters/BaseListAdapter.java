package org.lvu.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.lvu.R;
import org.lvu.adapters.newAdapters.video.Video1Adapter;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;
import org.video_player.VideoPlayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:39 PM.
 */
public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListAdapter.ViewHolder> {

    public static final int SYNC_DATA_SUCCESS = 0, LOAD_MORE_SUCCESS = 1, REFRESH_DATA_SUCCESS = 2,
            SYNC_DATA_FAILURE = 3, LOAD_MORE_FAILURE = 4, REFRESH_DATA_FAILURE = 5,
            JUMP_PAGE_SUCCESS = 6, JUMP_PAGE_FAILURE = 7;
    protected final String URL;

    //item类型
    protected static final int ITEM_TYPE_CONTENT = 0, ITEM_TYPE_BOTTOM = 1;
    private int mBottomCount;//底部View个数
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected int mLayoutId;
    protected List<Data> mData;
    private OnItemClickListener mOnItemClickListener, mOnFavoritesOnClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    protected OnFinishListener mOnLoadNextFinishListener, mOnSyncDataFinishListener,
            mOnLoadPreviousFinishListener, mOnJumpPageFinishListener;
    protected String mNextPageUrl;
    protected HttpUtil.HttpRequestCallbackListener mSyncDataCallbackListener,
            mLoadNextCallbackListener, mLoadPreviousCallbackListener, mOnJumpPageCallbackListener;
    private Handler mHandler;
    private boolean isOwnerDestroyed;
    private int mCurrentPage = -1, mCurrentPageTmp = -1, mTotalPages = -1;

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
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(SYNC_DATA_FAILURE, reason);
            }
        };
        mLoadNextCallbackListener = new HttpUtil.HttpRequestCallbackListener() {

            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(LOAD_MORE_SUCCESS, data, nextPage);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(LOAD_MORE_FAILURE, reason);
            }
        };
        mLoadPreviousCallbackListener = new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(REFRESH_DATA_SUCCESS, data, nextPage);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(REFRESH_DATA_FAILURE, reason);
            }
        };
        mOnJumpPageCallbackListener = new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String nextPage) {
                if (!isOwnerDestroyed)
                    sendSuccessMessage(JUMP_PAGE_SUCCESS, data, nextPage);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (!isOwnerDestroyed)
                    sendFailureMessage(JUMP_PAGE_FAILURE, reason);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!handleFooterHolder(holder))
            initDefaultItemData(holder, position);
    }

    void initDefaultItemData(final ViewHolder holder, int position) {
        if (mData.isEmpty())
            return;
        try {
            Data item = mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position);
            holder.text.setText(item.getText());
            if (!item.getDate().isEmpty())
                holder.date.setText(item.getDate());
            holder.favorites.setImageResource(item.isFavorites() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
            initItemOnClickListener(holder);
            initItemLongClickListener(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initItemOnClickListener(final ViewHolder holder) {
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
        // TODO: 1/10/17 handle on favorites on click
            /*holder.favorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnFavoritesOnClickListener != null)
                        mOnFavoritesOnClickListener.onClick();
                }
            });*/
    }

    void initItemLongClickListener(final ViewHolder holder) {
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
    }

    protected boolean handleFooterHolder(ViewHolder holder) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            LinearLayout.LayoutParams bottomLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ImmerseUtil.getNavigationBarHeight(mContext));
            footerHolder.bottomView.setLayoutParams(bottomLP);
            return true;
        }
        return false;
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

    private void addData(List<Data> data, int what) {
        if (data != null && !data.isEmpty()) {
            mCurrentPage = data.get(0).getCurrentPage();
            mTotalPages = data.get(0).getTotalPages();
            mData = data;
            notifyItemChanged(0, data.size());
            /*notifyDataSetChanged();
            for (int i = 0; i < data.size(); i++) {
                mData.add(data.get(i));
                notifyItemInserted(i);
            }*/

            switch (what) {
                case SYNC_DATA_SUCCESS:
                    if (mOnSyncDataFinishListener != null)
                        mOnSyncDataFinishListener.onFinish();
                    break;
                case LOAD_MORE_SUCCESS:
                    if (mOnLoadNextFinishListener != null)
                        mOnLoadNextFinishListener.onFinish();
                    break;
                case REFRESH_DATA_SUCCESS:
                    if (mOnLoadPreviousFinishListener != null)
                        mOnLoadPreviousFinishListener.onFinish();
                    break;
                case JUMP_PAGE_SUCCESS:
                    if (mOnJumpPageFinishListener != null)
                        mOnJumpPageFinishListener.onFinish();
                    break;
                default:
                    break;
            }
        }
    }

    public void saveDataToStorage(BufferedWriter writer) {
        if (writer == null) return;
        if (mData == null || mData.isEmpty()) {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        mData.get(0).setNextPageUrl(mNextPageUrl);
        mData.get(0).setCurrentPage(mCurrentPage);
        mData.get(0).setTotalPages(mTotalPages);
        Gson gson = new Gson();
        String json = gson.toJson(mData);
        try {
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreDataFromStorage(BufferedReader reader) {
        restoreDataFromStorage(reader, new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> data, String args) {
                mSyncDataCallbackListener.onSuccess(data, args);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                syncData("");
            }
        });
    }

    protected void restoreDataFromStorage(final BufferedReader reader, final HttpUtil.HttpRequestCallbackListener listener) {
        if (reader == null) return;
        new Thread() {
            @Override
            public void run() {
                try {
                    List<Data> result;
                    String json;
                    Gson gson = new Gson();
                    String tmp;
                    StringBuilder sb = new StringBuilder();
                    while ((tmp = reader.readLine()) != null)
                        sb.append(tmp);
                    json = sb.toString();
                    result = gson.fromJson(json, new TypeToken<ArrayList<Data>>() {
                    }.getType());
                    listener.onSuccess(result, result.get(0).getNextPageUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure(e, "");
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public abstract void syncData(@NonNull String url);

    public abstract void loadNext();

    public abstract void loadPrevious();

    public abstract void jumpToPage(int page);

    protected abstract String getUrl();

    protected abstract String getPageUrl();

    protected abstract Handler getHandler();

    int getDataSize() {
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

    public int getCurrentPage() {
        if (mCurrentPageTmp != -1) {
            int tmp = mCurrentPageTmp;
            mCurrentPageTmp = -1;
            return tmp;
        }
        return mCurrentPage;
    }

    public BaseListAdapter.ViewHolder getHolderByPosition(RecyclerView recyclerView, int position) {
        int firstItemPosition;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else {
            int[] pos = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPositions(null);
            firstItemPosition = Math.max(pos[0], pos[1]);
        }
        if (position - firstItemPosition >= 0) {
            //得到要更新的item的view
            View view = recyclerView.getChildAt(position - firstItemPosition);
            if (view == null)
                return null;
            if (null != recyclerView.getChildViewHolder(view))
                return (BaseListAdapter.ViewHolder) recyclerView.getChildViewHolder(view);
        }
        return null;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPageTmp = currentPage;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setOwnerIsDestroyed(boolean isDestroyed) {
        isOwnerDestroyed = isDestroyed;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(String url, String text, int position);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(Data item);
    }

    public void setOnSyncDataFinishListener(OnFinishListener listener) {
        mOnSyncDataFinishListener = listener;
    }

    public void setOnLoadMoreFinishListener(OnFinishListener listener) {
        mOnLoadNextFinishListener = listener;
    }

    public void setOnRefreshDataFinishListener(OnFinishListener listener) {
        mOnLoadPreviousFinishListener = listener;
    }

    public void setOnJumpPageFinishListener(OnFinishListener listener) {
        mOnJumpPageFinishListener = listener;
    }

    public OnFinishListener getOnSyncDataFinishListener() {
        return mOnSyncDataFinishListener;
    }

    public interface OnFinishListener {
        void onFinish();

        void onFailure(String reason);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public TextView text, date;
        public ImageView image;
        public ImageView favorites;
        public VideoPlayer player;
        public View progress;
        public boolean isBgColorChanged;

        ViewHolder(View itemView) {
            super(itemView);
            root = (CardView) itemView.findViewById(R.id.card_view);
            text = (TextView) itemView.findViewById(R.id.text);
            favorites = (ImageView) itemView.findViewById(R.id.favorites);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    public static class FooterHolder extends Video1Adapter.ViewHolder {

        View bottomView;

        public FooterHolder(View itemView) {
            super(itemView);
            bottomView = itemView.findViewById(R.id.navigation_bar_view);
        }
    }

    public static class DefaultHandler extends Handler {

        protected WeakReference<BaseListAdapter> mClass;

        public DefaultHandler(BaseListAdapter clazz) {
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
                    break;
                default:
                    break;
            }
        }
    }
}
