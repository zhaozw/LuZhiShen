package org.lvu.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;

/**
 * Created by wuyr on 6/23/16 2:33 PM.
 */
public class NavigationFragment extends Fragment {

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_navigation_view, container, false);
        return mRootView;
    }

    private void initViews() {

    }
}
