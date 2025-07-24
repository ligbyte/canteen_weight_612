package com.stkj.plate.weight.base.ui.dialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.michael.easydialog.EasyDialog;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.ui.adapter.FoodOrderDetailAdapter;
import com.stkj.plate.weight.base.utils.CommonDialogUtils;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.home.model.StoreInfo;
import com.stkj.plate.weight.pay.model.ConsumeFoodBean;
import com.stkj.plate.weight.pay.model.FoodConsumeDetailResponse;
import com.stkj.plate.weight.pay.model.RefreshConsumerRefundModeEvent;
import com.stkj.plate.weight.setting.helper.StoreInfoHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.plate.deviceinterface.callback.OnPrintListener;
import com.plate.deviceinterface.model.PrinterData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Order详情
 */
public class OrderAlertDialogFragment extends BaseDialogFragment {

    public final static String TAG = "OrderAlertDialogFragment";
    private TextView tvTitle;
//    private TextView tvAlertContent;
    private ShapeTextView stvLeftBt;
    private ShapeTextView stvRightBt;
    private boolean needHandleDismiss;
    private final FoodConsumeDetailResponse foodConsumeDetailResponse;
    private TextView tv_ddh;
    private TextView tv_ssct;
    private TextView tv_yhm;
    private TextView tv_ssck;
    private TextView tv_zh;
    private TextView tv_ssbm;
    private TextView tv_jssj;
    private TextView tv_cb;
    private TextView tv_jshj;
    private TextView tv_jslb;
    private TextView tv_ysze;
    private TextView tv_ssze;
    private ImageView iv_close;
    private ImageView food_tips;
    private RecyclerView rv_goods_list;
    private FoodOrderDetailAdapter foodOrderDetailAdapter;
    private String jslb = "";

    public OrderAlertDialogFragment(FoodConsumeDetailResponse foodConsumeDetailResponse) {
        this.foodConsumeDetailResponse = foodConsumeDetailResponse;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_order_alert;
    }

    public static OrderAlertDialogFragment build(FoodConsumeDetailResponse foodConsumeDetailResponse) {
        return new OrderAlertDialogFragment(foodConsumeDetailResponse);
    }

    public OrderAlertDialogFragment setNeedHandleDismiss(boolean needHandleDismiss) {
        this.needHandleDismiss = needHandleDismiss;
        return this;
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;

    private String leftNavTxt;

    /**
     * 设置左侧按钮文案
     */
    public OrderAlertDialogFragment setLeftNavTxt(String leftNavTxt) {
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
    public OrderAlertDialogFragment setRightNavTxt(String rightNavTxt) {
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
    public OrderAlertDialogFragment setAlertTitleTxt(String alertTitle) {
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
    public OrderAlertDialogFragment setAlertContentTxt(String alertContent) {
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
    public OrderAlertDialogFragment setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
        return this;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public OrderAlertDialogFragment setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
        return this;
    }

    public interface OnSweetClickListener {
        void onClick(OrderAlertDialogFragment alertDialogFragment);
    }

    @Override
    protected void initViews(View rootView) {
        try {
            tvTitle = (TextView) findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(alertTitleTxt)) {
                tvTitle.setText(alertTitleTxt);
            }
//        tvAlertContent = (TextView) findViewById(R.id.tv_alert_content);
//        tvAlertContent.setMovementMethod(ScrollingMovementMethod.getInstance());
//        if (!TextUtils.isEmpty(alertContentTxt)) {
//            tvAlertContent.setText(alertContentTxt);
//        }
            tv_ddh = (TextView) findViewById(R.id.tv_ddh);
            tv_ssct = (TextView) findViewById(R.id.tv_ssct);
            tv_yhm = (TextView) findViewById(R.id.tv_yhm);
            tv_ssck = (TextView) findViewById(R.id.tv_ssck);
            tv_zh = (TextView) findViewById(R.id.tv_zh);
            tv_ssbm = (TextView) findViewById(R.id.tv_ssbm);
            tv_jssj = (TextView) findViewById(R.id.tv_jssj);
            tv_cb = (TextView) findViewById(R.id.tv_cb);
            tv_jshj = (TextView) findViewById(R.id.tv_jshj);
            tv_jslb = (TextView) findViewById(R.id.tv_jslb);
            tv_ysze = (TextView) findViewById(R.id.tv_ysze);
            tv_ssze = (TextView) findViewById(R.id.tv_ssze);
            food_tips = (ImageView) findViewById(R.id.food_tips);
            iv_close = (ImageView) findViewById(R.id.iv_close);
            rv_goods_list = (RecyclerView) findViewById(R.id.rv_goods_list);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            food_tips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.layout_tip_text, null);
                    new EasyDialog(getActivity())
                            // .setLayoutResourceId(R.layout.layout_tip_content_horizontal)//layout resource id
                            .setLayout(view)
                            .setBackgroundColor(getActivity().getResources().getColor(R.color.background_color_black))
                            // .setLocation(new location[])//point in screen
                            .setLocationByAttachedView(food_tips)
                            .setGravity(EasyDialog.GRAVITY_BOTTOM)
                            .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                            .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                            .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                            .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                            .setTouchOutsideDismiss(true)
                            .setMatchParent(false)
                            .setMarginLeftAndRight(320, 320)
                            .setOutsideColor(getActivity().getResources().getColor(R.color.outside_color_trans))
                            .show();
                }
            });

            stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
            stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
            if (!TextUtils.isEmpty(leftNavTxt)) {
                stvLeftBt.setText(leftNavTxt);
            }
            stvLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!needHandleDismiss) {
                        dismiss();
                    }
                    if (mLeftNavClickListener != null) {
                        mLeftNavClickListener.onClick(OrderAlertDialogFragment.this);
                    }

                    if (DeviceManager.INSTANCE.getDeviceInterface().isSupportPrint()) {
                        printOrder(foodConsumeDetailResponse);
                    } else {
                        AppToast.toastMsg("该设备不支持打印小票");
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
                        mRightNavClickListener.onClick(OrderAlertDialogFragment.this);
                    }

                    EventBus.getDefault().post(new RefreshConsumerRefundModeEvent(1));
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
            rv_goods_list.setHasFixedSize(true);
            rv_goods_list.setNestedScrollingEnabled(false);
            rv_goods_list.setLayoutManager(linearLayoutManager);
            foodOrderDetailAdapter = new FoodOrderDetailAdapter(getContext());
            rv_goods_list.setAdapter(foodOrderDetailAdapter);
            rv_goods_list.post(() -> {
                ViewGroup.LayoutParams params = rv_goods_list.getLayoutParams();
                params.height = rv_goods_list.computeVerticalScrollRange();
                rv_goods_list.setLayoutParams(params);
            });
            tv_ddh.setText(foodConsumeDetailResponse.getOrder() == null|| TextUtils.isEmpty(foodConsumeDetailResponse.getOrder().getBillNo()) ? foodConsumeDetailResponse.getOrder().getOrderNo() : foodConsumeDetailResponse.getOrder().getBillNo());
            tv_ssct.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getRestaurantName()) ? "--" :foodConsumeDetailResponse.getRestaurantName());
            tv_yhm.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getCustomerName()) ? "--" :foodConsumeDetailResponse.getCustomerName());
            tv_ssck.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getDeviceName()) ? "--" :foodConsumeDetailResponse.getDeviceName());
            tv_zh.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getCustomerAccount()) ? "--" :foodConsumeDetailResponse.getCustomerAccount());
            tv_ssbm.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getOrgName()) ? "--" :foodConsumeDetailResponse.getOrgName());
            tv_jssj.setText(TextUtils.isEmpty(foodConsumeDetailResponse.getSettleTime()) ? "--" :foodConsumeDetailResponse.getSettleTime());
            String canBie = "";
            if (foodConsumeDetailResponse.getOrder().getFeeType() == 1) {
                canBie = "早餐";
            } else if (foodConsumeDetailResponse.getOrder().getFeeType() == 2) {
                canBie = "午餐";
            } else if (foodConsumeDetailResponse.getOrder().getFeeType() == 3) {
                canBie = "晚餐";
            } else if (foodConsumeDetailResponse.getOrder().getFeeType() == 4) {
                canBie = "夜餐";
            } else {
                canBie = "未知";
            }
            tv_cb.setText(canBie);
//            foodConsumeDetailResponse.getConsumeFoodList().addAll(foodConsumeDetailResponse.getConsumeFoodList());
//            foodConsumeDetailResponse.getConsumeFoodList().addAll(foodConsumeDetailResponse.getConsumeFoodList());
            foodOrderDetailAdapter.setNewInstance(foodConsumeDetailResponse.getConsumeFoodList());
            Log.d(TAG, "limeconvert: " + foodConsumeDetailResponse.getConsumeFoodList().size());
            double totalPrice = 0;
            int totalCount = 0;

            for (int i = 0; i < foodOrderDetailAdapter.getData().size(); i++) {
                ConsumeFoodBean consumeFoodBean = (ConsumeFoodBean) (foodOrderDetailAdapter.getData().get(i));
                Log.d(TAG, "limeconsumeFoodBean: " + JSON.toJSONString(consumeFoodBean));
                if (consumeFoodBean.getFoodMethod() == 2) {
                    totalCount += 1;
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodWeight() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
                } else {
                    totalCount += consumeFoodBean.getFoodCount();
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodCount() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
                }
            }
            tv_jshj.setText(String.valueOf(totalCount));

            if (foodConsumeDetailResponse.getSettleType() == 10) {
                jslb = "人脸";
            } else if (foodConsumeDetailResponse.getSettleType() == 20) {
                jslb = "餐卡";
            } else if (foodConsumeDetailResponse.getSettleType() == 30) {
                jslb = "扫码";
            } else if (foodConsumeDetailResponse.getSettleType() == 40) {
                jslb = "支付宝";
            } else if (foodConsumeDetailResponse.getSettleType() == 50) {
                jslb = "微信";
            } else if (foodConsumeDetailResponse.getSettleType() == 60) {
                jslb = "现金";
            } else if (foodConsumeDetailResponse.getSettleType() == 70) {
                jslb = "云闪付";
            } else {
                jslb = "未知";
            }
            tv_jslb.setText(jslb);
            tv_ysze.setText(PriceUtils.formatPrice(totalPrice));
            Log.d(TAG, "limefoodConsumeDetailResponse: " + JSON.toJSONString(foodConsumeDetailResponse.getOrder()));

            if (foodConsumeDetailResponse.getSettleType() == 40 || foodConsumeDetailResponse.getSettleType() == 50) {
                if (foodConsumeDetailResponse.getOrder().getFee() <= 0.0){
                    tv_ssze.setText(PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getPayFeeYuan().getAmount()));
                }else {
                    tv_ssze.setText(PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getFee()));
                }

            } else if (foodConsumeDetailResponse.getSettleType() == 60 || foodConsumeDetailResponse.getSettleType() == 70) {
                tv_ssze.setText(PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getPayFeeYuan().getAmount()));
            } else {
                tv_ssze.setText(PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getConsumeFeeYuan().getAmount()));
            }








        }catch (Exception e){
            Log.d(TAG, "limeinitViews: " + e.getMessage());
        }
    }



    /**
     * 打印订单
     */
    private void printOrder(FoodConsumeDetailResponse foodConsumeDetailResponse) {
        List<PrinterData> printerDataList = new ArrayList<>();
         String orderNumber = foodConsumeDetailResponse.getOrder() == null|| TextUtils.isEmpty(foodConsumeDetailResponse.getOrder().getBillNo()) ? foodConsumeDetailResponse.getOrder().getOrderNo() : foodConsumeDetailResponse.getOrder().getBillNo();
            printerDataList.add(PrinterData.newDivideLinePrintData());
            StoreInfoHelper storeInfoHelper = mActivity.getWeakRefHolder(StoreInfoHelper.class);
            StoreInfo storeInfo = storeInfoHelper.getStoreInfo();
            if (storeInfo != null) {
                printerDataList.add(PrinterData.newTitlePrintData(storeInfo.getDeviceName() + "\n"));
            } else {
                printerDataList.add(PrinterData.newTitlePrintData("智慧食堂\n"));
            }
            printerDataList.add(PrinterData.newDivideLinePrintData());
            printerDataList.add(PrinterData.newContentPrintData(
                    "订单编号: " + orderNumber + "\n" +
                            "订单时间: " + foodConsumeDetailResponse.getSettleTime() + "\n"
            ));
            printerDataList.add(PrinterData.newDivideLinePrintData());
            StringBuilder goodsInfoBuilder = new StringBuilder();
            List<ConsumeFoodBean> consumeFoodList = foodConsumeDetailResponse.getConsumeFoodList();
            if (consumeFoodList != null) {
                int printLineMaxLength = DeviceManager.INSTANCE.getDeviceInterface().getPrintLineMaxLength();
                int spaceLength = printLineMaxLength - 10 * 2;
                int spacePerLength = spaceLength / 6;
                if (spacePerLength < 1) {
                    spacePerLength = 1;
                }
                int goodsNameSpace = spacePerLength * 4 + 8;
                int goodsSinglePriceSpace = spacePerLength * 4 + 4;
                int goodsCountSpace = spacePerLength * 4 + 4;

                StringBuilder spaceInsertBuilder = new StringBuilder();
                for (int i = -1; i < consumeFoodList.size(); i++) {
                    spaceInsertBuilder.delete(0, spaceInsertBuilder.length());
                    if (i == -1) {
                        //商品名称
                        spaceInsertBuilder.append("商品名称");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsNameSpace - 8);
                        //单价
                        spaceInsertBuilder.append("单价");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace - 4);
                        //数量
                        spaceInsertBuilder.append("数量");
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsCountSpace - 4);
                        //金额
                        spaceInsertBuilder.append("金额\n");
                    } else {
                        ConsumeFoodBean consumeFoodBean = consumeFoodList.get(i);
                        //商品名称
                        String goodsNameStr = consumeFoodBean.getFoodName();
                        if (goodsNameStr != null) {
                            LogHelper.print("---printOrder---goodsNameStr:" + goodsNameStr + " nameLength:" + goodsNameStr.length() + " spaceLength:" + goodsSinglePriceSpace);
                            spaceInsertBuilder.append(goodsNameStr);
                        }
                        Log.i(TAG, "limeprintOrder 444: " + goodsNameSpace);
                        Log.d(TAG, "limeprintOrder 445: " + goodsNameStr.length());
                        appendSpacePlaceHolder(spaceInsertBuilder, goodsNameSpace - goodsNameStr.length()* 2);

                        //单价
                        String goodsSinglePriceStr = PriceUtils.formatPrice(consumeFoodBean.getFoodUnitPriceYuan().getAmount());
                        if (goodsSinglePriceStr != null) {
                            LogHelper.print("---printOrder---goodsSinglePriceStr:" + goodsSinglePriceStr + " singlePriceLength:" + goodsSinglePriceStr.length() + " spaceLength:" + goodsSinglePriceSpace);
                            if (goodsSinglePriceStr.length() < goodsSinglePriceSpace) {
                                spaceInsertBuilder.append(goodsSinglePriceStr);
                                appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace - goodsSinglePriceStr.length());
                            } else {
                                spaceInsertBuilder.append(goodsSinglePriceStr.substring(0, goodsSinglePriceSpace));
                            }
                        } else {
                            appendSpacePlaceHolder(spaceInsertBuilder, goodsSinglePriceSpace);
                        }
                        //数量
                        String goodsCountStr = consumeFoodBean.getFoodCount() + "";
                        if (consumeFoodBean.getFoodMethod() == 2) {
                            goodsCountStr = PriceUtils.formatPrice(consumeFoodBean.getFoodWeight());
                            goodsCountStr = goodsCountStr + "kg";
                        }
                        if (goodsCountStr != null) {
                            LogHelper.print("---printOrder---goodsCountStr:" + goodsCountStr + " countLength:" + goodsCountStr.length() + " spaceLength:" + goodsCountSpace);
                            if (goodsCountStr.length() < goodsCountSpace) {
                                spaceInsertBuilder.append(goodsCountStr);
                                //判断金额位数 尽量展示全
                                String payPrice = PriceUtils.formatPrice(consumeFoodBean.getFoodUnitPriceYuan().getAmount() * consumeFoodBean.getFoodCount());
                                if (consumeFoodBean.isWeightGoods()){
                                    payPrice = PriceUtils.formatPrice(consumeFoodBean.getFoodUnitPriceYuan().getAmount() * consumeFoodBean.getFoodWeight());
                                }
                                int countPlaceHolder = goodsCountSpace - goodsCountStr.length();
                                if (payPrice.length() > 4) {
                                    countPlaceHolder = countPlaceHolder - (payPrice.length() - 4);
                                    if (countPlaceHolder < 0) {
                                        countPlaceHolder = 0;
                                    }
                                }
                                appendSpacePlaceHolder(spaceInsertBuilder, countPlaceHolder);
                            } else {
                                spaceInsertBuilder.append(goodsCountStr.substring(0, goodsCountSpace));
                            }
                        } else {
                            appendSpacePlaceHolder(spaceInsertBuilder, goodsCountSpace);
                        }
                        //金额
                        String payPrice = PriceUtils.formatPrice(consumeFoodBean.getFoodUnitPriceYuan().getAmount() * consumeFoodBean.getFoodCount());
                        if (consumeFoodBean.isWeightGoods()){
                            payPrice = PriceUtils.formatPrice(consumeFoodBean.getFoodUnitPriceYuan().getAmount() * consumeFoodBean.getFoodWeight());
                        }
                        spaceInsertBuilder.append(payPrice).append("\n");
                    }
                    //添加到商品信息builder
                    goodsInfoBuilder.append(spaceInsertBuilder);
                }
                //商品信息
                PrinterData goodsInfoData = PrinterData.newContentPrintData(goodsInfoBuilder.toString());
                printerDataList.add(goodsInfoData);
            }
            printerDataList.add(PrinterData.newDivideLinePrintData());
            String payMoney = PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getPayFeeYuan() == null ? 0 :foodConsumeDetailResponse.getOrder().getPayFeeYuan().getAmount());
        double totalPrice = 0;
        int totalCount = 0;

        for (int i = 0; i < foodOrderDetailAdapter.getData().size(); i++) {
            ConsumeFoodBean consumeFoodBean = (ConsumeFoodBean) (foodOrderDetailAdapter.getData().get(i));
            Log.d(TAG, "limeconsumeFoodBean: " + JSON.toJSONString(consumeFoodBean));
            if (consumeFoodBean.getFoodMethod() == 2) {
                totalCount += 1;
                totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodWeight() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
            } else {
                totalCount += consumeFoodBean.getFoodCount();
                totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodCount() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
            }
        }

        if (foodConsumeDetailResponse.getSettleType() == 40 || foodConsumeDetailResponse.getSettleType() == 50) {
            if (foodConsumeDetailResponse.getOrder().getFee() <= 0.0){
                payMoney = PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getPayFeeYuan().getAmount());
            }else {
                payMoney = PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getFee());
            }

        } else if (foodConsumeDetailResponse.getSettleType() == 60 || foodConsumeDetailResponse.getSettleType() == 70) {
            payMoney = PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getPayFeeYuan().getAmount());
        } else {
            payMoney = PriceUtils.formatPrice(foodConsumeDetailResponse.getOrder().getConsumeFeeYuan().getAmount());
        }

        printerDataList.add(PrinterData.newContentPrintData(
                    "支付方式: " + jslb + "\n" +
                            "总价: " + PriceUtils.formatPrice(totalPrice) + "\n" +
                            "实收: " + payMoney + "\n"
            ));
            printerDataList.add(PrinterData.newCenterContentPrintData("\n\n谢谢惠顾，欢迎下次光临~\n\n\n\n"));
//        }
        DeviceManager.INSTANCE.getDeviceInterface().print(printerDataList, mPrintListener);
    }


    private OnPrintListener mPrintListener = new OnPrintListener() {
        @Override
        public void onPrintSuccess() {
            AppToast.toastMsg("小票打印成功");
        }

        @Override
        public void onPrintError(String message) {
            CommonDialogUtils.showTipsDialog(mActivity, "打印失败: " + message);
        }
    };

    private void appendSpacePlaceHolder(StringBuilder builder, int spaceLength) {
        for (int i = 0; i < spaceLength; i++) {
            builder.append(" ");
        }
    }

}
