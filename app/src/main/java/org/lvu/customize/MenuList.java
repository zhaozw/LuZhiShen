package org.lvu.customize;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.lvu.R;
import org.lvu.adapters.MenuListAdapter;
import org.lvu.models.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 3/31/16 9:55 PM.
 */
public class MenuList extends LinearLayout {

    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private View mChangeSkinView, mDownloadManagerView, mExitView, mBottomView;
    private Context mContext;

    public MenuList(Context context) {
        this(context, null);
    }

    public MenuList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.menu_list_view, this);
        mBottomView = view.findViewById(R.id.bottom_view);
        mChangeSkinView = view.findViewById(R.id.change_skin);
        mDownloadManagerView = view.findViewById(R.id.download_manager);
        mExitView = view.findViewById(R.id.exit);

        if (ImmerseUtil.isAboveKITKAT() && ImmerseUtil.isHasNavigationBar(context)) {
            LayoutParams bottomLP = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    ImmerseUtil.getNavigationBarHeight(context));
            mBottomView.setLayoutParams(bottomLP);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter =
                new MenuListAdapter(context, R.layout.menu_list_item, initData()));
        /*if (ImmerseUtil.isAboveKITKAT())
            mRecyclerView.setPadding(0, ImmerseUtil.getStatusBarHeight(context), 0, 0);*/
    }

    private List<Menu> initData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_video, R.string.menu_china_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_europe_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_japan_video));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_family_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_asia_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_europe_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_evil_pics));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_gif));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_excited_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_family_mess_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_school_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_lewd_wife_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_funny_joke));
        return result;
    }

    public void setSelectedPos(int position) {
        mAdapter.setSelectedPos(position);
        mRecyclerView.scrollToPosition(position);
    }

    public void clearSelected() {
        mChangeSkinView.setSelected(false);
        mDownloadManagerView.setSelected(false);
        mExitView.setSelected(false);
    }

    public void setSelectedItem(MenuItem type) {
        mAdapter.clearSelected();
        switch (type) {
            case CHANGE_SKIN:
                mChangeSkinView.setSelected(true);
                break;
            case DOWNLOAD_MANAGER:
                mDownloadManagerView.setSelected(true);
                break;
            case EXIT:
                mExitView.setSelected(true);
                break;
            default:
        }
    }

    public void changeToLandscape() {
        if (ImmerseUtil.isAboveKITKAT()) {
            //mRecyclerView.setPadding(0, 0, 0, 0);
            if (ImmerseUtil.isHasNavigationBar(mContext))
                mBottomView.setVisibility(GONE);
            int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            MenuListAdapter.ViewHolder holder = null;
            if (position >= 0) {
                View view = mRecyclerView.getChildAt(position);
                if (view != null && mRecyclerView.getChildViewHolder(view) != null)
                    holder = (MenuListAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
            }
            mAdapter.setOrientation(MenuListAdapter.Orientation.LANDSCAPE, position, holder);
        }
    }

    public void changeToPortrait() {
        if (ImmerseUtil.isAboveKITKAT()) {
            //mRecyclerView.setPadding(0, ImmerseUtil.getStatusBarHeight(mContext), 0, 0);
            if (ImmerseUtil.isHasNavigationBar(mContext))
                mBottomView.setVisibility(VISIBLE);
            int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            MenuListAdapter.ViewHolder holder = null;
            if (position >= 0) {
                View view = mRecyclerView.getChildAt(position);
                if (view != null && mRecyclerView.getChildViewHolder(view) != null)
                    holder = (MenuListAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
            }
            mAdapter.setOrientation(MenuListAdapter.Orientation.PORTRAIT, position, holder);
        }
    }

    public void setOnItemClickListener(MenuListAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void setOnClickListener(OnClickListener listener) {
        mChangeSkinView.setOnClickListener(listener);
        mDownloadManagerView.setOnClickListener(listener);
        mExitView.setOnClickListener(listener);
    }

    public enum MenuItem {
        CHANGE_SKIN, DOWNLOAD_MANAGER, EXIT
    }
}
