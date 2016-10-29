package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:21 PM.
 */
public class LewdWifeNovelAdapter extends ExcitedNovelAdapter {

    public LewdWifeNovelAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl(){
        return "http://55ex.com/t03/index.html";
    }
}
