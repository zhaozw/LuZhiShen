package org.lvu.adapters.SubAdapters.picture;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:27 PM.
 */

public class Picture3Adapter extends Picture1Adapter {
    public Picture3Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "/artlist/toukuizipai/%s.json";
    }
}
