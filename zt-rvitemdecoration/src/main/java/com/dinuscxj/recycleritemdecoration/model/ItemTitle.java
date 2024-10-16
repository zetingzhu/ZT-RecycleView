package com.dinuscxj.recycleritemdecoration.model;


import androidx.annotation.DrawableRes;

public class ItemTitle implements ItemDrawable {
    @DrawableRes
    private int mDrawableRes;

    private String mTitleName;

    public ItemTitle(int drawableRes, String titleName) {
        this.mDrawableRes = drawableRes;
        this.mTitleName = titleName;
    }

    @Override
    public int getDrawableRes() {
        return mDrawableRes;
    }

    public String getTitleName() {
        return mTitleName;
    }
}
