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
    protected String getUrl(){
        return "https://www.560hu.com/htm/novellist4/";
    }
}
