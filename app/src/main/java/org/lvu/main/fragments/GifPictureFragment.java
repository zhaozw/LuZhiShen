package org.lvu.main.fragments;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.lvu.R;
import org.lvu.adapter.BaseListAdapter;
import org.lvu.adapter.BasePictureListAdapter;
import org.lvu.adapter.GifPictureAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.model.Data;
import org.lvu.utils.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by wuyr on 6/23/16 2:39 PM.
 */
public class GifPictureFragment extends BaseListFragment {

    private HashCodeFileNameGenerator mNameGenerator;
    private ExecutorService mThreadPool;
    private File dir = ImageLoader.getInstance().getDiskCache().getDirectory();

    @Override
    protected BaseListAdapter getAdapter() {
        return new GifPictureAdapter(getActivity(), R.layout.adapter_gif_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String text, final int position) {
                int firstItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (position - firstItemPosition >= 0) {
                    //得到要更新的item的view
                    View view = mRecyclerView.getChildAt(position - firstItemPosition);
                    if (null != mRecyclerView.getChildViewHolder(view)) {

                        final BasePictureListAdapter.ViewHolder holder = (BasePictureListAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (holder.progress.getVisibility() == View.VISIBLE)
                            return;
                        holder.progress.setVisibility(View.VISIBLE);
                        final String gifUrl = mAdapter.getItem(position).getUrl();
                        if (mNameGenerator == null)
                            mNameGenerator = new HashCodeFileNameGenerator();
                        if (mThreadPool == null)
                            mThreadPool = Executors.newSingleThreadExecutor();
                        File gifFile = new File(dir, mNameGenerator.generate(gifUrl));
                        if (gifFile.exists()) {
                            try {
                                holder.image.setImageDrawable(new GifDrawable(gifFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                                holder.progress.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MySnackBar.show(mRootView.findViewById(R.id.coordinator), getString(R.string.load_pic_fail), Snackbar.LENGTH_SHORT);
                                    }
                                });
                            } finally {
                                holder.progress.setVisibility(View.GONE);
                            }
                        } else {
                            new AsyncHttpClient().get(gifUrl, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    try {
                                        mThreadPool.execute(new WriteDataThread(mNameGenerator.generate(gifUrl), responseBody));
                                        holder.image.setImageDrawable(new GifDrawable(responseBody));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        holder.progress.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                MySnackBar.show(mRootView.findViewById(R.id.coordinator), "加载图片失败", Snackbar.LENGTH_SHORT);
                                            }
                                        });
                                    } finally {
                                        holder.progress.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    holder.progress.setVisibility(View.GONE);
                                    holder.progress.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MySnackBar.show(mRootView.findViewById(R.id.coordinator), HttpUtil.REASON_CONNECT_SERVER_FAILURE, Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        };
    }

    @Override
    protected BaseListAdapter.OnItemLongClickListener getOnItemLongClickListener() {
        return new BaseListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(Data item) {
                showDialog(item, getString(R.string.download_this_gif));
                return true;
            }
        };
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void longClickLogic(Data data) {

    }

    @Override
    public void saveAdapterData() {
        try {
            mAdapter.saveDataToStorage(getActivity().openFileOutput(GifPictureFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        try {
            mAdapter.restoreDataFromStorage(getActivity().openFileInput(GifPictureFragment.class.getSimpleName()));
        } catch (FileNotFoundException e) {
            mAdapter.syncData("");
        }
    }

    public class WriteDataThread extends Thread {

        private String name;
        private byte[] data;

        WriteDataThread(String name, byte[] data) {
            this.data = data;
            this.name = name;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            try {
                if (name == null || name.isEmpty())
                    throw new IOException("name is empty");
                if (data == null || data.length <= 0)
                    throw new IOException("data is empty");
                fos = new FileOutputStream(new File(dir, name));
                fos.write(data);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
