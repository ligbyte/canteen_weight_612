package com.stkj.plate.weight.home.ui.widget.charview;

import android.content.Context;

import java.util.Collection;
/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * FileName: Test
 * Author: Lime
 * Date: 2025/7/18 9:32
 * Description: 工具类
 */
class Utils {
    static int dip2px (Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dipValue * scale);
    }

    static boolean isEmpty (Collection datas) {
        return datas == null || datas.isEmpty();
    }

    static <T> boolean isIn (T target, T... in) {
        if (target == null || in == null || in.length == 0) {
            return false;
        }
        for (T each : in) {
            if (target.equals(each)) {
                return true;
            }
        }
        return false;
    }

    static <T> T getNotNullValue (T... value) {
        if (value == null || value.length == 0)
            return null;
        for (T item : value) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }
}
