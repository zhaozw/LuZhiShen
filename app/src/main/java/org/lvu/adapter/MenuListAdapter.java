package org.lvu.adapter;

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
import org.lvu.model.Menu;

import java.util.List;


/**
 * Created by wuyr on 3/31/16 10:10 PM.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyHolder> {

    private Context mContext;
    private int mLayoutId;
    private List<Menu> mData;
    private OnItemClickListener mListener;
    private View mLastSelected, mPosView;
    private int mLastSelectedPos = -1;

    public MenuListAdapter(Context context, @LayoutRes int layoutId, List<Menu> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        final Menu menu = mData.get(position);
        holder.icon.setImageResource(menu.getImageId());
        holder.name.setText(menu.getNameId());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelected != null)
                    mLastSelected.setSelected(false);
                if (mPosView != null)
                    mPosView.setSelected(false);
                holder.root.setSelected(true);
                mLastSelected = holder.root;
                if (mLastSelectedPos != holder.getAdapterPosition()) {
                    if (mListener != null)
                        mListener.onClick(menu.getNameId());
                    mLastSelectedPos = holder.getAdapterPosition();
                }
            }
        });
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

    static class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout root;
        ImageView icon;
        TextView name;

        public MyHolder(View itemView) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int stringId);
    }
}
