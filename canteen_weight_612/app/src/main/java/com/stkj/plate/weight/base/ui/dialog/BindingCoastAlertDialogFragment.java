package com.stkj.plate.weight.base.ui.dialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.home.ui.widget.charview.ChartBarView;
import com.stkj.plate.weight.home.ui.widget.charview.bean.Item;
import com.stkj.plate.weight.home.ui.widget.charview.bean.ItemList;
import com.stkj.common.ui.fragment.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Random;

/**
 * 营业统计
 */
public class BindingCoastAlertDialogFragment extends BaseDialogFragment {

    private TextView tvTitle;
    private TextView tv_alert_content;
    private ImageView iv_close;
    private TextView tv_pwd_error_tips;
    private boolean needHandleDismiss;
    private ChartBarView chartbarview;
    private final Random random = new Random();

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_binding_coast_alert;
    }

    @Override
    protected void initViews(View rootView) {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        iv_close=(ImageView) findViewById(R.id.iv_close);
        tv_alert_content=(TextView) findViewById(R.id.tv_alert_content);
        chartbarview = (ChartBarView) findViewById(R.id.chartbarview);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/D-DIN-PRO-700-Bold.otf");
        tv_alert_content.setTypeface(typeface);
        if (!TextUtils.isEmpty(alertTitleTxt)) {
            tvTitle.setText(alertTitleTxt);
        }
        tv_pwd_error_tips = (TextView) findViewById(R.id.tv_pwd_error_tips);




        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        configSingLeftChart();
    }

    public BindingCoastAlertDialogFragment setNeedHandleDismiss(boolean needHandleDismiss) {
        this.needHandleDismiss = needHandleDismiss;
        return this;
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;

    private String leftNavTxt;

    /**
     * 设置左侧按钮文案
     */
    public BindingCoastAlertDialogFragment setLeftNavTxt(String leftNavTxt) {
        this.leftNavTxt = leftNavTxt;

        return this;
    }

    private String rightNavTxt;

    /**
     * 设置右侧按钮文案
     */
    public BindingCoastAlertDialogFragment setRightNavTxt(String rightNavTxt) {
        this.rightNavTxt = rightNavTxt;

        return this;
    }

    /**
     * 设置EDITTEXT文案
     */
    public BindingCoastAlertDialogFragment setEtText(String etText) {

        return this;
    }

    private String alertTitleTxt;

    /**
     * 设置弹窗标题
     */
    public BindingCoastAlertDialogFragment setAlertTitleTxt(String alertTitle) {
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
    public BindingCoastAlertDialogFragment setAlertContentTxt(String alertContent) {
        this.alertContentTxt = alertContent;

        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public BindingCoastAlertDialogFragment setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
        return this;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public BindingCoastAlertDialogFragment setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
        return this;
    }

    public interface OnSweetClickListener {
        void onClick(BindingCoastAlertDialogFragment alertDialogFragment);
    }

    private static volatile BindingCoastAlertDialogFragment instance;

    public static BindingCoastAlertDialogFragment build() {
        if (instance == null) {
            synchronized (BindingCoastAlertDialogFragment.class) {
                if (instance == null) {
                    instance = new BindingCoastAlertDialogFragment();
                }
            }
        }
        return instance;
    }


    private void configSingLeftChart () {
        // 百分比树状图
        ItemList.TreeInfo treeInfo = new ItemList.TreeInfo(dp2px(20), true);
        ArrayList<Item> data = new ArrayList<>();
        float min = 0;
        float max = 1F;
        data.add(new Item("早餐", random.nextFloat()));
        data.add(new Item("午餐", random.nextFloat()));
        data.add(new Item("晚餐", random.nextFloat()));
        data.add(new Item("夜餐", random.nextFloat()));
        data.get(data.size() - 1).setValue(max);
        data.get(0).setValue(min);
        ItemList rightAxisPercent = new ItemList(treeInfo, data);
        rightAxisPercent.setAxis(ItemList.AxisAlign.LEFT, ItemList.AxisValueType.PERCENT, max, min);
        rightAxisPercent.setColor(0xff3584FF);

        rightAxisPercent.setShowTip(true);
        rightAxisPercent.setTipColor(Color.WHITE);
        rightAxisPercent.setTipSize(dp2px(13));
        chartbarview.setSingletonData(rightAxisPercent);
    }




    int dp2px (float dipValue) {
        return Math.round(dipValue * getResources().getDisplayMetrics().density);
    }

}
