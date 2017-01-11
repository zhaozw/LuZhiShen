package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.FunnyJokeFragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text1Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text2Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text3Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text4Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text5Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text6Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text7Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text8Fragment;
import org.lvu.main.fragments.view_pager_content.new_content_fragments.text.Text9Fragment;

/**
 * Created by wuyr on 1/5/17 1:21 PM.
 */

public class TextAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{
                R.string.text1,
                R.string.text2,
                R.string.text3,
                R.string.text4,
                R.string.text5,
                R.string.text6,
                R.string.text7,
                R.string.text8,
                R.string.text9,
                R.string.menu_funny_joke
        };
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{
                new Text1Fragment(),
                new Text2Fragment(),
                new Text3Fragment(),
                new Text4Fragment(),
                new Text5Fragment(),
                new Text6Fragment(),
                new Text7Fragment(),
                new Text8Fragment(),
                new Text9Fragment(),
                new FunnyJokeFragment()
        };
    }
}
