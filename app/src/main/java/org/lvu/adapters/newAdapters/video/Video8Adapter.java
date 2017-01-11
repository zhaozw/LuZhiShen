package org.lvu.adapters.newAdapters.video;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:27 PM.
 */

public class Video8Adapter extends Video1Adapter {
    public Video8Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "vodlist/11.html";
    }

    @Override
    protected String getPageUrl() {
        return "vodlist/11-%s.html";
    }
}
