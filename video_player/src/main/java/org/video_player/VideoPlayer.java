package org.video_player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static org.video_player.PlayManager.PlayStatus.ERROR;
import static org.video_player.PlayManager.PlayStatus.NORMAL;
import static org.video_player.PlayManager.PlayStatus.PAUSE;
import static org.video_player.PlayManager.PlayStatus.PLAYING;

/**
 * Created by wuyr on 12/3/16 5:53 PM.
 */

@SuppressLint("SetTextI18n")
public class VideoPlayer extends RelativeLayout {

    private View mRootView, mTitleView, mControlView, mDownloadButton, mLoadingBar;
    private RelativeLayout mSurfaceViewContainer;
    private ImageView mBackgroundImg, mPlayStatusButton, mFullScreenButton;
    private TextView mTitle, mSeekView, mCurrentTime, mTotalTime;
    private SeekBar mProgressBar;
    private ProgressBar mBottomProgressBar;
    private SurfaceView mSurfaceView;
    private PlayManager mManager;
    private PlayManager.PlayListener mPlayListener;
    private Timer mDismissControlViewTimer, mUpdateProgressTimer;
    private boolean isOriginallyPlaying, isShowTitleView = true, isTouching;
    private float mStartX;
    private int mMoveDistance, mResult, mTotalWidth = -1;
    private static boolean isFullScreenNow;
    private String mCurrentVideoUrl;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.video_player_view, this, true);
        mManager = PlayManager.getInstance();
        findViews();
        setListeners();
        LogUtil.print("init player complete");
    }

    private void findViews() {
        mSurfaceViewContainer = (RelativeLayout) mRootView.findViewById(R.id.surface_view_container);
        mTitleView = mRootView.findViewById(R.id.title_view);
        mControlView = mRootView.findViewById(R.id.control_view);
        mDownloadButton = mRootView.findViewById(R.id.download_button);
        mLoadingBar = mRootView.findViewById(R.id.loading_bar);
        mFullScreenButton = (ImageView) mRootView.findViewById(R.id.fullscreen_button);
        mPlayStatusButton = (ImageView) mRootView.findViewById(R.id.play_status);
        mBackgroundImg = (ImageView) mRootView.findViewById(R.id.background_img);
        mTitle = (TextView) mRootView.findViewById(R.id.title);
        mSeekView = (TextView) mRootView.findViewById(R.id.seek_view);
        mCurrentTime = (TextView) mRootView.findViewById(R.id.current_time);
        mTotalTime = (TextView) mRootView.findViewById(R.id.total_time);
        mProgressBar = (SeekBar) mRootView.findViewById(R.id.progress_bar);
        mBottomProgressBar = (ProgressBar) mRootView.findViewById(R.id.bottom_progress);
    }

    private void setListeners() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                } else
                    mManager.setCurrentListener(mManager.getLastListener());
                int i = view.getId();
                if (i == R.id.fullscreen_button) {
                    LogUtil.printf("fullscreen button isFullScreenNow? %s", isFullScreenNow);
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
                            LogUtil.print("prepareAsync()");
                            prepareAsync();
                            break;
                        case PLAYING:
                            LogUtil.print("playing");
                            pause();
                            mPlayStatusButton.setImageResource(R.drawable.ic_play);
                            break;
                        case PAUSE:
                            LogUtil.print("pause");
                            play();
                            mPlayStatusButton.setImageResource(R.drawable.ic_pause);
                            break;
                        default:
                    }
                } else if (i == R.id.download_button) {
                    LogUtil.print("download button clicked");
                    // TODO: 12/21/16 handle download
                    // TODO: 12/22/16 activity stop player releases
                }
                startDismissControlViewTimer();
            }
        };
        mFullScreenButton.setOnClickListener(onClickListener);
        mDownloadButton.setOnClickListener(onClickListener);
        mPlayStatusButton.setOnClickListener(onClickListener);
        mProgressBar.setEnabled(false);
        mRootView.setFocusable(true);
        mRootView.setClickable(true);
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                } else
                    mManager.setCurrentListener(mManager.getLastListener());
                LogUtil.printf("rootView clicked player status: %s", mManager.getPlayStatus());
                if (mManager.getPlayStatus() == ERROR || mManager.getPlayStatus() == NORMAL)
                    prepareAsync();
                else {
                    setControlViewsVisibility(mControlView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
                    startDismissControlViewTimer();
                }
            }
        });
        mRootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mManager.getPlayStatus() != PLAYING && mManager.getPlayStatus() != PAUSE)
                    return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float endY = event.getX();
                        mMoveDistance = (int) (endY - mStartX);
                        if (mControlView.getVisibility() != VISIBLE) {
                            if (mManager.getDuration() <= 0 || mTotalWidth <= 0) {
                                break;
                            }
                            if (isForward()) {
                                mMoveDistance -= 30;
                                //mManager.getDuration() * mMoveDistance / mTotalWidth * 60000;
                                mSeekView.setVisibility(VISIBLE);
                                mResult = (int) (mManager.getDuration() / 1000 * mMoveDistance / mTotalWidth * 60000 * 2 * 0.01);
                                if (mResult > mProgressBar.getMax())
                                    mResult = mProgressBar.getMax();
                                mSeekView.setText("+" + formatTime(mResult));
                                int result2 = mProgressBar.getProgress() + mResult;
                                if (result2 > mProgressBar.getMax())
                                    result2 = mProgressBar.getMax();
                                else if (mResult < 0 - mProgressBar.getMax())
                                    result2 = 0 - mProgressBar.getMax();
                                mBottomProgressBar.setProgress(result2);
                            } else if (isBackward()) {
                                mMoveDistance += 30;
                                mSeekView.setVisibility(VISIBLE);
                                mResult = (int) (mManager.getDuration() / 1000 * mMoveDistance / mTotalWidth * 60000 * 2 * 0.01);
                                if (mResult < 0 - mProgressBar.getMax())
                                    mResult = (0 - mProgressBar.getMax());
                                mSeekView.setText("-" + formatTime(0 - mResult));
                                int result2 = mProgressBar.getProgress() + mResult;
                                if (result2 > mProgressBar.getMax())
                                    result2 = mProgressBar.getMax();
                                else if (mResult < 0 - mProgressBar.getMax())
                                    result2 = 0 - mProgressBar.getMax();
                                mBottomProgressBar.setProgress(result2);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mSeekView.getVisibility() != INVISIBLE) {
                            int result2 = mProgressBar.getProgress() + mResult;
                            if (result2 > mProgressBar.getMax())
                                result2 = mProgressBar.getMax();
                            else if (mResult < 0 - mProgressBar.getMax())
                                result2 = 0 - mProgressBar.getMax();
                            mManager.seekTo(result2);
                            mProgressBar.setProgress(result2);
                            mBottomProgressBar.setProgress(result2);
                            mCurrentTime.setText(formatTime(result2));
                            mSeekView.setVisibility(INVISIBLE);
                            return true;
                        }
                        break;
                    default:
                }
                return false;
            }
        });
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mManager.pause();
                    this.progress = progress;
                    mCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isOriginallyPlaying = mManager.isPlaying();
                isTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mManager.seekTo(progress);
                if (isOriginallyPlaying) {
                    play();
                    isOriginallyPlaying = false;
                }
                if (progress >= mProgressBar.getMax()) {
                    mPlayStatusButton.setImageResource(R.drawable.ic_play);
                    mCurrentTime.setText("00:00");
                    mManager.pause();
                    mManager.seekTo(0);
                    mProgressBar.setProgress(0);
                }
                isTouching = false;
                startDismissControlViewTimer();
            }
        });
        mPlayListener = new PlayManager.PlayListener() {
            @Override
            public void onPrepared() {
                LogUtil.print("onPrepared");
                setKeepScreenOn(true);
                mTotalWidth = mManager.getVideoWidth();
                mRootView.setFocusable(true);
                mRootView.setClickable(true);
                mProgressBar.setMax((int) mManager.getDuration());
                mBottomProgressBar.setMax((int) mManager.getDuration());
                mTotalTime.setText(formatTime((int) mManager.getDuration()));
                mCurrentTime.setText(formatTime((int) mManager.getCurrentPosition()));
                mProgressBar.setEnabled(true);
                hideLoadingBar();
                setBackgroundVisibility(INVISIBLE);
                play();
                setControlViewsVisibility(VISIBLE);
                startDismissControlViewTimer();
                startProgressTimer();
            }

            @Override
            public void onCompletion() {
                LogUtil.print("onCompletion");
                setKeepScreenOn(false);
                mPlayStatusButton.setImageResource(R.drawable.ic_play);
                setControlViewsVisibility(VISIBLE);
            }

            @Override
            public void onBufferingUpdate(int percent) {
                double tmp = percent * 0.01;
                mProgressBar.setSecondaryProgress((int) (mProgressBar.getMax() * tmp));
                mBottomProgressBar.setSecondaryProgress((int) (mBottomProgressBar.getMax() * tmp));
            }

            @Override
            public void onError(int what, int extra) {
                LogUtil.printf("on error what = %s", what);
                if (what != -38) {
                    resetPlayerData();
                    mRootView.setFocusable(true);
                    mRootView.setClickable(true);
                    mManager.setPlayerStatus(ERROR);
                    mManager.release();
                    mPlayStatusButton.setImageResource(R.drawable.ic_error);
                    mPlayStatusButton.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onDisplayChanged(boolean isExitFullScreen) {
                LogUtil.print("on display changed");
                if (!isExitFullScreen)
                    setBackgroundVisibility(VISIBLE);
                else setBackgroundVisibility(INVISIBLE);
            }

            @Override
            public void onResetStatus() {
                resetPlayerData();
            }
        };
    }

    private boolean isForward() {
        return mMoveDistance > 30;
    }

    private boolean isBackward() {
        return mMoveDistance < -30;
    }

    private void resetPlayerData() {
        setKeepScreenOn(false);
        LogUtil.print("reset player data");
        cancelDismissControlViewTimer();
        cancelProgressTimer();
        mProgressBar.setSecondaryProgress(0);
        mBottomProgressBar.setSecondaryProgress(0);
        mCurrentTime.setText(formatTime(0));
        mTotalTime.setText(formatTime(0));
        mBackgroundImg.setVisibility(VISIBLE);
        mProgressBar.setEnabled(false);
        mPlayStatusButton.setImageResource(R.drawable.ic_play);
        resetPlayerViews();
        hideLoadingBar();
    }

    private void initSurfaceView() {
        initSurfaceView(false);
    }

    private void initSurfaceView(final boolean isExitFullScreen) {
        LogUtil.print("init surface view");
        mSurfaceViewContainer.removeAllViews();
        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mManager.setDisplay(holder, isExitFullScreen);
                LogUtil.print("mManager.setDisplay(holder);");
                measure();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceViewContainer.addView(mSurfaceView, 0, layoutParams);
    }

    void refreshSurfaceView() {
        initSurfaceView();
    }

    private void setControlViewsVisibility(int visible) {
        if (!isTouching) {
            setTitleViewVisibility(visible);
            mPlayStatusButton.setVisibility(visible);
            mControlView.setVisibility(visible);
            mBottomProgressBar.setVisibility(visible == VISIBLE ? INVISIBLE : VISIBLE);
        }
    }

    private void resetPlayerViews() {
        setTitleViewVisibility(VISIBLE);
        mPlayStatusButton.setVisibility(VISIBLE);
        mControlView.setVisibility(INVISIBLE);
        mBottomProgressBar.setVisibility(INVISIBLE);
    }

    private void showLoadingBar() {
        LogUtil.print("show loading bar");
        mLoadingBar.setVisibility(VISIBLE);
    }

    private void hideLoadingBar() {
        LogUtil.print("hide loading bar");
        mLoadingBar.setVisibility(INVISIBLE);
    }

    private void exitFullScreen() {
        LogUtil.print("exit full screen");
        if (getContext() instanceof FullScreenActivity)
            ((FullScreenActivity) getContext()).finish();
    }

    private void enterFullScreen() {
        LogUtil.print("enter full screen");
        mManager.setPlayerData(initPlayerStatus());
        getContext().startActivity(new Intent(getContext(), FullScreenActivity.class));
    }

    Status initPlayerStatus() {
        Status result = new Status();
        result.setLastStatus(mManager.getPlayStatus());
        result.setControlViewVisibility(mControlView.getVisibility());
        int resId;
        switch (mManager.getPlayStatus()) {
            case ERROR:
                resId = R.drawable.ic_error;
                break;
            case NORMAL:
            case PAUSE:
                resId = R.drawable.ic_play;
                break;
            case PLAYING:
            case PREPARING:
                resId = R.drawable.ic_pause;
                break;
            default:
                resId = -1;
        }
        result.setPlayStatusButtonRes(resId);
        result.setTotalWidth(mTotalWidth);
        result.setPlayProgress(mManager.getCurrentPosition());
        result.setProgressLength(mManager.getDuration());
        result.setProgressBarEnable(mProgressBar.isEnabled());
        result.setLoadingBarVisible(mLoadingBar.getVisibility() == VISIBLE);
        result.setTitle(mTitle.getText().toString());
        result.setVideoUrl(mCurrentVideoUrl);
        return result;
    }

    void setFullScreen() {
        isFullScreenNow = true;
        mFullScreenButton.setImageResource(R.drawable.ic_exit_fullscreen);
        if (mManager.getPlayStatus() == PLAYING)
            mPlayStatusButton.setImageResource(R.drawable.ic_pause);
        else if (mManager.getPlayStatus() == PAUSE)
            mPlayStatusButton.setImageResource(R.drawable.ic_play);
    }

    void setPlayerStatus(Status status) {
        if (status == null)
            return;
        try {
            mManager.setPlayerStatus(status.getLastStatus());
            setUrl(status.getVideoUrl());
            mTotalWidth = status.getTotalWidth();
            mTitle.setText(status.getTitle());
            mPlayStatusButton.setImageResource(status.getPlayStatusButtonRes());
            setControlViewsVisibility(status.getControlViewVisibility());
            mProgressBar.setEnabled(status.isProgressBarEnable());
            mProgressBar.setMax((int) status.getProgressLength());
            mProgressBar.setProgress((int) status.getPlayProgress());
            mBottomProgressBar.setMax((int) status.getProgressLength());
            mBottomProgressBar.setProgress((int) status.getPlayProgress());
            mTotalTime.setText(formatTime((int) status.getProgressLength()));
            mCurrentTime.setText(formatTime((int) status.getPlayProgress()));
            if (status.isLoadingBarVisible())
                showLoadingBar();
            else hideLoadingBar();
            startProgressTimer();
            if (status.getControlViewVisibility() == VISIBLE)
                startDismissControlViewTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onFullScreenActivityExited(Status status) {
        LogUtil.print("full screen activity exited");
        isFullScreenNow = false;
        setPlayerStatus(status);
        initSurfaceView(true);
    }

    private void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        mDismissControlViewTimer = new Timer();
        mDismissControlViewTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mManager.getPlayStatus() != NORMAL || mManager.getPlayStatus() != ERROR) {
                                LogUtil.print("DismissControlView");
                                setControlViewsVisibility(INVISIBLE);
                            }
                        }
                    });
                }
            }
        }, 2500);
    }

    private void cancelDismissControlViewTimer() {
        if (mDismissControlViewTimer != null) {
            mDismissControlViewTimer.cancel();
        }
    }

    private void setTitleViewVisibility(int visible) {
        mTitleView.setVisibility(isShowTitleView ? visible : isFullScreenNow ? visible : INVISIBLE);
    }

    private void setBackgroundVisibility(int visible) {
        mBackgroundImg.setVisibility(visible);
    }

    private void startProgressTimer() {
        LogUtil.print("start progress timer");
        cancelProgressTimer();
        mUpdateProgressTimer = new Timer();
        mUpdateProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mManager.getPlayStatus() == PLAYING) {
                                int currentPos = (int) mManager.getCurrentPosition();
                                mProgressBar.setProgress(currentPos);
                                mBottomProgressBar.setProgress(currentPos);
                                mCurrentTime.setText(formatTime(currentPos));
                                if (mCurrentTime.getText().toString().equals(mTotalTime.getText().toString()))
                                    mPlayListener.onCompletion();
                            }
                        }
                    });
                }
            }
        }, 0, 100);
    }

    private void prepareAsync() {
        showLoadingBar();
        setControlViewsVisibility(INVISIBLE);
        mRootView.setFocusable(false);
        mRootView.setClickable(false);
        initSurfaceView();
        mManager.prepareAsync(getContext(), mCurrentVideoUrl);
    }

    private void cancelProgressTimer() {
        LogUtil.print("cancel progress timer");
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
        }
    }

    public void setUrl(String url) {
        mCurrentVideoUrl = url;
    }

    public void setUrlPlay(String url) {
        setUrl(url);
        mManager.prepareAsync(getContext(), url);
    }

    public void play() {
        mManager.play();
        mPlayStatusButton.setImageResource(R.drawable.ic_pause);
        startProgressTimer();
    }

    public void pause() {
        mManager.pause();
        mPlayStatusButton.setImageResource(R.drawable.ic_play);
    }

    public void stop() {
        mManager.stop();
        mPlayStatusButton.setImageResource(R.drawable.ic_play);
    }

    public void release() {
        mManager.release();
        mPlayStatusButton.setImageResource(R.drawable.ic_play);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setPreviewResource(@DrawableRes int resId) {
        mBackgroundImg.setImageResource(resId);
    }

    public void setPreviewBitmap(Bitmap bitmap) {
        mBackgroundImg.setImageBitmap(bitmap);
    }

    public void hideTitleView() {
        isShowTitleView = false;
        mTitleView.setVisibility(INVISIBLE);
    }

    private String formatTime(int time) {
        if (time <= 0 || time >= 24 * 60 * 60 * 1000)
            return "00:00";
        int totalSeconds = time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
        /*//转成分
        int minutes = time / 1000 / 60;
        //转成秒
        int seconds = time / 1000 % 60;
        //得到的分秒若小于10，即个位数则前面2加0，否则直接转换成String
        String mm = minutes < 10 ? "0" + minutes : String.valueOf(minutes),
                ss = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        //以 00:00 的形式返回
        return mm + ":" + ss;*/
    }

    private void measure() {
        if (mSurfaceView == null) return;
        int displayWith = mSurfaceView.getWidth(), displayHeight = mSurfaceView.getHeight();
        int vW = mManager.getVideoWidth(), vH = mManager.getVideoHeight();

        if (vW != 0 && vH != 0) {
            //设置视频的宽度和高度
            ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
            lp.width = displayWith;
            lp.height = displayHeight;
            //视频显示高度要重新调整
            if (vW * displayHeight > displayWith * vH)
                lp.height = displayWith * vH / vW;
                //视频宽度要重新调整
            else if (vW * displayHeight < displayWith * vH)
                lp.width = displayHeight * vW / vH;
            else {
                lp.width = displayWith;
                lp.height = displayHeight;
            }
            mSurfaceView.setLayoutParams(lp);
            mSurfaceView.getHolder().setFixedSize(lp.width, lp.height);
        }
    }

    static class Status {

        private int controlViewVisibility, playStatusButtonRes, totalWidth;
        private long playProgress, progressLength;
        private boolean progressBarEnable, loadingBarVisible;
        private String title, videoUrl;
        private PlayManager.PlayStatus lastStatus;

        int getControlViewVisibility() {
            return controlViewVisibility;
        }

        void setControlViewVisibility(int controlViewVisibility) {
            this.controlViewVisibility = controlViewVisibility;
        }

        int getPlayStatusButtonRes() {
            return playStatusButtonRes;
        }

        void setPlayStatusButtonRes(int playStatusButtonRes) {
            this.playStatusButtonRes = playStatusButtonRes;
        }

        int getTotalWidth() {
            return totalWidth;
        }

        void setTotalWidth(int totalWidth) {
            this.totalWidth = totalWidth;
        }

        long getPlayProgress() {
            return playProgress;
        }

        void setPlayProgress(long playProgress) {
            this.playProgress = playProgress;
        }

        long getProgressLength() {
            return progressLength;
        }

        void setProgressLength(long progressLength) {
            this.progressLength = progressLength;
        }

        boolean isProgressBarEnable() {
            return progressBarEnable;
        }

        void setProgressBarEnable(boolean progressBarEnable) {
            this.progressBarEnable = progressBarEnable;
        }

        boolean isLoadingBarVisible() {
            return loadingBarVisible;
        }

        void setLoadingBarVisible(boolean loadingBarVisible) {
            this.loadingBarVisible = loadingBarVisible;
        }

        String getTitle() {
            return title;
        }

        void setTitle(String title) {
            this.title = title;
        }

        String getVideoUrl() {
            return videoUrl;
        }

        void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        PlayManager.PlayStatus getLastStatus() {
            return lastStatus;
        }

        void setLastStatus(PlayManager.PlayStatus lastStatus) {
            this.lastStatus = lastStatus;
        }
    }
}