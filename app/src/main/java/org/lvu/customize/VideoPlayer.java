package org.lvu.customize;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.lvu.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by wuyr on 6/6/16 8:50 PM.
 */
public class VideoPlayer extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private Activity mActivity;
    private MediaPlayer mMediaPlayer;
    private VideoView mPlayer;
    private ImageView mPlayStatus;
    private View mRootView, mBottomView, mExitBtn, mLoadingBar;
    private TextView mSeekView, mCurrentTime, mTotalTime, mDownloadSpeed;
    private SeekBar mProgressbar;
    private boolean isControlButtonsShowing, isReplay,
            isTimingThreadRunning, isOriginallyPlaying, mRunningFlag;
    private int mOriginallyVCS, mOriginallyOrientation;
    private Handler mHandler;
    private float mStartX, mEndX;
    private int mMoveDistance, mTotalWidth = -1, mResult;

    public VideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        findViews();
        //initViewsColors();
        setListeners();
        initProgressBar();
    }

    private void initProgressBar() {
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorAccent),
                mContext.getResources().getColor(R.color.blueAccent));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList stateList = ColorStateList.valueOf(color);
                mProgressbar.setProgressTintList(stateList);
                mProgressbar.setIndeterminateTintList(stateList);
                mProgressbar.setSecondaryProgressTintList(stateList);
            } else {
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mode = PorterDuff.Mode.MULTIPLY;
                }
                if (mProgressbar.getIndeterminateDrawable() != null)
                    mProgressbar.getIndeterminateDrawable().setColorFilter(color, mode);
                if (mProgressbar.getProgressDrawable() != null)
                    mProgressbar.getProgressDrawable().setColorFilter(color, mode);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        a.recycle();
        mProgressbar.setEnabled(false);
    }

    private void findViews() {
        mRootView = LayoutInflater.from(mContext).
                inflate(R.layout.customize_video_player_view, this, true);
        mPlayer = (VideoView) mRootView.findViewById(R.id.video_view);
        mLoadingBar = mRootView.findViewById(R.id.buffering);
        mDownloadSpeed = (TextView) mRootView.findViewById(R.id.download_speed);
        mPlayStatus = (ImageView) mRootView.findViewById(R.id.play_status);
        mBottomView = mRootView.findViewById(R.id.bottom_view);
        mExitBtn = mRootView.findViewById(R.id.btn_exit);
        mSeekView = (TextView) mRootView.findViewById(R.id.seek_view);
        mCurrentTime = (TextView) mRootView.findViewById(R.id.current_time);
        mTotalTime = (TextView) mRootView.findViewById(R.id.total_time);
        mProgressbar = (SeekBar) mRootView.findViewById(R.id.progress_bar);
    }

    private void setListeners() {
        mPlayer.setOnClickListener(this);
        mPlayStatus.setOnClickListener(this);
        mExitBtn.setOnClickListener(this);
        mProgressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pause();
                    this.progress = progress;
                    mCurrentTime.setText(parseToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isOriginallyPlaying = mPlayer.isPlaying();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(progress);
                if (isOriginallyPlaying) {
                    play();
                    isOriginallyPlaying = false;
                }
                if (!isTimingThreadRunning)
                    startTimingThread();
                if (progress >= mProgressbar.getMax()) {
                    mPlayStatus.setImageResource(R.drawable.ic_continue);
                    pause();
                    mCurrentTime.setText("00:00");
                    isReplay = true;
                    mPlayer.seekTo(1);
                    mProgressbar.setProgress(1);
                }
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
                mProgressbar.setEnabled(true);
                mTotalWidth = mPlayer.getWidth();
                if (mMediaPlayer == null) {
                    mMediaPlayer = mp;
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayStatus.setImageResource(R.drawable.ic_continue);
                            pause();
                            mCurrentTime.setText("00:00");
                            isReplay = true;
                            mPlayer.seekTo(1);
                            mProgressbar.setProgress(1);
                            showControlButtons(true);
                        }
                    });
                }
                mLoadingBar.setVisibility(INVISIBLE);
                mDownloadSpeed.setVisibility(INVISIBLE);
                mProgressbar.setMax((int) mPlayer.getDuration());
                mTotalTime.setText(parseToString((int) mPlayer.getDuration()));
                mCurrentTime.setText(parseToString((int) mPlayer.getCurrentPosition()));
                mPlayer.start();
                startTimingThread();
            }
        });
        /*mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                double tmp = percent * 0.01;
                mProgressbar.setSecondaryProgress((int) (mProgressbar.getMax() * tmp));
            }
        });*/
        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            // 等待缓冲监听
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mLoadingBar.setVisibility(VISIBLE);
                        mDownloadSpeed.setVisibility(VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        // 缓存完成，继续播放
                        mLoadingBar.setVisibility(INVISIBLE);
                        mDownloadSpeed.setVisibility(INVISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        if (mDownloadSpeed.getVisibility() == VISIBLE)
                            mDownloadSpeed.setText(extra + "K/S");
                }
                return true;
            }
        });
        mPlayer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mEndX = event.getX();
                        mMoveDistance = (int) (mEndX - mStartX);
                        if (!isControlButtonsShowing) {
                            if (isForward()) {
                                if (mPlayer.getDuration() == -1 || mTotalWidth == -1)
                                    break;
                                mMoveDistance -= 30;
                                //mPlayer.getDuration() * mMoveDistance / mTotalWidth * 60000;
                                mSeekView.setVisibility(VISIBLE);
                                mResult = (int) (mPlayer.getDuration() / 1000 * mMoveDistance / mTotalWidth * 60000 * 2 * 0.01);
                                if (mResult > mProgressbar.getMax())
                                    mResult = mProgressbar.getMax();
                                mSeekView.setText("+" + parseToString(mResult));
                            } else if (isBackward()) {
                                if (mPlayer.getDuration() == -1 || mTotalWidth == -1)
                                    break;
                                mMoveDistance += 30;
                                mSeekView.setVisibility(VISIBLE);
                                mResult = (int) (mPlayer.getDuration() / 1000 * mMoveDistance / mTotalWidth * 60000 * 2 * 0.01);
                                if (mResult < 0 - mProgressbar.getMax())
                                    mResult = (0 - mProgressbar.getMax());
                                mSeekView.setText("-" + parseToString(0 - mResult));
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mSeekView.getVisibility() != GONE) {
                            int result2 = mProgressbar.getProgress() + mResult;
                            if (result2 > mProgressbar.getMax())
                                result2 = mProgressbar.getMax();
                            else if (mResult < 0 - mProgressbar.getMax())
                                result2 = 0 - mProgressbar.getMax();
                            mPlayer.seekTo(result2);
                            mProgressbar.setProgress(result2);
                            mCurrentTime.setText(parseToString(result2));
                            mSeekView.setVisibility(GONE);
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private boolean isForward() {
        return mMoveDistance > 30;
    }

    private boolean isBackward() {
        return mMoveDistance < -30;
    }

    private void initViewsColors() {
        List<Integer> data = new ArrayList<>();
        int[] array = R.styleable.AppCompatTheme;
        for (int tmp : array)
            data.add(tmp);
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
        int color = a.getColor(data.indexOf(R.attr.colorAccent),
                mContext.getResources().getColor(R.color.blueAccent));

        mPlayStatus.setColorFilter(color);
        ((ImageView) mExitBtn).setColorFilter(color);
        mCurrentTime.setTextColor(color);
        mTotalTime.setTextColor(color);
        ((TextView) mLoadingBar).setTextColor(color);
        a.recycle();
    }

    /*private void measure() {
        int displayWith = mSurfaceView.getWidth(), displayHeight = mSurfaceView.getHeight();
        int vW = mPlayer.getVideoWidth(), vH = mPlayer.getVideoHeight();

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
    }*/

    private void startTimingThread() {
        new Thread() {
            @Override
            public void run() {
                isTimingThreadRunning = true;
                mRunningFlag = true;
                while (mRunningFlag) {
                    if (mPlayer.isPlaying()) {
                        //每隔0.5秒同步一次歌曲播放进度
                        if (mPlayer.getCurrentPosition() < mProgressbar.getMax()) {
                            mProgressbar.setProgress((int) mPlayer.getCurrentPosition());
                            Message message = new Message();
                            message.obj = parseToString((int) mPlayer.getCurrentPosition());
                            //UI操作不能在线程中完成，所以需要作异步处理
                            mHandler.sendMessage(message);
                        } else
                            mRunningFlag = false;
                        try {
                            //线程暂停0.5秒
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                isTimingThreadRunning = false;
            }
        }.start();
    }

    private void showControlButtons(boolean isShow) {
        mBottomView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mPlayStatus.setVisibility(mBottomView.getVisibility());
        isControlButtonsShowing = isShow;
    }

    public void setUrlPlay(String url) {
        changeToLandscape();
        mLoadingBar.setVisibility(VISIBLE);
        mDownloadSpeed.setVisibility(VISIBLE);
        mPlayer.setVideoPath(url);
        if (mRootView.getVisibility() == GONE)
            mRootView.setVisibility(VISIBLE);
        //mPlayer.setDataSource(mContext, Uri.parse("android:resource://org.lvu/"+R.raw.media));
    }

    public void play() {
        if (!mPlayer.isPlaying())
            mPlayer.start();
        if (!isTimingThreadRunning)
            startTimingThread();
    }

    public void pause() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
    }

    public void replay() {
        if (!mPlayer.isPlaying())
            mPlayer.start();
        startTimingThread();
    }

    public void exit() {
        pause();
        mRootView.setVisibility(GONE);
        mPlayer.destroyDrawingCache();
        if (mOriginallyOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            changeToPortrait();
        if (mOnPlayCompleteListener != null)
            mOnPlayCompleteListener.playComplete();
        mActivity.setVolumeControlStream(mOriginallyVCS);
        try {
            if (mPlayer != null)
                mRunningFlag = false;
            if (mMediaPlayer != null)
                mMediaPlayer.stop();
            mCurrentTime.setText("00:00");
            mPlayStatus.setImageResource(R.drawable.ic_pause);
            mPlayer.seekTo(1);
            mProgressbar.setProgress(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    private void changeToPortrait() {
        if (mActivity.getResources().getConfiguration().orientation
                != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mActivity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void changeToLandscape() {
        if (mActivity == null)
            Log.e("changeToLandscape", "please setActivity first!");
        if (mActivity.getResources().getConfiguration().orientation
                != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
            uiFlags |= 0x00001000;
            mActivity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    private OnPlayCompleteListener mOnPlayCompleteListener;

    public void setOnPlayCompleteListener(OnPlayCompleteListener listener) {
        mOnPlayCompleteListener = listener;
    }

    public interface OnPlayCompleteListener {
        void playComplete();
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        mOriginallyVCS = mActivity.getVolumeControlStream();
        mOriginallyOrientation = mActivity.getResources().getConfiguration().orientation;
        mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    //将MediaPlayer返回毫秒解释成 分:秒
    private String parseToString(int time) {
        //转成分
        int minutes = time / 1000 / 60;
        //转成秒
        int seconds = time / 1000 % 60;
        //得到的分秒若小于10，即个位数则前面2加0，否则直接转换成String （三目运算）
        String mm = minutes < 10 ? "0" + minutes : String.valueOf(minutes),
                ss = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        //以 04:23 的形式返回
        return mm + ":" + ss;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
                showControlButtons(!isControlButtonsShowing);
                break;
            case R.id.btn_exit:
                exit();
                break;
            case R.id.play_status:
                if (mPlayer.isPlaying()) {
                    pause();
                    mPlayStatus.setImageResource(R.drawable.ic_continue);
                } else {
                    if (isReplay) {
                        replay();
                        isReplay = false;
                    } else
                        play();
                    mPlayStatus.setImageResource(R.drawable.ic_pause);
                }
                break;
            default:
                break;
        }
    }

    static class MyHandler extends Handler {

        private WeakReference<VideoPlayer> mClass;

        public MyHandler(VideoPlayer clazz) {
            mClass = new WeakReference<>(clazz);
        }

        @Override
        public void handleMessage(Message msg) {
            mClass.get().mCurrentTime.setText((String) msg.obj);
            if (msg.obj.equals(mClass.get().mTotalTime.getText().toString())) {
                mClass.get().showControlButtons(true);
                mClass.get().mProgressbar.setProgress(1);
                mClass.get().mPlayStatus.setImageResource(R.drawable.ic_continue);
                mClass.get().mCurrentTime.setText("00:00");
                mClass.get().isReplay = true;
            }
        }
    }
}
