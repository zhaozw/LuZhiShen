package org.lvu.adapter.BaseListAdapterSubs;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.lvu.adapter.BaseListAdapter;
import org.lvu.model.Data;

import java.util.List;

/**
 * Created by wuyr on 6/23/16 6:18 PM.
 */
public class ExcitedNovelAdapter extends BaseListAdapter {

    public ExcitedNovelAdapter(Context context, int layoutId, List<Data> data) {
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
