package org.lvu.adapters.newAdapters.picture;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:27 PM.
 */

public class Picture2Adapter extends Picture1Adapter {
    public Picture2Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "artlist/24.html";
    }

    @Override
    protected String getPageUrl() {
        return "artlist/24-%s.html";
    }
}
