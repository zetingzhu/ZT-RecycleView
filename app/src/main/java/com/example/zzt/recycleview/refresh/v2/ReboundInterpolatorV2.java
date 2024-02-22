package com.example.zzt.recycleview.refresh.v2;

import android.view.animation.Interpolator;

/**
 * @author: zeting
 * @date: 2024/2/22
 */
public class ReboundInterpolatorV2 implements Interpolator {

    // interpolator type
    public static final int INTERPOLATOR_VISCOUS_FLUID = 0;
    public static final int INTERPOLATOR_DECELERATE = 1;

    private static final float VISCOUS_FLUID_SCALE = 8.0f;

    private int mInterpolatorType;

    // must be set to 1.0 (used in viscousFluid())
    private float VISCOUS_FLUID_NORMALIZE = 1.0f;
    // account for very small floating-point error
    private float VISCOUS_FLUID_OFFSET = 1.0f;

    public ReboundInterpolatorV2(int mInterpolatorType) {
        this.mInterpolatorType = mInterpolatorType;
        // must be set to 1.0 (used in viscousFluid())
        VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f);
        // account for very small floating-point error
        VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f);

    }

    private Float viscousFluid(Float x) {
        float lx = x;
        lx *= VISCOUS_FLUID_SCALE;
        if (lx < 1.0f) {
            lx -= 1.0f - Math.exp(-lx);
        } else {
            float start = 0.36787944117f; // 1/e == exp(-1)
            lx = (float) (1.0f - Math.exp((1.0f - lx)));
            lx = start + lx * (1.0f - start);
        }
        return lx;
    }


    @Override
    public float getInterpolation(float input) {
        if (mInterpolatorType == INTERPOLATOR_DECELERATE) {
            return 1.0f - (1.0f - input) * (1.0f - input);
        }
        float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
        if (interpolated > 0) {
            return interpolated + VISCOUS_FLUID_OFFSET;
        } else {
            return interpolated;
        }
    }
}
