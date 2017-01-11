package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.EvilComicsFragment;
import org.lvu.main.fragments.view_pager_content.GifPictureFragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture1Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture2Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture3Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture4Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture5Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture6Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture7Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture8Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.picture.Picture9Fragment;

/**
 * Created by wuyr on 1/5/17 1:20 PM.
 */

public class PictureAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{
                R.string.picture1,
                R.string.picture2,
                R.string.picture3,
                R.string.picture4,
                R.string.picture5,
                R.string.picture6,
                R.string.picture7,
                R.string.picture8,
                R.string.picture9,
                R.string.menu_evil_pics,
                R.string.menu_gif
        };
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{
                new Picture1Fragment(),
                new Picture2Fragment(),
                new Picture3Fragment(),
                new Picture4Fragment(),
                new Picture5Fragment(),
                new Picture6Fragment(),
                new Picture7Fragment(),
                new Picture8Fragment(),
                new Picture9Fragment(),
                new EvilComicsFragment(),
                new GifPictureFragment()
        };
    }
}
