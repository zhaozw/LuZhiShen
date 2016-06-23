package org.lvu.main.fragments;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.MySnackBar;
import org.lvu.customize.VideoPlayer;
import org.lvu.main.activities.MainActivity;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/6/16 2:22 PM.
 */
public abstract class BaseListFragment extends Fragment {

    protected View mRootView;
    protected RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    protected CircleProgressBar mLoadMoreBar;
    protected boolean isLoadingMore, isLoadBarHiding;
    protected VideoPlayer mPlayer;
    private BaseListAdapter mAdapter;
    private ProgressDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        //((TextView) mRootView.findViewById(R.id.text)).setText(String.valueOf(((char) (getArguments().getInt(LIST_TYPE) + 19968))));
        init();
        return mRootView;
    }

    private void init() {
        initPlayer();
        initDialog();
        initAdapter();
        initRecyclerView();
        initRefreshLayout();
        ((MainActivity) getActivity()).setOnBackPressedListener(new MainActivity.OnBackPressedListener() {
            @Override
            public boolean onBackPressed() {
                if (mPlayer.getVisibility() == View.GONE)
                    return false;
                else {
                    mPlayer.exit();
                    return true;
                }
            }
        });
    }

    private void initPlayer() {
        mPlayer = (VideoPlayer) mRootView.findViewById(R.id.player);
        mPlayer.setActivity(getActivity());
        mPlayer.setOnPlayCompleteListener(new VideoPlayer.OnPlayCompleteListener() {
            @Override
            public void playComplete() {
                ((MainActivity) getActivity()).setDrawerLockMode(false);
                if (mRecyclerView.getVisibility() != View.VISIBLE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).showToolbar();
            }
        });
    }

    private void initDialog() {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(getActivity().getString(R.string.loading_please_wait));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void initAdapter() {
        mAdapter = getAdapter();
        mAdapter.setOnSyncDataFinishListener(new BaseListAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                mDialog.dismiss();
            }

            @Override
            public void onFailure() {
                mDialog.dismiss();
                MySnackBar.show(getActivity().findViewById(R.id.coordinator),
                        getString(R.string.get_list_failure), Snackbar.LENGTH_INDEFINITE);
            }
        });
        mAdapter.setOnLoadMoreFinishListener(new BaseListAdapter.OnLoadMoreFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }

            @Override
            public void onFailure() {
                hideLoadMoreBar();
                MySnackBar.show(getActivity().findViewById(R.id.coordinator),
                        getString(R.string.get_list_failure), Snackbar.LENGTH_INDEFINITE);
            }
        });
        mAdapter.setOnRefreshDataFinishListener(new BaseListAdapter.OnRefreshDataFinishListener() {
            @Override
            public void onFinish() {
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                mRefreshLayout.setRefreshing(false);
                MySnackBar.show(getActivity().findViewById(R.id.coordinator),
                        getString(R.string.get_list_failure), Snackbar.LENGTH_INDEFINITE);
            }
        });
        mAdapter.setOnItemClickListener(getOnItemClickListener());
        mAdapter.syncData("");
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isScrollDown(dy) && !isLoadBarHiding && !isLoadingMore &&
                        (recyclerView.getLayoutManager() instanceof GridLayoutManager ?
                                ((GridLayoutManager) recyclerView.getLayoutManager()) :
                                ((LinearLayoutManager) recyclerView.getLayoutManager()))
                                .findLastVisibleItemPosition() == mAdapter.getDataSize() - 1) {
                    showLoadMoreBar();
                    mAdapter.loadMore();
                }
            }
        });
    }

    // TODO: 6/18/16 img load failure set a default img
    private void initRefreshLayout() {
        mLoadMoreBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
        if (ImmerseUtil.isHasNavigationBar(getActivity())) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLoadMoreBar.getLayoutParams();
            lp.bottomMargin += ImmerseUtil.getNavigationBarHeight(getActivity());
            mLoadMoreBar.setLayoutParams(lp);
        }
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.menu_text_color);
        mLoadMoreBar.setColorSchemeResources(R.color.menu_text_color);
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = getActivity().obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorPrimary), getActivity()
                .getResources().getColor(R.color.bluePrimary));
        mRefreshLayout.setProgressBackgroundColorSchemeColor(color);
        a.recycle();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refreshData();
            }
        });
    }

    protected boolean isScrollDown(int y) {
        return y > 0;
    }

    private void showLoadMoreBar() {
        isLoadingMore = true;
        TranslateAnimation animation = new TranslateAnimation(0, 0, 150, 0);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadMoreBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadMoreBar.startAnimation(animation);
    }

    private void hideLoadMoreBar() {
        if (isLoadingMore) {
            isLoadBarHiding = true;
            ScaleAnimation animation = new ScaleAnimation(
                    1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(250);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLoadMoreBar.setVisibility(View.GONE);
                    isLoadingMore = false;
                    isLoadBarHiding = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLoadMoreBar.startAnimation(animation);
        }
    }

    protected abstract BaseListAdapter getAdapter();

    protected abstract BaseListAdapter.OnItemClickListener getOnItemClickListener();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

}