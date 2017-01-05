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

    protected View mRootView, mTitleView, mControlView, mDownloadButton, mLoadingBar;
    protected RelativeLayout mSurfaceViewContainer;
    protected ImageView mBackgroundImg, mPlayStatusButton, mFullScreenButton;
    protected TextView mTitle, mSeekView, mCurrentTime, mTotalTime;
    protected SeekBar mProgressBar;
    protected ProgressBar mBottomProgressBar;
    protected SurfaceView mSurfaceView;
    protected PlayManager mManager;
    protected PlayManager.PlayListener mPlayListener;
    protected static Timer mDismissControlViewTimer, mUpdateProgressTimer;
    protected boolean isOriginallyPlaying, isShowTitleView = true, isTouching;
    protected float mStartX;
    protected int mMoveDistance, mResult, mTotalWidth = -1;
    protected static boolean isFullScreenNow;
    protected String mCurrentVideoUrl;
    protected OnClickListener mOnClickListener;

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

    protected void init() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.video_player_view, this, true);
        mManager = PlayManager.getInstance();
        findViews();
        setListeners();
        LogUtil.print("init player complete");
    }

    protected void findViews() {
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

    protected void setListeners() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                }
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
                }
                startDismissControlViewTimer();
            }
        };
        mFullScreenButton.setOnClickListener(onClickListener);
        mDownloadButton.setOnClickListener(onClickListener);
        mPlayStatusButton.setOnClickListener(onClickListener);
        mRootView.setFocusable(true);
        mRootView.setClickable(true);
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                }
                LogUtil.printf("rootView clicked player status: %s", mManager.getPlayStatus());
                if (mManager.getPlayStatus() == ERROR && mManager.getPlayStatus() == NORMAL)
                    prepareAsync();
                else
                    setControlViewsVisibility(mControlView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);

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
                return mSeekView.getVisibility() == VISIBLE;
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
                if (isPortraitVideo() || mSurfaceView == null)
                    initSurfaceView(true);
                setKeepScreenOn(true);
                mTotalWidth = mManager.getVideoWidth();
                mRootView.setFocusable(true);
                mRootView.setClickable(true);
                mProgressBar.setMax((int) mManager.getDuration());
                mBottomProgressBar.setMax((int) mManager.getDuration());
                mTotalTime.setText(formatTime((int) mManager.getDuration()));
                mCurrentTime.setText(formatTime((int) mManager.getCurrentPosition()));
                hideLoadingBar();
                setBackgroundVisibility(INVISIBLE);
                play();
                setControlViewsVisibility(VISIBLE);
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
                    mRootView.setFocusable(true);
                    mRootView.setClickable(true);
                    mManager.setPlayerStatus(ERROR);
                    mManager.release();
                    resetPlayerData();
                    mPlayStatusButton.setImageResource(R.drawable.ic_error);
                }
            }

            @Override
            public void onDisplayChanged(boolean isExitFullScreen) {
                LogUtil.print("on display changed");
                setBackgroundVisibility(isExitFullScreen ? INVISIBLE : VISIBLE);
            }

            @Override
            public void onResetStatus() {
                resetPlayerData();
            }

            @Override
            public void onExit() {
                if (getContext() instanceof FullScreenActivityLandscape)
                    ((FullScreenActivityLandscape) getContext()).finishSpecial();
            }
        };
    }

    protected boolean isForward() {
        return mMoveDistance > 30;
    }

    protected boolean isBackward() {
        return mMoveDistance < -30;
    }

    protected void resetPlayerData() {
        setKeepScreenOn(false);
        LogUtil.print("reset player data");
        cancelDismissControlViewTimer();
        cancelProgressTimer();
        mProgressBar.setProgress(0);
        mProgressBar.setSecondaryProgress(0);
        mBottomProgressBar.setSecondaryProgress(0);
        mBottomProgressBar.setProgress(0);
        mCurrentTime.setText(formatTime(0));
        mTotalTime.setText(formatTime(0));
        mBackgroundImg.setVisibility(VISIBLE);
        mPlayStatusButton.setImageResource(R.drawable.ic_play);
        resetPlayerViews();
        hideLoadingBar();
    }

    protected void initSurfaceView() {
        initSurfaceView(false);
    }

    protected void initSurfaceView(final boolean isExitFullScreen) {
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
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        mSurfaceViewContainer.addView(mSurfaceView, 0, layoutParams);
    }

    void refreshSurfaceView() {
        initSurfaceView();
    }

    protected void setControlViewsVisibility(int visible) {
        if (!isTouching) {
            setTitleViewVisibility(visible);
            mPlayStatusButton.setVisibility(visible);
            mControlView.setVisibility(visible);
            if (visible == VISIBLE) {
                mBottomProgressBar.setVisibility(INVISIBLE);
                startDismissControlViewTimer();
            } else mBottomProgressBar.setVisibility(VISIBLE);
        }
    }

    protected void resetPlayerViews() {
        LogUtil.print("resetPlayerViews");
        setTitleViewVisibility(VISIBLE);
        mPlayStatusButton.setVisibility(VISIBLE);
        mControlView.setVisibility(INVISIBLE);
        mBottomProgressBar.setVisibility(INVISIBLE);
    }

    protected void showLoadingBar() {
        LogUtil.print("show loading bar");
        mLoadingBar.setVisibility(VISIBLE);
    }

    public void hideLoadingBar() {
        LogUtil.print("hide loading bar");
        mLoadingBar.setVisibility(INVISIBLE);
    }

    public void reset() {
        mSurfaceView = null;
        resetPlayerData();
    }

    protected void exitFullScreen() {
        LogUtil.print("exit full screen");
        if (getContext() instanceof FullScreenActivityLandscape)
            ((FullScreenActivityLandscape) getContext()).finish();
    }

    protected void enterFullScreen() {
        LogUtil.print("enter full screen");
        mManager.setPlayerData(initPlayerStatus());
        getContext().startActivity(new Intent(getContext(), isPortraitVideo() ? FullScreenActivityPortrait.class : FullScreenActivityLandscape.class));
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
        result.setSecondaryProgress(mProgressBar.getSecondaryProgress());
        result.setPlayProgress(mManager.getCurrentPosition());
        result.setProgressLength(mManager.getDuration());
        result.setLoadingBarVisible(mLoadingBar.getVisibility() == VISIBLE);
        result.setTitle(mTitle.getText().toString());
        result.setVideoUrl(mCurrentVideoUrl);
        return result;
    }

    void setFullScreen() {
        isFullScreenNow = true;
        mManager.setFullScreenListener(mPlayListener);
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
            mProgressBar.setMax((int) status.getProgressLength());
            mProgressBar.setProgress((int) status.getPlayProgress());
            mProgressBar.setSecondaryProgress(status.getSecondaryProgress());
            mBottomProgressBar.setMax((int) status.getProgressLength());
            mBottomProgressBar.setProgress((int) status.getPlayProgress());
            mBottomProgressBar.setSecondaryProgress(status.getSecondaryProgress());
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

    protected void startDismissControlViewTimer() {
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

    protected void cancelDismissControlViewTimer() {
        if (mDismissControlViewTimer != null) {
            mDismissControlViewTimer.cancel();
        }
    }

    protected void setTitleViewVisibility(int visible) {
        mTitleView.setVisibility(isShowTitleView ? visible : isFullScreenNow ? visible : INVISIBLE);
    }

    protected void setBackgroundVisibility(int visible) {
        mBackgroundImg.setVisibility(visible);
    }

    protected void startProgressTimer() {
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
                                if (!mCurrentTime.getText().toString().isEmpty() && mCurrentTime.getText().toString().equals(mTotalTime.getText().toString()))
                                    mPlayListener.onCompletion();
                            }
                        }
                    });
                }
            }
        }, 0, 100);
    }

    protected void prepareAsync() {
        mRootView.setFocusable(false);
        mRootView.setClickable(false);
        showLoadingBar();
        setTitleViewVisibility(INVISIBLE);
        mPlayStatusButton.setVisibility(INVISIBLE);
        initSurfaceView();
        mManager.prepareAsync(getContext(), mCurrentVideoUrl);
    }

    protected void cancelProgressTimer() {
        LogUtil.print("cancel progress timer");
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
        }
    }

    static boolean isFullScreenNow() {
        return isFullScreenNow;
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
        if (mSurfaceView == null)
            initSurfaceView(true);
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
        reset();
        mSurfaceViewContainer.removeAllViews();
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

    public void setOnPlayerClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    protected String formatTime(int time) {
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

    private boolean isPortraitVideo() {
        return mManager.getVideoHeight() > mManager.getVideoWidth();
    }

    protected void measure() {
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

    private OnReleaseListener mOnReleaseListener;

    public void setOnReleaseListener(OnReleaseListener listener) {
        mOnReleaseListener = listener;
    }

    public interface OnReleaseListener {
        void onRelease();
    }

    static class Status {

        int controlViewVisibility, playStatusButtonRes, totalWidth, secondaryProgress;
        long playProgress, progressLength;
        boolean loadingBarVisible;
        String title, videoUrl;
        PlayManager.PlayStatus lastStatus;

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

        int getSecondaryProgress() {
            return secondaryProgress;
        }

        void setSecondaryProgress(int secondaryProgress) {
            this.secondaryProgress = secondaryProgress;
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