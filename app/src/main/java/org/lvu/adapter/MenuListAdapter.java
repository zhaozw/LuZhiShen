package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.model.Menu;
import org.lvu.utils.ImmerseUtil;

import java.util.List;


/**
 * Created by wuyr on 3/31/16 10:10 PM.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mLayoutId;
    List<Menu> mData;
    OnItemClickListener mOnItemClickListener;
    private View mLastSelectedView, mPosView;
    private int mLastSelectedPos = -1;
    private boolean isPortrait;

    public MenuListAdapter(Context context, @LayoutRes int layoutId, List<Menu> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Menu menu = mData.get(position);
        if (ImmerseUtil.isAboveKITKAT()) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            if (isPortrait && menu.getNameId() == R.string.menu_china_video)
                lp.topMargin = ImmerseUtil.getStatusBarHeight(mContext);
            else lp.topMargin = 0;
            holder.root.setLayoutParams(lp);
        }
        holder.icon.setImageResource(menu.getImageId());
        holder.name.setText(menu.getNameId());
        if (mOnItemClickListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLastSelectedView != null) {
                        mLastSelectedView.setSelected(false);
                        mLastSelectedView = null;
                    }
                    if (mPosView != null) {
                        mPosView.setSelected(false);
                        mPosView = null;
                    }
                    holder.root.setSelected(true);
                    mLastSelectedView = holder.root;
                    if (mLastSelectedPos != holder.getAdapterPosition()) {
                        mOnItemClickListener.onClick(menu.getNameId());
                        mLastSelectedPos = holder.getAdapterPosition();
                    }
                }
            });
        }
        if (mLastSelectedPos == position) {
            holder.root.setSelected(true);
            mPosView = holder.root;
        } else holder.root.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setSelectedPos(int position) {
        mLastSelectedPos = position;
    }

    public void setOrientation(Orientation orientation, int position, ViewHolder holder) {
        isPortrait = orientation == Orientation.PORTRAIT;
        if (holder == null)
            return;
        Menu item = mData.get(position);
        if (item.getNameId() == R.string.menu_china_video) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            if (isPortrait)
                lp.topMargin = ImmerseUtil.getStatusBarHeight(mContext);
            else lp.topMargin = 0;
            holder.root.setLayoutParams(lp);
        }
    }

    public enum Orientation {
        PORTRAIT, LANDSCAPE
    }

    public void clearSelected() {
        if (mLastSelectedView != null) {
            mLastSelectedView.setSelected(false);
            mLastSelectedView = null;
        }
        if (mPosView != null) {
            mPosView.setSelected(false);
            mPosView = null;
        }
        mLastSelectedPos = -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View root;
        public ImageView icon;
        public TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int stringId);
    }
}
