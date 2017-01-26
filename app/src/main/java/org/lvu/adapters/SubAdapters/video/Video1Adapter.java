package org.lvu.adapters.SubAdapters.video;

import android.content.Context;
import android.net.Uri;

import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.SubAdapters.picture.Picture1Adapter;
import org.lvu.models.Data;
import org.lvu.models.Row;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:26 PM.
 */

public class Video1Adapter extends Picture1Adapter {

    public Video1Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected void initItemImage(BaseListAdapter.ViewHolder holder, int position) {
        if (mData.isEmpty())
            return;
        try {
            holder.image.setImageURI(Uri.parse(HttpUtil.handleSpacesUrl(((Row)
                    mData.get(position != 0 && position >= mData.size()
                            ? mData.size() - 1 : position)).getImg())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return "/vodlist/rihannvyou/%s.json";
    }
}
