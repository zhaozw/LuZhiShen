package org.video_player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wuyr on 7/10/16 11:13 PM.
 */
public class ScrollingTextView extends android.support.v7.widget.AppCompatTextView {

    public ScrollingTextView(Context context) {
        super(context);
    }

    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }


}
