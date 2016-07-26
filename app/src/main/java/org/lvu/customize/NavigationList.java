package org.lvu.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lvu.R;
import org.lvu.adapter.NavigationAdapter;
import org.lvu.model.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 7/27/16 12:36 AM.
 */
public class NavigationList extends LinearLayout {

    //private Context mContext;
    private TextView mTitle;
    private NavigationAdapter mAdapter;

    public NavigationList(Context context) {
        super(context);
        init(context, null, 0);
    }

    public NavigationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NavigationList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.customize_navigation_list_item, this);
        mTitle = (TextView) view.findViewById(R.id.area_title);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.NavigationList, defStyleAttr, 0);
        setTitle(a.getString(R.styleable.NavigationList_area_title));
        a.recycle();
        mAdapter = new NavigationAdapter(context, R.layout.navigation_list_item,new ArrayList<Menu>());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(NavigationAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void setData(List<Menu> data) {
        mAdapter.setData(data);
    }

    public void setTitle(String title) {
        if (title != null)
            mTitle.setText(title);
    }

}
