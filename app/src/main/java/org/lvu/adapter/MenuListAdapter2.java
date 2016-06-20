package org.lvu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.model.Menu;

import java.util.List;


/**
 * Created by wuyr on 4/2/16 11:56 PM.
 */
public class MenuListAdapter2 extends ArrayAdapter<Menu> {

    private Context mContext;
    private int mResource;
    private List<Menu> mData;
    private MenuListAdapter2.OnItemClickListener mListener;

    public MenuListAdapter2(Context context, int resource, List<Menu> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.root = (LinearLayout) convertView.findViewById(R.id.root);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Menu menu = mData.get(position);
        holder.icon.setImageResource(menu.getImageId());
        if (mListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(menu.getNameId());
                }
            });
        }
        holder.name.setText(menu.getNameId());
        holder.name.setTextColor(mContext.getResources().getColor(Application.getCurrentSkin()
                .equals(mContext.getString(R.string.skin_black)) ?
                R.color.menu_text_color : R.color.menu_text_color_dark));
        return convertView;
    }

    class ViewHolder {
        LinearLayout root;
        ImageView icon;
        TextView name;
    }

    public MenuListAdapter2 setOnItemClickListener(MenuListAdapter2.OnItemClickListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnItemClickListener {
        void onClick(int stringId);
    }
}
