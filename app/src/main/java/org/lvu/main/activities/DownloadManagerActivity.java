package org.lvu.main.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.lvu.R;
import org.lvu.customize.DownloadManager;

/**
 * Created by wuyr on 1/5/17 2:03 PM.
 */

public class DownloadManagerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager_view);
        DownloadManager.getInstance().downloadVideo("http://hd.52avhd.com:9889/om/04A2E153/SD/playlist.m3u8", "test");
    }
}
