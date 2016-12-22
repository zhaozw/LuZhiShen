package org.lvu.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.lvu.R;
import org.video_player.PlayManager;

/**
 * Created by wuyr on 12/22/16 11:55 PM.
 */

public class VideoPlayer extends org.video_player.VideoPlayer {

    private boolean isLoadedUrl;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoadedUrl){
                    showLoadingBar();
                }
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                }
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
                if (!isFullScreenNow) {
                    mManager.setCurrentPlayer(VideoPlayer.this);
                    mManager.setCurrentListener(mPlayListener);
                }
                if (mManager.getPlayStatus() == PlayManager.PlayStatus.ERROR || mManager.getPlayStatus() == PlayManager.PlayStatus.NORMAL)
                    prepareAsync();
                else
                    setControlViewsVisibility(mControlView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);

            }
        });

    }
}
