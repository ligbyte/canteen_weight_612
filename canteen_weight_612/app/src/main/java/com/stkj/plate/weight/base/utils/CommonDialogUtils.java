package com.stkj.plate.weight.base.utils;

import android.content.Context;

import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.net.AppNetManager;
import com.stkj.plate.weight.base.ui.dialog.BindingPwdAlertDialogFragment;
import com.stkj.plate.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.plate.weight.base.ui.dialog.CommonBindSignleAlertDialogFragment;
import com.stkj.plate.weight.login.helper.LoginHelper;

public class CommonDialogUtils {

    public static void showTipsDialog(Context context, String msg) {
        CommonBindSignleAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .show(context);
    }

    public static void showBindTipsDialog(Context context, String title, BindingPwdAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        BindingPwdAlertDialogFragment.build()
                .setAlertTitleTxt(title)
                .setRightNavTxt("取消")
                .setLeftNavClickListener(confirmClickListener)
                .show(context);
    }


    public static void showTipsDialog(Context context, String msg, String confirmText, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setLeftNavTxt(confirmText)
                .setLeftNavClickListener(confirmClickListener)
                .show(context);
    }

    public static void showTipsBindDialog(Context context, String title,String msg, String confirmText, CommonBindSignleAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonBindSignleAlertDialogFragment.build()
                .setAlertTitleTxt(title)
                .setAlertContentTxt(msg)
                .setLeftNavTxt(confirmText)
                .setLeftNavClickListener(confirmClickListener)
                .show(context);
    }

    public static void showAppResetDialog(Context context, String msg, CommonAlertDialogFragment.OnSweetClickListener confirmClickListener) {
        CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt(msg)
                .setNeedHandleDismiss(true)
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.setAlertContentTxt("数据清理中,请稍等...")
                                .setLeftNavTxt("清理中")
                                .setRightNavTxt("")
                                .setLeftNavClickListener(null);
                        //确认回调
                        if (confirmClickListener != null) {
                            confirmClickListener.onClick(alertDialogFragment);
                        }
                        //释放设备接口
                        DeviceManager.INSTANCE.getDeviceInterface().release();
                        //清理网络缓存
                        AppNetManager.INSTANCE.clearAppNetCache();
                        //清理用户登录信息
                        LoginHelper.INSTANCE.clearUserInfo();

                    }
                })
                .setRightNavTxt("取消")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        alertDialogFragment.dismiss();
                    }
                });
        commonAlertDialogFragment.show(context);
    }

}
