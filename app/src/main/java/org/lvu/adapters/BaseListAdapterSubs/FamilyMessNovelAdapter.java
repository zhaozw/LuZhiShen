package org.lvu.adapters.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:20 PM.
 */
public class FamilyMessNovelAdapter extends ExcitedNovelAdapter {

    public FamilyMessNovelAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl(){
        return "http://fv3333.com/html/part/22.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/22_%s.html";
    }
}
