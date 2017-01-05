package org.lvu.main.fragments.view_pager_content;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.adapters.BaseListAdapter;
import org.lvu.adapters.GifPictureAdapter;
import org.lvu.customize.MySnackBar;
import org.lvu.main.activities.NewMainActivity;
import org.lvu.models.Data;
import org.lvu.utils.HttpUtil;

import java.io.File;
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
    protected void init() {
        super.init();
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                GifPictureAdapter.ViewHolder gifHolder = (GifPictureAdapter.ViewHolder) holder;
                if (gifHolder != null) {
                    if (gifHolder.progress != null)
                        gifHolder.progress.setVisibility(View.GONE);
                    gifHolder.setGifPlaying(false);
                }
            }
        });
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return new GifPictureAdapter(getActivity(), R.layout.adapter_gif_item, new ArrayList<Data>());
    }

    @Override
    protected BaseListAdapter.OnItemClickListener getOnItemClickListener() {
        return new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onClick(String url, String text, final int position) {
                final GifPictureAdapter.ViewHolder holder = (GifPictureAdapter.ViewHolder) mAdapter.getHolderByPosition(mRecyclerView, position);
                if (holder != null) {
                    if (holder.progress.getVisibility() == View.VISIBLE || holder.isGifPlaying())
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
                            if (holder.progress.getVisibility() != View.GONE) {
                                holder.image.setImageDrawable(new GifDrawable(gifFile));
                                holder.setGifPlaying(true);
                                mRecyclerView.smoothScrollToPosition(position);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null && ((NewMainActivity) getActivity()).getRootView() != null)
                                        MySnackBar.show(((NewMainActivity) getActivity()).getRootView(), getString(R.string.load_pic_fail), Snackbar.LENGTH_SHORT);
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
                                    if (holder.progress.getVisibility() != View.GONE) {
                                        holder.image.setImageDrawable(new GifDrawable(responseBody));
                                        holder.setGifPlaying(true);
                                        mRecyclerView.smoothScrollToPosition(position);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    holder.progress.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getActivity() != null && ((NewMainActivity) getActivity()).getRootView() != null)
                                                MySnackBar.show(((NewMainActivity) getActivity()).getRootView(), "加载图片失败", Snackbar.LENGTH_SHORT);
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
                                        if (getActivity() != null && ((NewMainActivity) getActivity()).getRootView() != null)
                                            MySnackBar.show(((NewMainActivity) getActivity()).getRootView(), HttpUtil.REASON_CONNECT_SERVER_FAILURE, Snackbar.LENGTH_SHORT);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        };
    }

    public static boolean isThisGifLoaded(String url) {
        return getGifFileByUrl(url).exists();
    }

    public static File getGifFileByUrl(String url) {
        return new File(ImageLoader.getInstance().getDiskCache().getDirectory(),
                new HashCodeFileNameGenerator().generate(url));
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
        //TODO: if gif is cached auto play gif
    }

    @Override
    public void saveAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.saveDataToStorage(Application.getContext().openFileOutput(GifPictureFragment.class.getSimpleName(), Context.MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void restoreAdapterData() {
        if (mAdapter == null) return;
        try {
            mAdapter.restoreDataFromStorage(Application.getContext().openFileInput(GifPictureFragment.class.getSimpleName()));
        } catch (Exception e) {
            mAdapter.syncData("");
        }
    }

    private class WriteDataThread extends Thread {

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
