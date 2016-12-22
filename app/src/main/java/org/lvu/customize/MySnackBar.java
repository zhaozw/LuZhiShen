package org.lvu.customize;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/5/16 10:55 PM.
 */
public class MySnackBar {

    public static void show(@NonNull View view, @NonNull CharSequence text, int duration) {
        show(view, text, duration, null, null);
    }

    public static void show(@NonNull View view, @NonNull CharSequence text, int duration,
                            @Nullable String actionText, @Nullable View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        Snackbar.SnackbarLayout root = (Snackbar.SnackbarLayout) snackbar.getView();
        if (ImmerseUtil.isAboveKITKAT() &&
                ImmerseUtil.isHasNavigationBar(view.getContext()) &&
                Application.getContext().getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {

            /*CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) root.getLayoutParams();
            lp.bottomMargin = ImmerseUtil.getNavigationBarHeight(view.getContext());

            root.setLayoutParams(lp);*/
            root.setPadding(0, 0, 0, ImmerseUtil.getNavigationBarHeight(view.getContext()));
        }
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = view.getContext()
                .obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorPrimary), view.getContext()
                .getResources().getColor(R.color.bluePrimary));

        root.setBackgroundColor(color);
        a.recycle();
        ((TextView) root.findViewById(R.id.snackbar_text)).setTextColor(
                view.getResources().getColor(R.color.menu_text_color));
        if (actionText != null && listener != null) {
            List<Integer> data2 = new ArrayList<>();
            int[] array2 = R.styleable.selector;
            for (int tmp : array2)
                data2.add(tmp);
            TypedArray a2 = view.getContext()
                    .obtainStyledAttributes(R.styleable.selector);
            int color2 = a2.getColor(data2.indexOf(R.attr.color_light), view.getContext()
                    .getResources().getColor(R.color.light_blue));

            a2.recycle();
            snackbar.setAction(actionText, listener).setActionTextColor(color2);
        }
        snackbar.show();
    }
}
