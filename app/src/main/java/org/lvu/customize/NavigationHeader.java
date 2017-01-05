package org.lvu.customize;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.R;

/**
 * Created by wuyr on 12/29/16 1:56 PM.
 */

public class NavigationHeader extends LinearLayout {

    private TextView mTextView;

    public NavigationHeader(Context context) {
        this(context, null);
    }

    public NavigationHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.customize_navigation_header_view, this, true);
        mTextView = (TextView) rootView.findViewById(R.id.text_view);
        mTextView.getPaint().setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/HeaderText.ttf"));
    }

    public NavigationHeader setText(CharSequence s) {
        mTextView.setText(s);
        return this;
    }

    public void setText(@StringRes int resId) {
        mTextView.setText(resId);
    }
}
