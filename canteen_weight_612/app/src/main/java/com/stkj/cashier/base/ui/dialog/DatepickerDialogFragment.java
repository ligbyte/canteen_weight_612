package com.stkj.cashier.base.ui.dialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.ui.adapter.FoodOrderDetailAdapter;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.home.model.StoreInfo;
import com.stkj.cashier.pay.model.ConsumeFoodBean;
import com.stkj.cashier.pay.model.FoodConsumeDetailResponse;
import com.stkj.cashier.pay.model.RefreshConsumerRefundModeEvent;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.deviceinterface.callback.OnPrintListener;
import com.stkj.deviceinterface.model.PrinterData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.wcy.datepicker.DatePickerView;

/**
 * 日期选择
 */
public class DatepickerDialogFragment extends BaseDialogFragment {

    public final static String TAG = "OrderAlertDialogFragment";
    private TextView tvTitle;
//    private TextView tvAlertContent;
    private ShapeTextView stvLeftBt;
    private ShapeTextView stvRightBt;
    private boolean needHandleDismiss;
    private OnDateSetListener onDateSetListener;
    private DatePickerView datePickerView;
    private int year = 2025;
    private int month = 10;
    private int day = 13;


    public DatepickerDialogFragment(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DatepickerDialogFragment() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_date_picker;
    }

    public static DatepickerDialogFragment build(int year, int month, int day) {
        return new DatepickerDialogFragment(year,month,day);
    }

    public DatepickerDialogFragment setNeedHandleDismiss(boolean needHandleDismiss) {
        this.needHandleDismiss = needHandleDismiss;
        return this;
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;


    private String leftNavTxt;

    /**
     * 设置左侧按钮文案
     */
    public DatepickerDialogFragment setLeftNavTxt(String leftNavTxt) {
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
    public DatepickerDialogFragment setRightNavTxt(String rightNavTxt) {
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

    private String alertTitleTxt;

    /**
     * 设置弹窗标题
     */
    public DatepickerDialogFragment setAlertTitleTxt(String alertTitle) {
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
    public DatepickerDialogFragment setAlertContentTxt(String alertContent) {
        this.alertContentTxt = alertContent;
//        if (tvAlertContent != null) {
//            if (!TextUtils.isEmpty(alertContent)) {
//                tvAlertContent.setVisibility(View.VISIBLE);
//                tvAlertContent.setText(alertContent);
//            } else {
//                tvAlertContent.setVisibility(View.GONE);
//            }
//        }
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public DatepickerDialogFragment setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
        return this;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public DatepickerDialogFragment setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
        return this;
    }

    public interface OnSweetClickListener {
        void onClick(DatepickerDialogFragment alertDialogFragment);
    }

    @Override
    protected void initViews(View rootView) {
        try {
            tvTitle = (TextView) findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(alertTitleTxt)) {
                tvTitle.setText(alertTitleTxt);
            }


            datePickerView = (DatePickerView) findViewById(R.id.datePickerView);
            datePickerView.updateDay(year,month,day);
            stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
            stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
            if (!TextUtils.isEmpty(leftNavTxt)) {
                stvLeftBt.setText(leftNavTxt);
            }
            stvLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onDateSetListener != null) {
                        onDateSetListener.onDateSet(DatepickerDialogFragment.this,datePickerView.getSelectedYear(),datePickerView.getSelectedMonth(),datePickerView.getSelectedDay());
                    }else {
                        if (!needHandleDismiss) {
                            dismiss();
                        }
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
                        mRightNavClickListener.onClick(DatepickerDialogFragment.this);
                    }

                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return true;
                }

                @Override
                public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                    super.onMeasure(recycler, state, widthSpec, heightSpec);
                }
            };



        }catch (Exception e){
            Log.d(TAG, "limeinitViews: " + e.getMessage());
        }
    }

    public DatepickerDialogFragment setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
        return this;
    }

    public interface OnDateSetListener {
        /**
         * @param dialogFragment the picker associated with the dialog
         * @param year the selected year
         * @param month the selected month (0-11 for compatibility with
         *              {@link Calendar#MONTH})
         * @param dayOfMonth the selected day of the month (1-31, depending on
         *                   month)
         */
        void onDateSet(DatepickerDialogFragment dialogFragment, int year, int month, int dayOfMonth);
    }

}
