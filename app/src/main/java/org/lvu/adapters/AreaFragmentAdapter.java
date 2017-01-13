package org.lvu.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import org.lvu.main.fragments.view_pager_content.BaseListFragment;

/**
 * Created by wuyr on 1/5/17 1:43 PM.
 */

public class AreaFragmentAdapter extends FragmentPagerAdapter {

    private BaseListFragment[] mFragments;
    private String[] mTitles;
    private BaseListFragment mShowingFragment;
    private int mCurrentPos;
    private ViewPager.OnPageChangeListener mListener;
    private boolean isInited;

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
        if (!isInited || mCurrentPos != position) {
            mShowingFragment = (BaseListFragment) object;
            mCurrentPos = position;
            if (mListener != null)
                mListener.onPageSelected(position);
            isInited = true;
        }
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener){
        mListener = listener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragments[position].saveAdapterData();
    }

    public BaseListFragment[] getAllFragments() {
        return mFragments;
    }

    public BaseListFragment getCurrentFragment() {
        return mShowingFragment;
    }
}
