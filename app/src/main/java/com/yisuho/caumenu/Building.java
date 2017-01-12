package com.yisuho.caumenu;

/**
 * Created by xon23 on 2016-08-25.
 */
public class Building {
    private String mCode;
    private String mButtonText;
    private String mName;
    private String mDescription;
    private int mImageResourceId;
    private int mWeight;

    public Building(String code, String buttonText, String name, String description, int imageResourceId, int weight){
        mCode = code;
        mButtonText = buttonText;
        mName = name;
        mDescription = description;
        mImageResourceId = imageResourceId;
        mWeight = weight;
    }

    public String getCode() {
        return mCode;
    }

    public String getButtonText() {
        return mButtonText;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public int getWeight() {
        return mWeight;
    }
}
