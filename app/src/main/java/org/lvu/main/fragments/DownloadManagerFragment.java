package org.lvu.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lvu.R;

/**
 * Created by wuyr on 12/1/16 7:22 PM.
 */

public class DownloadManagerFragment extends Fragment {

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_manager_view, container, false);
        init();
        return mRootView;
    }

    private void init() {

    }
}
