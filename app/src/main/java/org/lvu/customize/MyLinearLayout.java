package org.lvu.customize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.main.activities.BaseActivity;

/**
 * Created by wuyr on 1/4/17 4:06 PM.
 */

public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        TextView textView = getTextView();
        if (textView != null) {
            textView.setTextColor(((BaseActivity) getContext()).getThemeColor(selected ? 3 : 0));
            textView.getPaint().setFakeBoldText(selected);
        }
    }

    public TextView getTextView() {
        TextView textView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof TextView) {
                textView = (TextView) getChildAt(i);
                break;
            }
        }
        return textView;
    }
}