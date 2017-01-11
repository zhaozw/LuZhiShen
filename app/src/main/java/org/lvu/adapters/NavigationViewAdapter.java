package org.lvu.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.main.activities.BaseActivity;
import org.lvu.models.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.HashSet;
import java.util.List;

/**
 * Created by wuyr on 1/4/17 5:21 PM.
 */

public class NavigationViewAdapter extends RecyclerView.Adapter<NavigationViewAdapter.ViewHolder> {
    //item类型
    private static final int ITEM_TYPE_CONTENT = 0, ITEM_TYPE_BOTTOM = 1;
    private int mBottomCount;//底部View个数
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mLayoutId;
    private List<Menu> mData;
    private OnItemClickListener mOnItemClickListener;
    private ViewHolder mSelectedHolder;
    private int mSelectedPos = -1;
    private HashSet<ViewHolder> holders;
    private boolean isInited;

    public NavigationViewAdapter(Context context, @LayoutRes int layoutId, List<Menu> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
        holders = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_BOTTOM && ImmerseUtil.isAboveKITKAT()
                && ImmerseUtil.isHasNavigationBar(mContext))
            return new FooterHolder(mLayoutInflater.inflate(
                    R.layout.navigation_view_adapter_footer, parent, false));
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (!handleFooterHolder(holder)) {
            if (mSelectedPos == position)
                syncStatus(holder);
            else holder.setSelected(false);
            holder.space.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
            Menu menu = mData.get(position);
            holder.title.setText(menu.getNameId());
            holder.icon.setImageResource(menu.getImageId());
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder != mSelectedHolder) {
                        if (holder.getAdapterPosition() < 5) {
                            syncStatus(holder);
                            mSelectedPos = holder.getAdapterPosition();
                            mSelectedHolder = holder;
                        }
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onClick(mData.get(holder.getAdapterPosition()).getNameId());
                    }
                }
            });
            if (position == 0 && !isInited) {
                holder.rootView.callOnClick();
                isInited = true;
            }
        }
    }

    private boolean handleFooterHolder(ViewHolder holder) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            LinearLayout.LayoutParams bottomLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ImmerseUtil.getNavigationBarHeight(mContext));
            footerHolder.bottomView.setLayoutParams(bottomLP);
            return true;
        }
        return false;
    }

    private void syncStatus(ViewHolder holder) {
        for (ViewHolder tmp : holders) {
            if (tmp != null && tmp != holder)
                tmp.setSelected(false);
        }
        holder.setSelected(true);
    }

    //内容长度
    private int getContentItemCount() {
        return mData.size();
    }

    //判断当前item类型
    @Override
    public int getItemViewType(int position) {
        return mBottomCount != 0 && position >= getContentItemCount() ?
                ITEM_TYPE_BOTTOM : ITEM_TYPE_CONTENT;
    }

    public void changeToLandscape() {
        mBottomCount = 0;
    }

    public void changeToPortrait() {
        mBottomCount = ImmerseUtil.isAboveKITKAT() && ImmerseUtil.isHasNavigationBar(mContext) ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return getContentItemCount() + mBottomCount;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int stringId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View rootView, space;
        TextView title;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root_view);
            title = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            space = itemView.findViewById(R.id.space);
            holders.add(this);
        }

        void setSelected(boolean selected) {
            rootView.setSelected(selected);
            title.setTextColor(((BaseActivity) mContext).getThemeColor(selected ? 3 : 0));
            title.getPaint().setFakeBoldText(selected);
        }
    }

    private class FooterHolder extends NavigationViewAdapter.ViewHolder {

        View bottomView;

        FooterHolder(View itemView) {
            super(itemView);
            bottomView = itemView.findViewById(R.id.navigation_bar_view);
        }
    }
}
