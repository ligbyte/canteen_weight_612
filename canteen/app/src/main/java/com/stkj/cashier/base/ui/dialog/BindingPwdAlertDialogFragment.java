package com.stkj.cashier.base.ui.dialog;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.base.utils.MD5Utils;
import com.stkj.cashier.pay.model.BindFragmentSwitchEvent;
import com.stkj.cashier.pay.model.RefreshConsumerRefundModeEvent;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import org.greenrobot.eventbus.EventBus;

/**
 * 管理员密码
 */
public class BindingPwdAlertDialogFragment extends BaseDialogFragment {

    private TextView tvTitle;
    private EditText etAlertContent;
    private TextView stvLeftBt;
    private TextView tv_pwd_error_tips;
    private TextView stvRightBt;
    private boolean needHandleDismiss;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_binding_pwd_alert;
    }

    @Override
    protected void initViews(View rootView) {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(alertTitleTxt)) {
            tvTitle.setText(alertTitleTxt);
        }
        etAlertContent = (EditText) findViewById(R.id.et_alert_content);
        tv_pwd_error_tips = (TextView) findViewById(R.id.tv_pwd_error_tips);
        etAlertContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (!TextUtils.isEmpty(alertContentTxt)) {
            etAlertContent.setText(alertContentTxt);
        }
        stvLeftBt = (TextView) findViewById(R.id.stv_left_bt);
        stvRightBt = (TextView) findViewById(R.id.stv_right_bt);
        if (!TextUtils.isEmpty(leftNavTxt)) {
            stvLeftBt.setText(leftNavTxt);
        }
        stvLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etAlertContent.getText().toString())){
                    AppToast.toastMsg( "请输入密码");
                    return;
                }

                if (MD5Utils.encrypt(etAlertContent.getText().toString().trim()).equals(MD5Utils.TAG)){
                    //AppToast.toastMsg( "密码正确");
                    tv_pwd_error_tips.setVisibility(View.GONE);
                    etAlertContent.setText("");
                    EventBus.getDefault().post(new BindFragmentSwitchEvent(1));

                }else {
                    tv_pwd_error_tips.setVisibility(View.VISIBLE);
                    return;
                }

                if (!needHandleDismiss) {
                    dismiss();
                }
                if (mLeftNavClickListener != null) {
                    mLeftNavClickListener.onClick(BindingPwdAlertDialogFragment.this);
                }
            }
        });
        if (!TextUtils.isEmpty(rightNavTxt)) {
            stvRightBt.setVisibility(View.VISIBLE);
            stvRightBt.setText(rightNavTxt);
        } else {
            stvRightBt.setVisibility(View.GONE);
        }
        stvRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!needHandleDismiss) {
                    dismiss();
                }
                if (mRightNavClickListener != null) {
                    mRightNavClickListener.onClick(BindingPwdAlertDialogFragment.this);
                }
            }
        });
    }

    public BindingPwdAlertDialogFragment setNeedHandleDismiss(boolean needHandleDismiss) {
        this.needHandleDismiss = needHandleDismiss;
        return this;
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;

    private String leftNavTxt;

    /**
     * 设置左侧按钮文案
     */
    public BindingPwdAlertDialogFragment setLeftNavTxt(String leftNavTxt) {
        this.leftNavTxt = leftNavTxt;
        if (stvLeftBt != null) {
            stvLeftBt.setText(leftNavTxt);
        }
        return this;
    }

    private String rightNavTxt;

    /**
     * 设置右侧按钮文案
     */
    public BindingPwdAlertDialogFragment setRightNavTxt(String rightNavTxt) {
        this.rightNavTxt = rightNavTxt;
        if (stvRightBt != null) {
            if (!TextUtils.isEmpty(rightNavTxt)) {
                stvRightBt.setVisibility(View.VISIBLE);
                stvRightBt.setText(rightNavTxt);
            } else {
                stvRightBt.setVisibility(View.GONE);
            }
        }
        return this;
    }

    /**
     * 设置EDITTEXT文案
     */
    public BindingPwdAlertDialogFragment setEtText(String etText) {
        if (etAlertContent != null) {
            etAlertContent.setText(etText);
        }
        return this;
    }

    private String alertTitleTxt;

    /**
     * 设置弹窗标题
     */
    public BindingPwdAlertDialogFragment setAlertTitleTxt(String alertTitle) {
        this.alertTitleTxt = alertTitle;
        if (tvTitle != null) {
            tvTitle.setText(alertTitle);
        }
        return this;
    }

    private String alertContentTxt;

    /**
     * 设置弹窗内容
     */
    public BindingPwdAlertDialogFragment setAlertContentTxt(String alertContent) {
        this.alertContentTxt = alertContent;
        if (etAlertContent != null) {
            if (!TextUtils.isEmpty(alertContent)) {
                etAlertContent.setVisibility(View.VISIBLE);
                etAlertContent.setText(alertContent);
            } else {
                etAlertContent.setVisibility(View.GONE);
            }
        }
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public BindingPwdAlertDialogFragment setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
        return this;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public BindingPwdAlertDialogFragment setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
        return this;
    }

    public interface OnSweetClickListener {
        void onClick(BindingPwdAlertDialogFragment alertDialogFragment);
    }

    private static volatile BindingPwdAlertDialogFragment instance;

    public static BindingPwdAlertDialogFragment build() {
        if (instance == null) {
            synchronized (BindingPwdAlertDialogFragment.class) {
                if (instance == null) {
                    instance = new BindingPwdAlertDialogFragment();
                }
            }
        }
        return instance;
    }


}
