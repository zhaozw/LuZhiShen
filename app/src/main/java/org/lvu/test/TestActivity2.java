package org.lvu.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import org.lvu.R;
import org.lvu.adapter.ChinaVideoAdapter;
import org.lvu.customize.CircleProgressBar;
import org.lvu.customize.VideoPlayer;
import org.lvu.model.Data;

import java.util.ArrayList;

import io.vov.vitamio.Vitamio;

/**
 * Created by wuyr on 6/8/16 7:33 PM.
 */
public class TestActivity2 extends AppCompatActivity {

    private ChinaVideoAdapter mAdapter;
    private CircleProgressBar mLoadMoreBar;
    private boolean isLoadingMore, isLoadBarHiding;
    private VideoPlayer mPlayer;
    private RecyclerView mRecyclerView;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_test2_view);
        initViews();
    }

    private void initViews() {
        mLoadMoreBar = (CircleProgressBar) findViewById(R.id.progressbar);
        mPlayer = (VideoPlayer) findViewById(R.id.player);
        mPlayer.setActivity(this);
        mPlayer.setOnPlayCompleteListener(new VideoPlayer.OnPlayCompleteListener() {

            @Override
            public void playComplete() {
                if (mRecyclerView.getVisibility() != View.VISIBLE)
                    mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new ChinaVideoAdapter(this, R.layout.adapter_picture_list_item, new ArrayList<Data>());
        mAdapter.setOnLoadMoreFinishListener(new ChinaVideoAdapter.OnLoadMoreFinishListener() {
            @Override
            public void onFinish() {
                hideLoadMoreBar();
            }

            @Override
            public void onFailure(String reason) {

            }

        });
        mAdapter.setOnSyncDataFinishListener(new ChinaVideoAdapter.OnSyncDataFinishListener() {
            @Override
            public void onFinish() {
                mDialog.dismiss();
                mDialog = null;
            }

            @Override
            public void onFailure(String reason) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.syncData("");
        mAdapter.setOnItemClickListener(new ChinaVideoAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String title) {
                if (mLoadMoreBar.getVisibility() == View.VISIBLE)
                    mLoadMoreBar.setVisibility(View.GONE);
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
                                .findLastVisibleItemPosition() == mAdapter.getDataSize() - 1) {
                    showLoadMoreBar();
                    mAdapter.loadMore();
                }
            }
        });
        /*final VideoPlayer player = (VideoPlayer) findViewById(R.id.player);
        player.setActivity(this);
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setUrlPlay("http://sss.ws6f.com:7799/video/category/uploa14ds/article/52dmmh2kkc5.mp4");
                player.setVisibility(View.VISIBLE);
            }
        });*/
        /*mDialog = new ProgressDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.progress_dialog_view, null))
                .setPositiveButton("如果你等了好久还没加载出来请按下这个按钮", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recreate();
                    }
                }).setCancelable(false).show();*/
    }

    @Override
    public void onBackPressed() {
        if (mPlayer.getVisibility() == View.GONE)
            super.onBackPressed();
        else mPlayer.exit();
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
}
