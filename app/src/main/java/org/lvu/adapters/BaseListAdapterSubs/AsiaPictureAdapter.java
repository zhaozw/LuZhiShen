package org.lvu.adapters.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.models.Data;

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
        return "http://fv3333.com/html/part/9.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/9_%s.html";
    }
}
