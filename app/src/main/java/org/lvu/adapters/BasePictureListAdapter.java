package org.lvu.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.lvu.R;
import org.lvu.models.Data;
import org.lvu.utils.ImmerseUtil;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 3:15 PM.
 */
public abstract class BasePictureListAdapter extends BaseListAdapter {

    protected ImageLoader mImageLoader;

    BasePictureListAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
        mImageLoader = ImageLoader.getInstance();
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
    public void onBindViewHolder(BaseListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        initItemImage(holder, position);
    }

    protected void initItemImage(final BaseListAdapter.ViewHolder holder, int position) {
        if (mData.isEmpty())
            return;
        try {
            holder.image.setImageResource(R.drawable.ic_pic_loading);
            mImageLoader.loadImage(mData.get(position != 0 && position >= mData.size() ?
                            mData.size() - 1 : position).getSrc(),
                    new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build(),
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.image.setImageResource(R.drawable.ic_pic_bad);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.image.setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends BaseListAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
