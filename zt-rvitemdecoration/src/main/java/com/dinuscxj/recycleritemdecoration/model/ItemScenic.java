package com.dinuscxj.recycleritemdecoration.model;


import androidx.annotation.DrawableRes;

public class ItemScenic implements ItemDrawable {
    @DrawableRes
    private int mDrawableRes;

    private String mScenicName;

    public ItemScenic(int drawableRes, String scenicName) {
        this.mDrawableRes = drawableRes;
        this.mScenicName = scenicName;
    }

    @Override
    public int getDrawableRes() {
        return mDrawableRes;
    }

    public String getScenicName() {
        return mScenicName;
    }
}
