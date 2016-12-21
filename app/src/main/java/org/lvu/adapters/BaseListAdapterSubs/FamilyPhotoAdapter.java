package org.lvu.adapters.BaseListAdapterSubs;

import android.content.Context;

import org.lvu.models.Data;

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
        return "http://fv3333.com/html/part/10.html";
    }

    @Override
    protected String getPageUrl() {
        return "http://fv3333.com/html/part/10_%s.html";
    }
}
