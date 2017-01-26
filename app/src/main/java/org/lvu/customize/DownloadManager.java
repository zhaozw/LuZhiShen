package org.lvu.customize;

import android.os.Environment;

import org.lvu.Application;
import org.lvu.models.DownloadInfo;
import org.lvu.utils.M3U8Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

/**
 * Created by wuyr on 1/17/17 9:54 PM.
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DownloadManager {

    private volatile static DownloadManager mInstance;
    private static final String DOWNLOAD_PATH, VIDEO_PATH, PICTURE_PATH, NOVEL_PATH;
    private static ExecutorService mThreadPool;
    private static OkHttpClient mHttpClient;
    private static List<DownloadInfo> mDownloadQueue;
    private static boolean isHasTask;
    private static File videoPath, picturePath, novelPath;

    static {
        mThreadPool = Executors.newFixedThreadPool(7);
        mHttpClient = Application.getOkHttpClient();
        DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/撸至深";
        VIDEO_PATH = DOWNLOAD_PATH + "/视频/";
        PICTURE_PATH = DOWNLOAD_PATH + "/图片/";
        NOVEL_PATH = DOWNLOAD_PATH + "/小说/";
        videoPath = new File(VIDEO_PATH);
        videoPath.mkdirs();
        picturePath = new File(PICTURE_PATH);
        picturePath.mkdirs();
        novelPath = new File(NOVEL_PATH);
        novelPath.mkdirs();
    }

    public static DownloadManager getInstance() {
        if (mInstance == null)
            synchronized (DownloadManager.class) {
                if (mInstance == null)
                    mInstance = new DownloadManager();
            }
        return mInstance;
    }

    public static boolean isHasTask() {
        return isHasTask;
    }

    public void downloadVideo(final String url, final String name) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                M3U8Parser.parse(url, new M3U8Parser.OnParserListener() {
                    @Override
                    public void onSuccess(List<String> urls, String m3u8File) {
                        File file = new File(videoPath, name);
                        file.mkdirs();
                        if (writeFile(file.getPath() + "/" + name + ".m3u8", m3u8File))
                            for (String tmp : urls)
                                downloadTS(tmp);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private static void downloadTS(String url) {

    }

    private static boolean writeFile(String filePath, String content) {
        boolean isSuccess;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.flush();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    public boolean downloadPicture(String url) {
        return true;
    }

    public boolean downloadNovel(String url) {
        return true;
    }

    public List<DownloadInfo> getQueue() {
        return mDownloadQueue;
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
