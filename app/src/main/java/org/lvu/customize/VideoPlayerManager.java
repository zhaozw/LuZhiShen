package org.lvu.customize;

import org.video_player.PlayManager;
import org.video_player.VideoPlayer;

import java.util.HashSet;

/**
 * Created by wuyr on 12/23/16 4:20 PM.
 */

public class VideoPlayerManager {

    private HashSet<org.video_player.VideoPlayer> mPlayers;
    private volatile static VideoPlayerManager mInstance;
    private VideoPlayer mLastSyncPlayer;

    private VideoPlayerManager() {
        mPlayers = new HashSet<>();
    }

    public static VideoPlayerManager getInstance() {
        if (mInstance == null)
            synchronized (PlayManager.class) {
                if (mInstance == null)
                    mInstance = new VideoPlayerManager();
            }
        return mInstance;
    }

    void add(org.video_player.VideoPlayer player) {
        mPlayers.add(player);
    }

    void syncStatus(VideoPlayer player) {
        if (mLastSyncPlayer != player) {
            if (!mPlayers.contains(player))
                mPlayers.add(player);
            for (VideoPlayer tmp : mPlayers) {
                if (tmp != null && tmp != player)
                    tmp.reset();
            }
        }
        mLastSyncPlayer = player;
    }

    public void resetAll() {
        for (VideoPlayer tmp : mPlayers)
            if (tmp != null)
                tmp.reset();
        PlayManager.getInstance().setPlayerStatus(PlayManager.PlayStatus.NORMAL);
        PlayManager.getInstance().onlyRelease();
    }
}
