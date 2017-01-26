package org.lvu.adapters.SubAdapters.picture;

import android.content.Context;
import android.net.Uri;

import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.SubBaseAdapter;
import org.lvu.models.Data;
import org.lvu.models.Row;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:27 PM.
 */

public class Picture1Adapter extends SubBaseAdapter {
    public Picture1Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(BaseListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        initItemImage(holder, position);
    }

    @Override
    protected void initItemImage(BaseListAdapter.ViewHolder holder, int position) {
        if (mData.isEmpty())
            return;
        try {
            holder.image.setImageURI(Uri.parse(HttpUtil.handleSpacesUrl(((Row)
                    mData.get(position != 0 && position >= mData.size()
                            ? mData.size() - 1 : position)).getImgs())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return "/artlist/yazhousetu/%s.json";
    }
}
