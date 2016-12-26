package org.lvu.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.lvu.R;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;
import org.video_player.PlayManager;

import java.util.List;

/**
 * Created by wuyr on 12/22/16 11:55 PM.
 */

public class VideoPlayer extends org.video_player.VideoPlayer {

    private boolean isLoadedUrl;
    private VideoPlayerManager mVideoManager;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVideoManager = VideoPlayerManager.getInstance();
        mVideoManager.add(this);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null)
                    mOnClickListener.onClick(view);
                if (handleOnClick()) return;
                int i = view.getId();
                if (i == R.id.fullscreen_button) {
                    if (isFullScreenNow)
                        exitFullScreen();
                    else
                        enterFullScreen();
                    return;
                }
                if (i == R.id.play_status) {
                    switch (mManager.getPlayStatus()) {
                        case NORMAL:
                        case ERROR:
                            prepareAsync();
                            break;
                        case PLAYING:
                            pause();
                            mPlayStatusButton.setImageResource(R.drawable.ic_play);
                            break;
                        case PAUSE:
                            play();
                            mPlayStatusButton.setImageResource(R.drawable.ic_pause);
                            break;
                        default:
                    }
                } else if (i == R.id.download_button) {
                    // TODO: 12/21/16 handle download
                    // TODO: 12/22/16 activity stop player releases
                }
                startDismissControlViewTimer();
            }
        };
        mFullScreenButton.setOnClickListener(onClickListener);
        mDownloadButton.setOnClickListener(onClickListener);
        mPlayStatusButton.setOnClickListener(onClickListener);
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null)
                    mOnClickListener.onClick(view);
                if (handleOnClick()) return;
                if (mManager.getPlayStatus() != PlayManager.PlayStatus.ERROR && mManager.getPlayStatus() != PlayManager.PlayStatus.NORMAL)
                    setControlViewsVisibility(mControlView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);

            }
        });

    }

    private synchronized boolean handleOnClick() {
        mVideoManager.syncStatus(this);
        if (!isFullScreenNow) {
            mManager.setCurrentPlayer(VideoPlayer.this);
            mManager.setCurrentListener(mPlayListener);
        }
        if (mManager.getPlayStatus() == PlayManager.PlayStatus.PREPARING)
            return true;
        if (!isLoadedUrl) {
            mManager.onlyRelease();
            HttpUtil.getVideoUrlByUrl(mCurrentVideoUrl, new HttpUtil.HttpRequestCallbackListener() {
                @Override
                public void onSuccess(List<Data> data, final String args) {
                    mControlView.post(new Runnable() {
                        @Override
                        public void run() {
                            setUrlPlay(args);
                            isLoadedUrl = true;
                        }
                    });
                }

                @Override
                public void onFailure(Exception e, String reason) {
                    mControlView.post(new Runnable() {
                        @Override
                        public void run() {
                            mPlayListener.onError(0, 0);
                        }
                    });
                }
            });
            mManager.setPlayerStatus(PlayManager.PlayStatus.PREPARING);
            mRootView.setFocusable(false);
            mRootView.setClickable(false);
            showLoadingBar();
            setTitleViewVisibility(INVISIBLE);
            mPlayStatusButton.setVisibility(INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        isLoadedUrl = false;
    }
}
