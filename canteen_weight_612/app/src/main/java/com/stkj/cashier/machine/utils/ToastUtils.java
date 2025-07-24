package com.stkj.cashier.machine.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.home.ui.widget.ToastUtil;
import com.stkj.common.core.MainThreadHolder;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * FileName: Lime
 * Author: Lime
 * Date: 2025/7/21 16:22
 * Description: Toast 自定义
 */
public class ToastUtils {


    /**
     * @param msg 显示内容
     */
    public static void toastMsgSuccess(String msg) {
        toastMsgSuccess(msg, Toast.LENGTH_SHORT);
    }


    /**
     * @param msg 显示内容
     */
    public static void toastMsgWarning(String msg) {
        toastMsgWarning(msg, Toast.LENGTH_SHORT);
    }


    /**
     * @param msg      显示内容
     * @param showTime 显示时间
     */
    public static void toastMsgWarning(final String msg, final int showTime) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(MainApplication.instances, R.layout.toast_center_warning, msg);
                    toastUtil2.show();
                }
            }
        });
    }

    /**
     * @param msg 显示内容
     */
    public static void toastMsgError(String msg) {
        toastMsgError(msg, Toast.LENGTH_SHORT);
    }

    /**
     * @param msg      显示内容
     * @param showTime 显示时间
     */
    public static void toastMsgSuccess(final String msg, final int showTime) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(MainApplication.instances, R.layout.toast_center_horizontal, msg);
                    toastUtil2.show();
                }
            }
        });
    }

    /**
     * @param msg      显示内容
     * @param showTime 显示时间
     */
    public static void toastMsgError(final String msg, final int showTime) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil toastUtil2 = new ToastUtil(MainApplication.instances, R.layout.toast_center_error, msg);
                    toastUtil2.show();
                }
            }
        });
    }

}
