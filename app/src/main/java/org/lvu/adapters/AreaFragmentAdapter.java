package org.lvu.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.main.fragments.view_pager_content.BaseListFragment;

/**
 * Created by wuyr on 1/5/17 1:43 PM.
 */

public class AreaFragmentAdapter extends FragmentStatePagerAdapter {

    private BaseListFragment[] mFragments;
    private String[] mTitles;
    private BaseListFragment mShowingFragment;

    public AreaFragmentAdapter(FragmentManager fm, BaseListFragment[] fragments, String[] titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public BaseListFragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mShowingFragment = (BaseListFragment) object;
        mShowingFragment.refreshPagesInfo();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragments[position].saveAdapterData();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    public BaseListFragment[] getAllFragments() {
        return mFragments;
    }

    public BaseListFragment getCurrentFragment() {
        return mShowingFragment;
    }
}
