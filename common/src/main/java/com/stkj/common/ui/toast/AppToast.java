package com.stkj.common.ui.toast;

import android.text.TextUtils;
import android.widget.Toast;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.MainThreadHolder;

/**
 * app统一toast
 */
public class AppToast {

    /**
     * @param strResId 显示内容资源id
     */
    public static void toastMsgByStringResource(int strResId) {
        toastMsgByStringResource(strResId, Toast.LENGTH_SHORT);
    }

    /**
     * @param strResId 显示内容资源id
     * @param showTime 显示时间
     */
    public static void toastMsgByStringResource(int strResId, int showTime) {
        String msg = AppManager.INSTANCE.getApplication().getResources().getString(strResId);
        toastMsg(msg, showTime);
    }

    /**
     * @param msg 显示内容
     */
    public static void toastMsg(String msg) {
        toastMsg(msg, Toast.LENGTH_SHORT);
    }

    /**
     * @param msg 显示内容
     */
    public static void toastLongMsg(String msg) {
        toastMsg(msg, Toast.LENGTH_LONG);
    }

    /**
     * @param msg      显示内容
     * @param showTime 显示时间
     */
    public static void toastMsg(final String msg, final int showTime) {
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(AppManager.INSTANCE.getApplication(), msg, showTime).show();
                }
            }
        });
    }




}
