package org.lvu.customize;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by wuyr on 1/12/17 2:31 PM.
 */

public class MyStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    private static final String TAG = "MyStaggeredGridLayoutMa";

    public MyStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state,
                                                 LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
        }catch (Exception e){
            Log.e(TAG, "collectAdjacentPrefetchPositions: ", e);
        }
    }
}
