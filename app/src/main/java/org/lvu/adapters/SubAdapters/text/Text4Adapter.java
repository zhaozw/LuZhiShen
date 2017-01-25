package org.lvu.adapters.SubAdapters.text;

import android.content.Context;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:29 PM.
 */

public class Text4Adapter extends Text1Adapter {
    public Text4Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "/artlist/gudianwuxia/%s.json";
    }
}
