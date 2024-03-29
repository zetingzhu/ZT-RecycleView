package com.example.zzt.recycleview.anim2;


import android.view.animation.Interpolator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;


public class ScaleInLeftAnimator extends BaseItemAnimator {

  public ScaleInLeftAnimator() {
  }

  public ScaleInLeftAnimator(Interpolator interpolator) {
    mInterpolator = interpolator;
  }

  @Override
  protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setPivotX(0);
  }

  @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .scaleX(0)
        .scaleY(0)
        .setDuration(getRemoveDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultRemoveVpaListener(holder))
        .setStartDelay(getRemoveDelay(holder))
        .start();
  }

  @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setPivotX(0);
    holder.itemView.setScaleX(0);
    holder.itemView.setScaleY(0);
  }

  @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .scaleX(1)
        .scaleY(1)
        .setDuration(getAddDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultAddVpaListener(holder))
        .setStartDelay(getAddDelay(holder))
        .start();
  }
}
