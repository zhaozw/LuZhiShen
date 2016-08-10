package org.lvu.main.activities;

import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.lvu.R;
import org.lvu.customize.MySnackBar;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.util.List;

/**
 * Created by wuyr on 8/10/16 9:04 PM.
 */
public class MoreJokeActivity extends NovelViewActivity {
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void startSyncData() {
        HttpUtil.readMoreJokeAsync(getIntent().getStringExtra(PicturesViewActivity.URL), new HttpUtil.HttpRequestCallbackListener() {
            @Override
            public void onSuccess(List<Data> tmp, String textContent) {
                Message message = new Message();
                message.obj = textContent;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Exception e, String reason) {
                if (reason.equals("无可用网络。\t(向右滑动清除)")) {
                    try {
                        mLoadMoreBar.setVisibility(View.GONE);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else
                    hideLoadMoreBar();
                MySnackBar.show(findViewById(R.id.coordinator), reason, Snackbar.LENGTH_INDEFINITE,
                        getString(R.string.back), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
            }
        });
    }
}
