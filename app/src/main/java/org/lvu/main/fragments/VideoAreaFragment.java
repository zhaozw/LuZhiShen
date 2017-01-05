package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.activities.NewMainActivity;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.ChinaVideoFragment;
import org.lvu.main.fragments.view_pager_content.EuropeVideoFragment;
import org.lvu.main.fragments.view_pager_content.JapanVideoFragment;

/**
 * Created by wuyr on 1/5/17 1:20 PM.
 */

public class VideoAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{R.string.menu_china_video, R.string.menu_japan_video, R.string.menu_europe_video};
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{new ChinaVideoFragment().setActivity((NewMainActivity) getActivity()),
                new JapanVideoFragment().setActivity((NewMainActivity) getActivity()),
                new EuropeVideoFragment().setActivity((NewMainActivity) getActivity())};
    }
}
