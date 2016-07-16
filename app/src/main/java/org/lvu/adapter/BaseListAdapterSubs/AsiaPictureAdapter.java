package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:12 PM.
 */
public class AsiaPictureAdapter extends EuropePictureAdapter {

    public AsiaPictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "https://www.dd99aa.com/htm/piclist1/";
    }
}
