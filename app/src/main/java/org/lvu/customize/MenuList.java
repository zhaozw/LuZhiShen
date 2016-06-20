package org.lvu.customize;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.lvu.R;
import org.lvu.adapter.MenuListAdapter;
import org.lvu.model.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 3/31/16 9:55 PM.
 */
public class MenuList extends LinearLayout {

    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private View mSkin,mExit,mBottomView;
    private Context mContext;

    public MenuList(Context context) {
        super(context);
        initViews(context);
    }

    public MenuList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public MenuList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.menu_list_view, this);
        mBottomView = view.findViewById(R.id.bottom_view);
        mSkin = view.findViewById(R.id.change_skin);
        mExit = view.findViewById(R.id.exit);

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
        mAdapter.setSelectedPos(0);
        if (ImmerseUtil.isAboveKITKAT())
            mRecyclerView.setPadding(0, ImmerseUtil.getStatusBarHeight(context), 0, 0);
    }

    private List<Menu> initData() {
        List<Menu> result = new ArrayList<>();
        result.add(new Menu(R.drawable.ic_navigation, R.string.menu_navigation));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_china_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_europe_video));
        result.add(new Menu(R.drawable.ic_video, R.string.menu_japan_video));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_family_take_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_asia_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_europe_pic));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_evil_pics));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_japan_pics));
        result.add(new Menu(R.drawable.ic_pic, R.string.menu_gif));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_excited_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_family_mess_novel));
        result.add(new Menu(R.drawable.ic_book, R.string.menu_lewd_wife_novel));
        return result;
    }

    public void changeToLandscape() {
        if (ImmerseUtil.isAboveKITKAT()) {
            mRecyclerView.setPadding(0, 0, 0, 0);
            if (ImmerseUtil.isHasNavigationBar(mContext))
                mBottomView.setVisibility(GONE);
        }
    }

    public void changeToPortrait() {
        if (ImmerseUtil.isAboveKITKAT()) {
            mRecyclerView.setPadding(0, ImmerseUtil.getStatusBarHeight(mContext), 0, 0);
            if (ImmerseUtil.isHasNavigationBar(mContext))
                mBottomView.setVisibility(VISIBLE);
        }
    }

    public void setOnItemListener(MenuListAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void setOnClickListener(OnClickListener listener){
        mSkin.setOnClickListener(listener);
        mExit.setOnClickListener(listener);
    }

}
