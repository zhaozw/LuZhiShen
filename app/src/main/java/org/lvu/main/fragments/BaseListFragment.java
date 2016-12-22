package org.lvu.main.fragments;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.MySnackBar;
import org.lvu.customize.RefreshLayout;
import org.lvu.main.activities.MainActivity;
import org.lvu.models.Data;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wuyr on 4/6/16 2:22 PM.
 */
public abstract class BaseListFragment extends Fragment {

    protected View mRootView;
    protected RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    protected CircleProgressBar mJumpBar;
    protected boolean isJumping;
    protected BaseListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        init();
        return mRootView;
    }

    private void init() {
        initRefreshLayout();
        initAdapter();
        initRecyclerView();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            changeToLandscape();
        else changeToPortrait();
    }

    private void initAdapter() {
        mAdapter = getAdapter();
        mAdapter.setOnSyncDataFinishListener(new BaseListAdapter.OnFinishListener() {
            @Override
            public void onFinish() {
                handleOnFinish();
            }

            @Override
            public void onFailure(String reason) {
                handleOnFailure(reason);
            }
        });
        mAdapter.setOnLoadMoreFinishListener(mAdapter.getOnSyncDataFinishListener());
        mAdapter.setOnRefreshDataFinishListener(mAdapter.getOnSyncDataFinishListener());
        mAdapter.setOnJumpPageFinishListener(mAdapter.getOnSyncDataFinishListener());
        mAdapter.setOnItemClickListener(getOnItemClickListener());
        mAdapter.setOnItemLongClickListener(getOnItemLongClickListener());
        restoreAdapterData();
    }

    private void handleOnFinish() {
        if (getActivity() == null)
            return;
        ((MainActivity) getActivity()).setTotalPages(mAdapter.getTotalPages());
        ((MainActivity) getActivity()).setCurrentPage(mAdapter.getCurrentPage());
        if (mRefreshLayout.isRefreshing())
            mRefreshLayout.setRefreshing(false);
        if (isJumping)
            hideJumpBar();
        mRecyclerView.smoothScrollToPosition(0);
    }

    private boolean flag = true;

    private void handleOnFailure(String reason) {
        if (flag && reason.equals("无可用网络。\t(向右滑动清除)")) {
            if (isJumping) {
                try {
                    mJumpBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isJumping = false;
                mRefreshLayout.setEnabled(true);
            }
        } else if (isJumping)
            hideJumpBar();
        if (mRefreshLayout.isRefreshing())
            mRefreshLayout.setRefreshing(false);
        flag = false;
        MySnackBar.show(mRootView.findViewById(R.id.coordinator), reason, Snackbar.LENGTH_INDEFINITE);
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(getLayoutManager());
       /* mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //用来标记是否正在向最后一个滑动，既是否向下滑动
            boolean isSlidingToLast;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager
                        && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 当不滚动时
                    //获取最后一个完全显示的ItemPosition
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
                    int lastVisiblePos = getMaxElem(lastVisiblePositions);
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部
                    if (!mRefreshLayout.isRefreshing() && isSlidingToLast && !isLoadBarHiding && !isJumping &&
                            lastVisiblePos == totalItemCount) {
                        showJumpBar();
                        mAdapter.loadNext();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                isSlidingToLast = isScrollDown(dy);
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager)
                    if (!mRefreshLayout.isRefreshing() && isSlidingToLast && !isLoadBarHiding && !isJumping &&
                            (((LinearLayoutManager) recyclerView.getLayoutManager())
                                    .findLastVisibleItemPosition() == mAdapter.getDataSize() - 1 ||
                                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                                            .findLastVisibleItemPosition() == mAdapter.getDataSize() - 2)) {
                        showJumpBar();
                        mAdapter.loadNext();
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
    }*/
    }

    private void initRefreshLayout() {
        mJumpBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
        if (ImmerseUtil.isHasNavigationBar(getActivity())) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mJumpBar.getLayoutParams();
            lp.bottomMargin += ImmerseUtil.getNavigationBarHeight(getActivity());
            mJumpBar.setLayoutParams(lp);
        }
        mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.menu_text_color);
        mJumpBar.setColorSchemeResources(R.color.menu_text_color);
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = getActivity().obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorPrimary), getActivity()
                .getResources().getColor(R.color.bluePrimary));
        mRefreshLayout.setProgressBackgroundColorSchemeColor(color);
        a.recycle();
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadPrevious();
            }

            @Override
            public void onLoad() {
                mAdapter.loadNext();
            }
        });
        isJumping = true;
        mRefreshLayout.setEnabled(false);
    }

    private Animation showAnimation;

    private void showJumpBar() {
        isJumping = true;
        if (showAnimation == null)
            initShowAnimation();
        try {
            mJumpBar.startAnimation(showAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initShowAnimation() {
        showAnimation = new TranslateAnimation(0, 0, 150, 0);
        showAnimation.setDuration(250);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mJumpBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private boolean isAnimationNormalEnd;

    private class HideLoadMoreBarThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isAnimationNormalEnd) {
                try {
                    mJumpBar.post(new Runnable() {
                        @Override
                        public void run() {
                            mJumpBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private Animation hideAnimation;

    private void hideJumpBar() {
        if (isJumping || mJumpBar.getVisibility() == View.VISIBLE) {
            isJumping = false;
            mRefreshLayout.setEnabled(true);
            if (hideAnimation == null)
                initHideAnimation();
            try {
                isAnimationNormalEnd = false;
                mJumpBar.startAnimation(hideAnimation);
                threadPool.execute(new HideLoadMoreBarThread());
            } catch (Exception e) {
                try {
                    mJumpBar.setVisibility(View.GONE);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private void initHideAnimation() {
        hideAnimation = new ScaleAnimation(
                1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideAnimation.setDuration(250);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mJumpBar.setVisibility(View.GONE);
                isAnimationNormalEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            changeToLandscape();
        else
            changeToPortrait();
    }

    public void jumpToPage(int page) {
        mAdapter.jumpToPage(page);
        mAdapter.setCurrentPage(page);
        showJumpBar();
        mRefreshLayout.setEnabled(false);
    }

    public void changeToLandscape() {
        /*if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager());
            //int spanCount = manager.getSpanCount()
            int[] firstVisiblePositions = manager.findFirstVisibleItemPositions(new int[manager.getSpanCount()]);
            int currentPos = getMaxElem(firstVisiblePositions);

            StaggeredGridLayoutManager manager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager2);
            manager2.scrollToPosition(currentPos);
        }*/
        if (mAdapter != null)
            mAdapter.changeToLandscape();

    }

    public void changeToPortrait() {
        /*if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager());
            int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
            int currentPos = getMaxElem(lastVisiblePositions);

            StaggeredGridLayoutManager manager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager2);
            manager2.scrollToPosition(currentPos);
        }*/
        if (mAdapter != null)
            mAdapter.changeToPortrait();
    }

    @Override
    public void onDetach() {
        saveAdapterData();
        mAdapter.clearData();
        mAdapter.setOwnerIsDestroyed();
        super.onDetach();
    }

    protected void showDialog(final Data data, String itemName) {
        new AlertDialog.Builder(getActivity()).setItems(new String[]{itemName}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                longClickLogic(data);
            }
        }).show();
    }

    protected abstract void longClickLogic(Data data);

    public abstract void saveAdapterData();

    protected abstract void restoreAdapterData();

    protected abstract BaseListAdapter getAdapter();

    protected abstract BaseListAdapter.OnItemClickListener getOnItemClickListener();

    protected abstract BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener();

    protected abstract RecyclerView.LayoutManager getLayoutManager();
}
