package org.lvu.main.fragments;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.lvu.R;
import org.lvu.adapter.ChinaVideoAdapter;
import org.lvu.adapter.JapanVideoAdapter;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.VideoPlayer;
import org.lvu.main.activity.MainActivity;
import org.lvu.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/6/16 2:22 PM.
 */
public class ListFragment extends Fragment {

    public static final String LIST_TYPE = "listType";
    public static final int NAVIGATION = 0, CHINA_V = 1, EUROPE_V = 2, JAPAN_V = 3, FAMILY_P = 4,
            ASIA_P = 5, EUROPE_P = 6, EVIL_P = 7, JAPAN_P = 8,
            GIF = 9, EXCITED_N = 10, FAMILY_N = 11, WIFE_N = 12;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private CircleProgressBar mLoadMoreBar;
    private String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        //((TextView) mRootView.findViewById(R.id.text)).setText(String.valueOf(((char) (getArguments().getInt(LIST_TYPE) + 19968))));
        init(inflater, container);
        return mRootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
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
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            switch (bundle.getInt(LIST_TYPE, -1)) {
                case NAVIGATION:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(new ChinaVideoAdapter(getActivity(),
                            R.layout.list_item, new ArrayList<Data>()));

                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case CHINA_V:
                    initChinaV();
                    break;
                case EUROPE_V:
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case JAPAN_V:
                    initJapanV();
                    break;
                case FAMILY_P:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case ASIA_P:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case EUROPE_P:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case EVIL_P:
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case JAPAN_P:
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case GIF:
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case EXCITED_N:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case FAMILY_N:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                case WIFE_N:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    url = "";
                    mRootView = inflater.inflate(R.layout.temp, container, false);
                    break;
                default:
                    break;
            }
        }
        //initRefreshLayout();
    }

    private boolean isLoadingMore, isLoadBarHiding;
    private VideoPlayer mPlayer;
    private ChinaVideoAdapter mChinaVideoAdapter;
    private JapanVideoAdapter mJapanVideoAdapter;

    private void initChinaV() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中，请稍等。。。");
        dialog.setCancelable(false);
        dialog.show();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        url = "";
        mLoadMoreBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
        mPlayer = (VideoPlayer) mRootView.findViewById(R.id.player);
        mPlayer.setActivity(getActivity());
        mPlayer.setOnScreenOrientationChangedListener(new VideoPlayer.OnScreenOrientationChangedListener() {
            @Override
            public void onChangedToPortrait() {
                if (mRecyclerView.getVisibility() != View.VISIBLE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).showToolbar();
            }

            @Override
            public void onChangeToLandscape() {

            }
        });
        mChinaVideoAdapter = new ChinaVideoAdapter(getActivity(), R.layout.list_item, new ArrayList<Data>());
        mChinaVideoAdapter.setOnLoadMoreFinishListener(new ChinaVideoAdapter.OnLoadMoreFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }
        });
        mChinaVideoAdapter.setOnSyncDataFinishListener(new ChinaVideoAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
        mRecyclerView.setAdapter(mChinaVideoAdapter);
        mChinaVideoAdapter.syncData("");
        mChinaVideoAdapter.setOnItemClickListener(new ChinaVideoAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                if (mLoadMoreBar.getVisibility() == View.VISIBLE)
                    mLoadMoreBar.setVisibility(View.GONE);
                ((MainActivity)getActivity()).hideToolbar();
                mPlayer.setUrlPlay(url);
                mPlayer.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isScrollDown(dy) && !isLoadBarHiding && !isLoadingMore &&
                        ((GridLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition() == mChinaVideoAdapter.getDataSize() - 1) {
                    showLoadMoreBar();
                    mChinaVideoAdapter.loadMore();
                }
            }
        });
    }

    private void initJapanV() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中，请稍等。。。");
        dialog.setCancelable(false);
        dialog.show();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        url = "";
        mLoadMoreBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
        mPlayer = (VideoPlayer) mRootView.findViewById(R.id.player);
        mPlayer.setActivity(getActivity());
        mPlayer.setOnScreenOrientationChangedListener(new VideoPlayer.OnScreenOrientationChangedListener() {
            @Override
            public void onChangedToPortrait() {
                if (mRecyclerView.getVisibility() != View.VISIBLE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).showToolbar();
            }

            @Override
            public void onChangeToLandscape() {

            }
        });
        mJapanVideoAdapter = new JapanVideoAdapter(getActivity(), R.layout.list_item, new ArrayList<Data>());
        mJapanVideoAdapter.setOnLoadMoreFinishListener(new JapanVideoAdapter.OnLoadMoreFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }
        });
        mJapanVideoAdapter.setOnSyncDataFinishListener(new JapanVideoAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
        mRecyclerView.setAdapter(mJapanVideoAdapter);
        mJapanVideoAdapter.syncData("");
        mJapanVideoAdapter.setOnItemClickListener(new JapanVideoAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                if (mLoadMoreBar.getVisibility() == View.VISIBLE)
                    mLoadMoreBar.setVisibility(View.GONE);
                ((MainActivity)getActivity()).hideToolbar();
                mPlayer.setUrlPlay(url);
                mPlayer.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isScrollDown(dy) && !isLoadBarHiding && !isLoadingMore &&
                        ((GridLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition() == mJapanVideoAdapter.getDataSize() - 1) {
                    showLoadMoreBar();
                    mJapanVideoAdapter.loadMore();
                }
            }
        });
    }

    // TODO: 6/18/16 img load failure set a default img
    private void initRefreshLayout() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.menu_text_color);
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
                refreshData();
            }
        });

        mLoadMoreBar = (CircleProgressBar) mRootView.findViewById(R.id.progressbar);
    }

    /**
     * 判断是否向下滑动
     *
     * @param y 滑动值
     * @return
     */
    private boolean isScrollDown(int y) {
        return y > 0;
    }

    public void showLoadMoreBar() {
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

    public void hideLoadMoreBar() {
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

    private void refreshData() {

    }
}
