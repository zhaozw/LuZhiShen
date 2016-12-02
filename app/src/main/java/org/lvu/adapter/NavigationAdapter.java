package org.lvu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;

import org.lvu.model.Menu;

import java.util.List;

/**
 * Created by wuyr on 7/26/16 9:59 PM.
 */
public class NavigationAdapter extends MenuListAdapter {

    public NavigationAdapter(Context context, @LayoutRes int layoutId, List<Menu> data) {
        super(context, layoutId, data);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Menu menu = mData.get(position);
        holder.icon.setImageResource(menu.getImageId());
        holder.name.setText(menu.getNameId());
        if (mOnItemClickListener != null)
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(menu.getNameId());
                }
            });
    }

    public void setData(List<Menu> data) {
        if (data != null) {
            mData = data;
            notifyDataSetChanged();
        }
    }
}
