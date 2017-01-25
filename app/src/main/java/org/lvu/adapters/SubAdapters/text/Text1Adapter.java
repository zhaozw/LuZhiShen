package org.lvu.adapters.SubAdapters.text;

import android.content.Context;

import org.lvu.adapters.SubAdapters.SubBaseAdapter;
import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 1/10/17 9:29 PM.
 */

public class Text1Adapter extends SubBaseAdapter {
    public Text1Adapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    protected String getUrl() {
        return "/artlist/dushiyanqing/%s.json";
    }

}
