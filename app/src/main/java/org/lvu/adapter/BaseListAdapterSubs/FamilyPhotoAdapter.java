package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:15 PM.
 */
public class FamilyPhotoAdapter extends EuropePictureAdapter {

    public FamilyPhotoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "https://se.haoav24.com/listhtml/1.html";
    }
}
