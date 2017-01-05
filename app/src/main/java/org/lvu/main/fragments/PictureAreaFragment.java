package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.activities.NewMainActivity;
import org.lvu.main.fragments.view_pager_content.AsiaPictureFragment;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.EuropePictureFragment;
import org.lvu.main.fragments.view_pager_content.EvilComicsFragment;
import org.lvu.main.fragments.view_pager_content.FamilyPhotoFragment;
import org.lvu.main.fragments.view_pager_content.GifPictureFragment;

/**
 * Created by wuyr on 1/5/17 1:20 PM.
 */

public class PictureAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{R.string.menu_family_pic, R.string.menu_asia_pic,
                R.string.menu_europe_pic, R.string.menu_evil_pics, R.string.menu_gif};
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{new FamilyPhotoFragment().setActivity((NewMainActivity) getActivity()),
                new AsiaPictureFragment().setActivity((NewMainActivity) getActivity()),
                new EuropePictureFragment().setActivity((NewMainActivity) getActivity()),
                new EvilComicsFragment().setActivity((NewMainActivity) getActivity()),
                new GifPictureFragment().setActivity((NewMainActivity) getActivity())};
    }
}
