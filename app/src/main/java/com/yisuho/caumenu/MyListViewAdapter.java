package com.yisuho.caumenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xon23 on 2016-08-16.
 */
public  class MyListViewAdapter extends BaseAdapter{
    private String[] mBuildingCodes;
    private SharedPreferences mSharedPref;

    public MyListViewAdapter(String[] buildingCodes, SharedPreferences sharedPref){
        mBuildingCodes = buildingCodes;
        mSharedPref = sharedPref;
    }

    @Override
    public int getCount() {
        return mBuildingCodes.length;
    }

    @Override
    public Object getItem(int i) {
        return mBuildingCodes[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.my_listview_item, viewGroup, false);
        }

        Building building = MyDataManager.BUILDING_CODE_MAP.get(mBuildingCodes[i]);

        ((ImageView)view.findViewById(R.id.buildingImageView))
                .setImageResource(building.getImageResourceId());

        ((TextView)view.findViewById(R.id.firstTv)).setText(building.getName());

        ((TextView)view.findViewById(R.id.secondTv)).setText(building.getDescription());

        if(mSharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCodes[i], false)){
            ((ImageView)view.findViewById(R.id.favIv)).setImageResource(R.drawable.ic_star);
        } else {
            ((ImageView)view.findViewById(R.id.favIv)).setImageResource(R.drawable.ic_star_outline);
        }

        return view;
    }
}