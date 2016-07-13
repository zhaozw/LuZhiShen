package org.lvu.main.fragments;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

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
    protected boolean isLoadingMore = true, isLoadBarHiding;
    protected VideoPlayer mPlayer;
    private BaseListAdapter mAdapter;

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
                    if (isLoadingMore)
                        showLoadMoreBar();
                    return true;
                }
            }
        });
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE)
            changeToLandscape();
        else changeToPortrait();
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
                if (isLoadingMore)
                    showLoadMoreBar();
            }
        });
    }

    private void initAdapter() {
        mAdapter = getAdapter();
        mAdapter.setOnSyncDataFinishListener(new BaseListAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }

            @Override
            public void onFailure() {
                if (!isDetached()) {
                    hideLoadMoreBar();
                    MySnackBar.show(mRootView.findViewById(R.id.coordinator),
                            getString(R.string.get_data_failure), Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        mAdapter.setOnLoadMoreFinishListener(new BaseListAdapter.OnLoadMoreFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }

            @Override
            public void onFailure() {
                if (!isDetached()) {
                    hideLoadMoreBar();
                    MySnackBar.show(mRootView.findViewById(R.id.coordinator),
                            getString(R.string.get_data_failure), Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        mAdapter.setOnRefreshDataFinishListener(new BaseListAdapter.OnRefreshDataFinishListener() {
            @Override
            public void onFinish() {
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                if (!isDetached()) {
                    mRefreshLayout.setRefreshing(false);
                    MySnackBar.show(mRootView.findViewById(R.id.coordinator),
                            getString(R.string.get_data_failure), Snackbar.LENGTH_INDEFINITE);
                }
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

            //用来标记是否正在向最后一个滑动，既是否向下滑动
            boolean isSlidingToLast;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    // 当不滚动时
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //获取最后一个完全显示的ItemPosition
                        int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
                        int lastVisiblePos = getMaxElem(lastVisiblePositions);
                        int totalItemCount = manager.getItemCount();

                        // 判断是否滚动到底部
                        if (isSlidingToLast && !isLoadBarHiding && !isLoadingMore &&
                                lastVisiblePos == (totalItemCount - 1)) {
                            showLoadMoreBar();
                            mAdapter.loadMore();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                isSlidingToLast = isScrollDown(dy);
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager)
                    if (isSlidingToLast && !isLoadBarHiding && !isLoadingMore &&
                            ((LinearLayoutManager) recyclerView.getLayoutManager())
                                    .findLastVisibleItemPosition() == mAdapter.getDataSize() - 1) {
                        showLoadMoreBar();
                        mAdapter.loadMore();
                    }
            }
        });
    }

    private int getMaxElem(int[] arr) {
        int maxVal = Integer.MIN_VALUE;
        for (int anArr : arr)
            if (anArr > maxVal)
                maxVal = anArr;
        return maxVal;
    }

    protected boolean isScrollDown(int y) {
        return y > 0;
    }

    // TODO: 6/18/16 img load failure set a default img
    private void initRefreshLayout() {
        mLoadMoreBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
        if (ImmerseUtil.isHasNavigationBar(getActivity())) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mLoadMoreBar.getLayoutParams();
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
        if (isLoadingMore || mLoadMoreBar.getVisibility() == View.VISIBLE) {
            isLoadingMore = false;
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
                    isLoadBarHiding = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLoadMoreBar.startAnimation(animation);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            changeToLandscape();
        else
            changeToPortrait();
    }

    public void changeToLandscape() {
        if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager)
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    3, StaggeredGridLayoutManager.VERTICAL));
        if (mAdapter != null)
            mAdapter.changeToLandscape();
    }

    public void changeToPortrait() {
        if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager)
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    2, StaggeredGridLayoutManager.VERTICAL));
        if (mAdapter != null)
            mAdapter.changeToPortrait();
    }

    protected abstract BaseListAdapter getAdapter();

    protected abstract BaseListAdapter.OnItemClickListener getOnItemClickListener();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

}
