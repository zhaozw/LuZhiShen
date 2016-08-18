package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:16 PM.
 */
public class GifPictureAdapter extends EvilComicsAdapter {

    public GifPictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(final BaseListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
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
        if (mData.isEmpty())
            return;
        try {
            holder.image.setImageBitmap(mData.get(position != 0 && position >= mData.size() ? mData.size() - 1 : position).getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return "http://www.lovefou.com/dtt/";
    }

    @Override
    public void syncData(@NonNull String url) {
        if (url.isEmpty())
            url = URL;
        HttpUtil.getGifList(url, mSyncDataCallbackListener);
    }

    @Override
    public void loadMore() {
        if (mNextPageUrl == null || mNextPageUrl.isEmpty())
            syncData("");
        else
            HttpUtil.getGifList(mNextPageUrl, mLoadMoreCallbackListener);
    }

    @Override
    public void refreshData() {
        HttpUtil.getGifList(URL, mRefreshDataCallbackListener);
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }
}
