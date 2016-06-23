package org.lvu.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:12 PM.
 */
public class AsiaPictureAdapter extends BaseListAdapter {

    public AsiaPictureAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void syncData(@NonNull String url) {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void refreshData() {

    }

    @Override
    protected Handler getHandler() {
        return null;
    }
}
