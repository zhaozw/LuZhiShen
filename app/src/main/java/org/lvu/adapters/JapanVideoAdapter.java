package org.lvu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;

import org.lvu.models.Data;

import java.util.List;

/**
 * Created by wuyr on 4/8/16 9:41 PM.
 */
public class JapanVideoAdapter extends EuropeVideoAdapter {

    public JapanVideoAdapter(Context context, int layoutId, List<Data> data) {
        super(context, layoutId, data);
    }

    @Override
    public void syncData(@NonNull String url) {
        //HttpUtil.getJapanVideoListAsync(1, mSyncDataCallbackListener);
    }

    @Override
    public void loadNext() {
        int page = getCurrentPage() + 1;
        if (page > getTotalPages())
            page = getCurrentPage();
        //HttpUtil.getJapanVideoListAsync(page, mLoadNextCallbackListener);
    }

    @Override
    public void loadPrevious() {
        int page = getCurrentPage() - 1;
        if (page < 1)
            page = 1;
        //HttpUtil.getJapanVideoListAsync(page, mLoadPreviousCallbackListener);
    }

    @Override
    public void jumpToPage(int page) {
        //HttpUtil.getJapanVideoListAsync(page, mOnJumpPageCallbackListener);
    }
}
