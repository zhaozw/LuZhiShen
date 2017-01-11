package org.lvu.adapters.newAdapters.picture;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:28 PM.
 */

public class Picture4Adapter extends Picture1Adapter {
    public Picture4Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "artlist/27.html";
    }

    @Override
    protected String getPageUrl() {
        return "artlist/27-%s.html";
    }
}
