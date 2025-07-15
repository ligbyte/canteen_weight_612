package com.stkj.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DimensUtils {

    // 将 SP 转换为像素（Int 版本）
    public static int spToPx(Context context, int sps) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(sps * metrics.scaledDensity);
    }

    // 将 DP 转换为像素（Int 版本）
    public static int dpToPx(Context context, int dps) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(dps * metrics.density);
    }

    // 将 SP 转换为像素（Float 版本）
    public static float spToPx(Context context, float sps) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sps, metrics);
    }

    // 将 DP 转换为像素（Float 版本）
    public static float dpToPx(Context context, float dps) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, metrics);
    }
}
