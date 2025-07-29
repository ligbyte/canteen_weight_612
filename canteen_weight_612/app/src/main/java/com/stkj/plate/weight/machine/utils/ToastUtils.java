package com.stkj.plate.weight.machine.utils;

import android.text.TextUtils;
import android.view.Gravity;

import com.stkj.common.core.AppManager;
import com.stkj.common.core.MainThreadHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.home.ui.widget.ToastUtil;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/7/21 16:22
 * Description: Toast 自定义
 */
public class ToastUtils {


    /**
     * @param msg 显示内容
     */
    public static void toastMsgSuccess(String msg) {
        toastMsgSuccess(msg, Gravity.BOTTOM);
    }


    /**
     * @param msg 显示内容
     */
    public static void toastMsgWarning(String msg) {
        toastMsgWarning(msg, Gravity.BOTTOM);
    }

    /**
     * @param msg 显示内容
     */
    public static void toastMsgError(String msg) {
        toastMsgError(msg, Gravity.BOTTOM);
    }

    /**
     * @param msg      显示内容
     */
    public static void toastMsgSuccess(final String msg, final int gravity) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(AppManager.INSTANCE.getApplication(), R.layout.toast_center_horizontal, msg,gravity);
                    toastUtil2.show();
                }
            }
        });
    }

    /**
     * @param msg      显示内容
     */
    public static void toastMsgWarning(final String msg, final int gravity) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(AppManager.INSTANCE.getApplication(), R.layout.toast_center_warning, msg,gravity);
                    toastUtil2.show();
                }
            }
        });
    }

    /**
     * @param msg      显示内容
     */
    public static void toastMsgError(final String msg, final int gravity) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(AppManager.INSTANCE.getApplication(), R.layout.toast_center_error, msg,gravity);
                    toastUtil2.show();
                }
            }
        });
    }

}
