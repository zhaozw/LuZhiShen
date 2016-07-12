package org.lvu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.lvu.R;
import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 3:15 PM.
 */
public abstract class BasePictureListAdapter extends BaseListAdapter {

    public BasePictureListAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseListAdapter.ViewHolder holder, int position) {
        holder.image.setImageBitmap(mData.get(position >= mData.size() ? mData.size() - 1 : position).getBitmap());
        super.onBindViewHolder(holder, position);
    }

    protected static class ViewHolder extends BaseListAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
