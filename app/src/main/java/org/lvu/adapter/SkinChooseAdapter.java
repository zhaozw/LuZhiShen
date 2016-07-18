package org.lvu.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.model.Menu;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wuyr on 4/2/16 11:56 PM.
 */
public class SkinChooseAdapter extends ArrayAdapter<Menu> {

    private Context mContext;
    private int mResource;
    private List<Menu> mData;
    //private SkinChooseAdapter.OnItemClickListener mListener;

    public SkinChooseAdapter(Context context, int resource, List<Menu> objects) {
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
            if (!Application.getCurrentSkin()
                    .equals(mContext.getString(R.string.skin_black))) {
                holder.root = (MaterialRippleLayout) convertView.findViewById(R.id.ripple_layout);
                List<Integer> data = new ArrayList<>();
                int[] array = R.styleable.AppCompatTheme;
                for (int tmp : array)
                    data.add(tmp);
                TypedArray a = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
                int color = a.getColor(data.indexOf(R.attr.colorAccent),
                        mContext.getResources().getColor(R.color.blueAccent));
                holder.root.setRippleColor(color);
                a.recycle();
            }
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Menu menu = mData.get(position);
        holder.icon.setImageResource(menu.getImageId());
        /*if (mListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(menu.getNameId());
                }
            });
        }*/
        holder.name.setText(menu.getNameId());
        holder.name.setTextColor(mContext.getResources().getColor(Application.getCurrentSkin()
                .equals(mContext.getString(R.string.skin_black)) ?
                R.color.menu_text_color : R.color.menu_text_color_dark));
        return convertView;
    }

    class ViewHolder {
        MaterialRippleLayout root;
        ImageView icon;
        TextView name;
    }

    /*public SkinChooseAdapter setOnItemClickListener(SkinChooseAdapter.OnItemClickListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnItemClickListener {
        void onClick(int stringId);
    }*/
}
