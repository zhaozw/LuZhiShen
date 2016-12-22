package org.lvu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class ChinaVideoAdapter extends EuropeVideoAdapter {

    public ChinaVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void syncData(@NonNull String url) {
        HttpUtil.getChinaVideoListAsync(1, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        int page = getCurrentPage() + 1;
        if (page > getTotalPages())
            page = getCurrentPage();
        HttpUtil.getChinaVideoListAsync(page, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page < 1)
            page = 1;
        HttpUtil.getChinaVideoListAsync(page, mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        HttpUtil.getChinaVideoListAsync(page, mOnJumpPageCallbackListener);
    }
}
