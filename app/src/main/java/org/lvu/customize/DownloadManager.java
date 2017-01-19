package org.lvu.customize;

import android.os.Environment;

import org.lvu.models.DownloadInfo;
import org.video_player.PlayManager;

import java.util.List;

/**
 * Created by wuyr on 1/17/17 9:54 PM.
 */

public class DownloadManager {

    private volatile static DownloadManager mInstance;
    private static final String DOWNLOAD_PATH, VIDEO_PATH, PICTURE_PATH, NOVEL_PATH;
    private static boolean isHasExternalStorage;

    static {
        isHasExternalStorage = Environment.isExternalStorageEmulated();
        DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/撸至深";
        VIDEO_PATH = DOWNLOAD_PATH + "/视频/";
        PICTURE_PATH = DOWNLOAD_PATH + "/图片/";
        NOVEL_PATH = DOWNLOAD_PATH + "/小说/";
    }

    private DownloadManager() {
        mInstance = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        if (mInstance == null)
            synchronized (PlayManager.class) {
                if (mInstance == null)
                    mInstance = new DownloadManager();
            }
        return mInstance;
    }

    public boolean downloadVideo(String url) {
        if (isHasExternalStorage)
            return false;
        return true;
    }

    public boolean downloadPicture(String url) {
        if (isHasExternalStorage)
            return false;
        return true;
    }

    public boolean downloadNovel(String url) {
        if (isHasExternalStorage)
            return false;
        return true;
    }

    public List<DownloadInfo> getQueue() {
        return null;
    }

    private void addTask(String url) {

    }

    private void removeTask(String url) {

    }

    private void pauseTask(String url) {

    }

    private void resumeTask(String url) {

    }
}
