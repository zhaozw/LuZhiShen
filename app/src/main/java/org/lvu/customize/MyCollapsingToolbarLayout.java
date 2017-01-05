package org.lvu.customize;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;

/**
 * Created by wuyr on 1/4/17 2:35 PM.
 */

public class MyCollapsingToolbarLayout extends CollapsingToolbarLayout {

    public MyCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, animate);
        if (mListener != null)
            mListener.onChanged(shown);
    }

    private OnScrimsChangedListener mListener;

    public void setOnScrimsCHangedListener(OnScrimsChangedListener listener) {
        mListener = listener;
    }

    public interface OnScrimsChangedListener {
        void onChanged(boolean isShown);
    }
}
