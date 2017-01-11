package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video1Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video2Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video3Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video4Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video5Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video6Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video7Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video8Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.video.Video9Fragment;

/**
 * Created by wuyr on 1/5/17 1:20 PM.
 */

public class VideoAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{
                R.string.video1,
                R.string.video2,
                R.string.video3,
                R.string.video4,
                R.string.video5,
                R.string.video6,
                R.string.video7,
                R.string.video8,
                R.string.video9,
        };
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{
                new Video1Fragment(),
                new Video2Fragment(),
                new Video3Fragment(),
                new Video4Fragment(),
                new Video5Fragment(),
                new Video6Fragment(),
                new Video7Fragment(),
                new Video8Fragment(),
                new Video9Fragment(),
        };
    }
}
