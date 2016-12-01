package org.lvu.main.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BaseListAdapterSubs.FunnyJokeAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.main.activities.MoreJokeActivity;
import org.lvu.main.activities.PicturesViewActivity;
import org.lvu.model.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by wuyr on 7/26/16 9:52 PM.
 */
public class FunnyJokeFragment extends BaseListFragment {
    @Override
    protected BaseListAdapter getAdapter() {
        return new FunnyJokeAdapter(getActivity(), R.layout.adapter_joke_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String text, int position) {
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), MoreJokeActivity.class);
                    intent.putExtra(PicturesViewActivity.URL, url);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return new BaseListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(Data item) {
                showDialog(item, getString(R.string.copy));
                return true;
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void longClickLogic(Data data) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_clipboard_label), data.getText());
        clipboard.setPrimaryClip(clip);
        MySnackBar.show(mRootView.findViewById(R.id.coordinator), getString(R.string.copied), Snackbar.LENGTH_SHORT);
    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(FunnyJokeFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(FunnyJokeFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }
}
