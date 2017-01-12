package com.yisuho.caumenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by xon23 on 2016-08-23.
 */
public class MyExpandAdapter extends BaseExpandableListAdapter {
    private ArrayList<Object> mGroupArrayList;
    private LayoutInflater mInflater;

    public MyExpandAdapter(ArrayList<Object> groupArrayList, Context context){
        super();

        mGroupArrayList = groupArrayList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mGroupArrayList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(mGroupArrayList.get(i) instanceof String){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object getGroup(int i) {
        return mGroupArrayList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v;

        if(mGroupArrayList.get(i) instanceof String){
            v = mInflater.inflate(R.layout.my_group_view_time, viewGroup, false);

            TextView tv = (TextView) v.findViewById(R.id.Tv);
            tv.setText((String) mGroupArrayList.get(i));

            v.setOnClickListener(null);
            return v;
        } else {
            v = mInflater.inflate(R.layout.my_group_view_name, viewGroup, false);

            TextView tv = (TextView) v.findViewById(R.id.Tv);
            tv.setText(((Menu) mGroupArrayList.get(i)).getName());
            return v;
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        View v = null;

        if(mGroupArrayList.get(i) instanceof Menu){
            v = mInflater.inflate(R.layout.my_group_view_description, viewGroup, false);

            TextView descriptionTv = (TextView) v.findViewById(R.id.descriptionTv);
            descriptionTv.setText(((Menu) mGroupArrayList.get(i)).getDescription());

            TextView priceTv = (TextView) v.findViewById(R.id.priceTv);
            priceTv.setText(((Menu) mGroupArrayList.get(i)).getPrice());
        }

        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
