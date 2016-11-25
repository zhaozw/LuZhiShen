package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 7/16/16 1:18 AM.
 */
public class SchoolNovelAdapter extends ExcitedNovelAdapter {

    public SchoolNovelAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "http://fv3333.com/html/part/19.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/19_%s.html";
    }
}
