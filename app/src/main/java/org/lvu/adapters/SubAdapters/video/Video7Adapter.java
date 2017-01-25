package org.lvu.adapters.SubAdapters.video;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:27 PM.
 */

public class Video7Adapter extends Video1Adapter {
    public Video7Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "/vodlist/biantailinglei/%s.json";
    }
}
