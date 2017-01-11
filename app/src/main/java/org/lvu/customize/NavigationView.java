package org.lvu.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.lvu.R;
import org.lvu.adapters.NavigationViewAdapter;
import org.lvu.models.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 12/27/16 9:58 PM.
 */

public class NavigationView extends LinearLayout {

    private View mRootView;
    private NavigationViewAdapter mAdapter;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mRootView = layoutInflater.inflate(R.layout.customize_navigation_view, this, true);
        ViewGroup container = (ViewGroup) mRootView.findViewById(R.id.root_view);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.NavigationView, defStyleAttr, 0);
        if (a.hasValue(R.styleable.NavigationView_header)) {
            View header = layoutInflater.inflate(a.getResourceId(R.styleable.NavigationView_header, 0), container, false);
            if (header != null) {
                //setHeader(header);
                setHeader(context);
            }
        }
        a.recycle();
        init();
    }
/*
    private void initBottomView() {
        mBottomView = mRootView.findViewById(R.id.navigation_bar_view);
        LayoutParams bottomLP = new LayoutParams(
                LayoutParams.MATCH_PARENT, ImmerseUtil.getNavigationBarHeight(getContext()));
        mBottomView.setLayoutParams(bottomLP);
    }*/

    private void init() {
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter((mAdapter = new NavigationViewAdapter(getContext(), R.layout.customize_menu_item, initData())));
    }

    private List<Menu> initData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_nav, R.string.area_index));
        result.add(new Menu(R.drawable.ic_video, R.string.area_video));
        result.add(new Menu(R.drawable.ic_pic, R.string.area_picture));
        result.add(new Menu(R.drawable.ic_book, R.string.area_text));
        result.add(new Menu(R.drawable.ic_favorite_selected, R.string.favorites));
        result.add(new Menu(R.drawable.ic_menu_download, R.string.download_manager));
        result.add(new Menu(R.drawable.ic_menu_skin, R.string.change_skin));
        result.add(new Menu(R.drawable.ic_settings, R.string.settings));
        result.add(new Menu(R.drawable.ic_menu_exit, R.string.exit));
        return result;
    }

    public void changeToLandscape() {
        /*if (mBottomView != null)
            mBottomView.setVisibility(View.GONE);*/
        if (mAdapter != null)
            mAdapter.changeToLandscape();
    }

    public void changeToPortrait() {
        /*if (mBottomView != null)
            mBottomView.setVisibility(View.VISIBLE);*/
        if (mAdapter != null)
            mAdapter.changeToPortrait();
    }

    public void setHeader(Context context) {
        //if (!isHasHeader)
        ((LinearLayout) mRootView.findViewById(R.id.root_view)).addView(new NavigationHeader(context).setText("垂死病中惊坐起\n突然想看撸至深\n洛阳亲友如相问\n就说我在撸至深"), 0);
    }

    public void setOnItemClickListener(NavigationViewAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }
/*
    public void setHeader(View header) {
        if (!isHasHeader) {
            mHeaderView = header;
            ((LinearLayout) mRootView.findViewById(R.id.root_view)).addView(header, 0);
            isHasHeader = true;
        }
    }*/
}
