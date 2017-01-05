package org.lvu.main.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;
import org.lvu.adapters.AreaFragmentAdapter;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.video_player.PlayManager;

/**
 * Created by wuyr on 1/5/17 1:24 PM.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected AreaFragmentAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_area_view, container, false);
        init();
        return mRootView;
    }

    protected void init() {
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);

        int[] tabs = getTabs();
        for (int tmp : tabs)
            mTabLayout.addTab(mTabLayout.newTab().setText(tmp));

        mViewPager.setAdapter((mAdapter = new AreaFragmentAdapter(getChildFragmentManager(), getFragments(), getStrings(tabs))));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                PlayManager.getInstance().onlyRelease();
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private String[] getStrings(int[] src) {
        String[] result = new String[src.length];
        for (int i = 0; i < src.length; i++)
            result[i] = getString(src[i]);
        return result;
    }

    @Override
    public void onDetach() {
        saveAdapterData();
        super.onDetach();
    }

    public void saveAdapterData() {
        BaseListFragment[] fragments = mAdapter.getAllFragments();
        for (BaseListFragment tmp : fragments) {
            if (tmp != null) {
                tmp.saveAdapterData();
            }
        }
    }

    public void jumpToPage(int cp) {
        mAdapter.getCurrentFragment().jumpToPage(cp);
    }

    public BaseListFragment getShowingFragment() {
        return mAdapter.getCurrentFragment();
    }

    protected abstract int[] getTabs();

    protected abstract BaseListFragment[] getFragments();

    public void setTabLayoutBackground(Drawable drawable) {
        mTabLayout.setBackground(drawable);
    }
}
