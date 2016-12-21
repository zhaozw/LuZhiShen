package org.lvu.customize;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by wuyr on 12/21/16 2:24 PM.
 */

public class NavigationLayoutManager extends LinearLayoutManager {

    public NavigationLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
