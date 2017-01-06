package org.lvu.main.fragments;

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
import org.lvu.main.activities.NewMainActivity;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.utils.LogUtil;
import org.video_player.PlayManager;

/**
 * Created by wuyr on 1/5/17 1:24 PM.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected AreaFragmentAdapter mAdapter;
    private boolean isTabLayoutExpanded = true;
    private ViewPager.SimpleOnPageChangeListener mSimpleOnPageChangeListener;

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
        mViewPager.addOnPageChangeListener((mSimpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LogUtil.print(position);
                int backgroundColor = NewMainActivity.getBackgroundColor();
                if (backgroundColor != -1 && mAdapter.getItem(position) != null) {
                    int themeColor = ((NewMainActivity) getActivity()).getThemeColor(1);
                    LogUtil.print(NewMainActivity.isAppBarExpanded());
                    if (NewMainActivity.isAppBarExpanded() || !NewMainActivity.isScrimsShown()) {
                        if (isTabLayoutExpanded) {
                            mTabLayout.setBackground(NewMainActivity.getAnimation(
                                    ((NewMainActivity) getActivity()).getThemeColor(0), themeColor));
                            isTabLayoutExpanded = false;
                            mAdapter.getItem(position).refreshViewsColor(themeColor, backgroundColor);
                        }
                    } else {
                        if (!isTabLayoutExpanded) {
                            mTabLayout.setBackground(NewMainActivity.getAnimation(themeColor,
                                    ((NewMainActivity) getActivity()).getThemeColor(0)));
                            isTabLayoutExpanded = true;
                            mAdapter.getItem(position).resetViewsColor(backgroundColor, themeColor);
                        }
                    }
                }
                PlayManager.getInstance().onlyRelease();
            }
        }));

        mTabLayout.setupWithViewPager(mViewPager);
        ((NewMainActivity) getActivity()).setOnAppBarExpandedListener(new NewMainActivity.OnAppBarExpandedListener() {
            @Override
            public void onExpanded(int startColor, int endColor) {
                mTabLayout.setBackground(NewMainActivity.getAnimation(
                        ((NewMainActivity) getActivity()).getThemeColor(0), endColor));
                mAdapter.getCurrentFragment().refreshViewsColor(startColor, endColor);
            }

            @Override
            public void onCollapsed(int startColor, int endColor) {
                mTabLayout.setBackground(NewMainActivity.getAnimation(startColor,
                        ((NewMainActivity) getActivity()).getThemeColor(0)));
                mAdapter.getCurrentFragment().resetViewsColor(startColor, endColor);
            }
        });
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
}
