package org.lvu.main.fragments;

import org.lvu.R;
import org.lvu.main.activities.NewMainActivity;
import org.lvu.main.fragments.view_pager_content.BaseListFragment;
import org.lvu.main.fragments.view_pager_content.ExcitedNovelFragment;
import org.lvu.main.fragments.view_pager_content.FamilyMessNovelFragment;
import org.lvu.main.fragments.view_pager_content.FunnyJokeFragment;
import org.lvu.main.fragments.view_pager_content.LewdWifeNovelFragment;
import org.lvu.main.fragments.view_pager_content.SchoolNovelFragment;

/**
 * Created by wuyr on 1/5/17 1:21 PM.
 */

public class TextAreaFragment extends BaseFragment {
    @Override
    protected int[] getTabs() {
        return new int[]{R.string.menu_excited_novel, R.string.menu_family_mess_novel,
                R.string.menu_school_novel, R.string.menu_lewd_wife_novel, R.string.menu_funny_joke,};
    }

    @Override
    protected BaseListFragment[] getFragments() {
        return new BaseListFragment[]{new ExcitedNovelFragment().setActivity((NewMainActivity) getActivity()),
                new FamilyMessNovelFragment().setActivity((NewMainActivity) getActivity()),
                new SchoolNovelFragment().setActivity((NewMainActivity) getActivity()),
                new LewdWifeNovelFragment().setActivity((NewMainActivity) getActivity()),
                new FunnyJokeFragment().setActivity((NewMainActivity) getActivity())};
    }
}
