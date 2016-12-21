package org.video_player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by wuyr on 12/3/16 5:53 PM.
 */

class PlayManager {

    private volatile static PlayManager mInstance;
    private IjkMediaPlayer mPlayer;
    private PlayListener mListener;
    private PlayStatus mStatus = PlayStatus.NORMAL;

    private PlayManager() {
        mPlayer = new IjkMediaPlayer();
    }

    static PlayManager getInstance() {
        LogUtil.print("get play manager instance");
        if (mInstance == null)
            synchronized (PlayManager.class) {
                if (mInstance == null)
                    mInstance = new PlayManager();
            }
        return mInstance;
    }

    void prepareAsync(Context context, String url) {
        LogUtil.print("prepareAsync");
        if (TextUtils.isEmpty(url)) {
            if (mListener != null)
                mListener.onError(0, 0);
            return;
        }
        try {
            if (mPlayer != null)
                mPlayer.release();
            mPlayer = new IjkMediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(context, Uri.parse(url));
            mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    if (mListener != null)
                        mListener.onPrepared();
                }
            });
            mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (mListener != null)
                        mListener.onCompletion();
                }
            });
            mPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                    if (mListener != null)
                        mListener.onBufferingUpdate(i);
                }
            });
            mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    if (mListener != null)
                        mListener.onError(i, i1);
                    return true;
                }
            });
            mPlayer.prepareAsync();
            setPlayerStatus(PlayStatus.PREPARING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int getVideoWidth() {
        return mPlayer.getVideoWidth();
    }

    int getVideoHeight() {
        return mPlayer.getVideoHeight();
    }

    void play() {
        if (mPlayer == null) return;
        if (!mPlayer.isPlaying())
            mPlayer.start();
        mStatus = PlayStatus.PLAYING;
    }

    void pause() {
        if (mPlayer == null) return;
        if (mPlayer.isPlaying())
            mPlayer.pause();
        mStatus = PlayStatus.PAUSE;
    }

    void stop() {
        if (mPlayer == null) return;
        if (mPlayer.isPlaying())
            mPlayer.stop();
        mStatus = PlayStatus.NORMAL;
        if (mListener != null)
            mListener.onCompletion();
    }

    void release() {
        if (mPlayer == null) return;
        stop();
        mPlayer.release();
    }

    boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    void setDisplay(SurfaceHolder sh, boolean isExitFullScreen) {
        if (mPlayer == null) return;
        mPlayer.setDisplay(sh);
        if (mListener != null)
            mListener.onDisplayChanged(isExitFullScreen);
    }

    long getDuration() {
        if (mPlayer == null) return -1;
        return mPlayer.getDuration();
    }

    long getCurrentPosition() {
        if (mPlayer == null) return -1;
        return mPlayer.getCurrentPosition();
    }

    void seekTo(long position) {
        if (mPlayer == null) return;
        mPlayer.seekTo(position);
    }

    void setCurrentListener(PlayListener listener) {
        if (listener == null) return;
        if (mListener != listener) {
            setPlayerStatus(PlayStatus.NORMAL);
            if (mListener != null)
                mListener.onResetStatus();
            mListener = listener;
        }
    }

    PlayListener getLastListener() {
        return mListener;
    }

    private VideoPlayer.Status mPlayerData;

    void setPlayerData(VideoPlayer.Status data) {
        mPlayerData = data;
    }

    VideoPlayer.Status getPlayerData() {
        return mPlayerData;
    }

    private VideoPlayer mVideoPlayer;

    void setCurrentPlayer(VideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer)
            mVideoPlayer = videoPlayer;
    }

    VideoPlayer getLastPlayer() {
        return mVideoPlayer;
    }

    interface PlayListener {
        void onPrepared();

        void onCompletion();

        void onBufferingUpdate(int percent);

        void onError(int what, int extra);

        void onDisplayChanged(boolean isExitFullScreen);

        void onResetStatus();
    }

    void setPlayerStatus(PlayStatus status) {
        mStatus = status;
    }

    PlayStatus getPlayStatus() {
        return mStatus;
    }

    enum PlayStatus {
        PREPARING, PAUSE, PLAYING, NORMAL, ERROR
    }
}
