package com.yisuho.caumenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by xon23 on 2016-08-16.
 */
public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.ViewHolder> {
    private Card[] mCards;
    private Activity mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public MyCardAdapter(Card[] cards, Activity activity) {
        mCards = cards;
        mActivity = activity;
    }

    @Override
    public MyCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_card_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        TextView currentTv = (TextView) holder.mView.findViewById(R.id.currentTv);

        if(mCards[position].getTimeMark() < 0){
            currentTv.setText("종료");
            currentTv.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.cardNotCurrentColor));
        } else if(mCards[position].getTimeMark() > 0){
            currentTv.setText("예정");
            currentTv.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.cardNotCurrentColor));
        }
        else {
            currentTv.setText("현재");
            currentTv.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.cardCurrentColor));
        }

        TextView timeTv = (TextView) holder.mView.findViewById(R.id.timeTv);
        String timeString = mCards[position].getStartTime().getTimeString() + " ~ "
                + mCards[position].getEndTime().getTimeString();
        timeTv.setText(timeString);

        TextView availableTv = (TextView) holder.mView.findViewById(R.id.availableTv);
        availableTv.setText("이용 가능한 식당 " + mCards[position].getBuildingCodes().length + "개");

        FlowLayout buildingFlowLayout = (FlowLayout) holder.mView.findViewById(R.id.buildingFlowLayout);

        ArrayList<Button> normalBuildingBts = new ArrayList<>();
        ArrayList<Button> favoriteBuildingBts = new ArrayList<>();

        for (String buildingCode: mCards[position].getBuildingCodes()) {
            Button newBt = (Button) LayoutInflater.from(holder.mView.getContext())
                    .inflate(R.layout.my_round_button, buildingFlowLayout, false);
            String buttonText = MyDataManager.BUILDING_CODE_MAP.get(buildingCode).getButtonText();
            newBt.setText(buttonText);

            final String code = buildingCode;
            newBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), BuildingPageActivity.class);
                    intent.putExtra(MyDataManager.CONTENT_TYPE_BUILDING_CODE, code);
                    intent.putExtra(MyDataManager.CONTENT_TYPE_FILTER_START_TIME, mCards[position].getStartTime().getTotalMin());
                    intent.putExtra(MyDataManager.CONTENT_TYPE_FILTER_END_TIME, mCards[position].getEndTime().getTotalMin());
                    mActivity.startActivityForResult(intent, MainActivity.REQUEST_UPDATE_CARDS);

                }
            });

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(holder.mView.getContext());
            if(sharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + buildingCode, false)){
                newBt.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
                newBt.setTypeface(null, Typeface.BOLD);
                favoriteBuildingBts.add(newBt);
            }else{
                normalBuildingBts.add(newBt);
            }
        }

        buildingFlowLayout.removeAllViews();
        for (Button button: favoriteBuildingBts) {
            buildingFlowLayout.addView(button);
        }
        for (Button button: normalBuildingBts) {
            buildingFlowLayout.addView(button);
        }
    }

    @Override
    public int getItemCount() {
        if(mCards == null){
            return 0;
        }
        return mCards.length;
    }
}
