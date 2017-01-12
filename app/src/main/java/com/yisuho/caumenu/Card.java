package com.yisuho.caumenu;

/**
 * Created by xon23 on 2016-08-24.
 */
public class Card {
    private SimpleTime mStartTime;
    private SimpleTime mEndTime;
    private String[] mBuildingCodes;
    private int mTimeMark;

    public Card(int startHour, int startMinute, int endHour, int endMinute, String[] buildingCodes, int timeMark){
        mStartTime = new SimpleTime(startHour, startMinute);
        mEndTime = new SimpleTime(endHour, endMinute);
        mBuildingCodes = buildingCodes;
        mTimeMark = timeMark;
    }

    public String[] getBuildingCodes() {
        return mBuildingCodes;
    }

    public SimpleTime getEndTime() {
        return mEndTime;
    }

    public SimpleTime getStartTime() {
        return mStartTime;
    }

    public int getTimeMark() {
        return mTimeMark;
    }
}
