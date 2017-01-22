package org.lvu.models;

/**
 * Created by wuyr on 1/17/17 11:04 PM.
 */

public class DownloadInfo {
    private int length;
    private int current;
    private boolean isDownloading;
    private String name;
    private String url;

    public interface OnDownloadListener {
        void onProgressChanged(int progress, long speed);

        void onFailure(Exception e);

        void onSuccess(String name);
    }
}
