package com.stkj.cashier.consumer.ui.presentation;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.adapter.FoodListShowAdapter;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.callback.ConsumerController;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.consumer.callback.OnInputNumberListener;
import com.stkj.cashier.consumer.ui.weight.SimpleInputNumber;
import com.stkj.cashier.home.ui.widget.HomeTitleLayout;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.ChangeConsumerModeEvent;
import com.stkj.cashier.pay.model.FindViewResumeEvent;
import com.stkj.cashier.pay.model.GoodsClearAllEvent;
import com.stkj.cashier.pay.model.UpdateBlanceEvent;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.CircleProgressBar;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 食堂收银客户展示页
 */
public class ConsumerPresentation extends Presentation implements ConsumerController {

    public final static String TAG = "ConsumerPresentation";
    //    private LinearLayout llOrderList;
//    private RecyclerView rvGoodsList;
//    private LinearLayout llFastPayPresentation;
//    private TextView tvGoodsCount;
//    private TextView tvGoodsPrice;
//    private ShapeFrameLayout sflOrderList;
//    private CommonRecyclerAdapter mOrderAdapter;
    private ShapeFrameLayout sflConsumerContent;
    private HomeTitleLayout htlConsumer;

    private ConsumerListener consumerListener;
    private OnConsumerConfirmListener facePassConfirmListener;
    private boolean isSetPayOrderInfo;
    private LinearLayout llFaceConfirm;
    private ShapeTextView stvFaceLeftBt;
    private ShapeTextView stvFaceRightBt;
    private ShapeTextView stvPayPrice;
    private ShapeTextView stv_pay_price_balance;
    private LinearLayout llTakeMealWay;
    private TextView tv_show_total_count;
    private TextView tv_show_yingshou;
    private TextView tv_show_shishou;
    private ShapeTextView stvTakeMealByCode;
    private ShapeTextView stvTakeMealByPhone;
    private SimpleInputNumber sinNumber;
    private ShapeFrameLayout sflInputNumber;
    private CircleProgressBar pbConsumer;
    private ShapeTextView stvCancelPay;
    private RecyclerView rv_goods_inventory_list;
    private LinearLayout ll_inventory_list_empty;
    private Context context;
    private FoodListShowAdapter foodListShowAdapter;
    private ShapeFrameLayout sfl_goods_show_left;
    private int currentConsumerMode;

    public ConsumerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.context = context;
    }

    public ConsumerPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        EventBusUtils.registerEventBus(this);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            //窗口标记属性
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            window.setAttributes(attributes);
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(AppManager.INSTANCE.getMainActivity());
        currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();


            int consumeLayRes = DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes();
            if (consumeLayRes == 1) {
                setContentView(R.layout.presentation_consumer_s1);
                Log.d(TAG, "limeconsumeLayRes: " + 127);
            } else if (consumeLayRes == 2) {
                setContentView(R.layout.presentation_consumer_s2);
                Log.d(TAG, "limeconsumeLayRes: " + 130);
            } else {
                if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE) {
                    setContentView(R.layout.presentation_consumer_s1_goods);
                }else {
                    setContentView(R.layout.presentation_consumer);
                }
                Log.d(TAG, "limeconsumeLayRes: " + 133);
            }
        findViews();
        LogHelper.print("-Consumer--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }



    private void findViews() {
        if (DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes() != 2) {

            sfl_goods_show_left = findViewById(R.id.sfl_goods_show_left);
            tv_show_total_count = findViewById(R.id.tv_show_total_count);
            tv_show_yingshou = findViewById(R.id.tv_show_yingshou);
            tv_show_shishou = findViewById(R.id.tv_show_shishou);

            rv_goods_inventory_list = findViewById(R.id.rv_goods_inventory_list);
            rv_goods_inventory_list.setLayoutManager(new LinearLayoutManager(context));
            foodListShowAdapter = new FoodListShowAdapter(context);
            rv_goods_inventory_list.setAdapter(foodListShowAdapter);
            rv_goods_inventory_list.setItemAnimator(null);
            rv_goods_inventory_list.setItemViewCacheSize(12);
            rv_goods_inventory_list.setHasFixedSize(true);

            ll_inventory_list_empty = findViewById(R.id.ll_inventory_list_empty);


        stvCancelPay = (ShapeTextView) findViewById(R.id.stv_cancel_pay);
        pbConsumer = (CircleProgressBar) findViewById(R.id.pb_consumer);
        sflInputNumber = (ShapeFrameLayout) findViewById(R.id.sfl_input_number);
        sinNumber = (SimpleInputNumber) findViewById(R.id.sin_number);
        stvPayPrice = (ShapeTextView) findViewById(R.id.stv_pay_price);
        stv_pay_price_balance = (ShapeTextView) findViewById(R.id.stv_pay_price_balance);
        htlConsumer = (HomeTitleLayout) findViewById(R.id.htl_consumer);
        sflConsumerContent = (ShapeFrameLayout) findViewById(R.id.sfl_consumer_content);
        llFaceConfirm = (LinearLayout) findViewById(R.id.ll_face_confirm);
        stvFaceLeftBt = (ShapeTextView) findViewById(R.id.stv_face_left_bt);
        stvFaceRightBt = (ShapeTextView) findViewById(R.id.stv_face_right_bt);
         if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE){
                sfl_goods_show_left.setVisibility(View.VISIBLE);
                sflConsumerContent.setBackground(getResources().getDrawable(R.mipmap.goods_face_bg));
                stvPayPrice.setSolidColor(Color.parseColor("#00000000"));
                //sflConsumerContent.setRadius(getResources().getDimensionPixelSize(com.stkj.common.R.dimen.dp_5));
            }else {
             sfl_goods_show_left.setVisibility(View.GONE);
             sflConsumerContent.setBackground(null);
             stvPayPrice.setSolidColor(Color.parseColor("#e9f3ff"));
                //sflConsumerContent.setRadius(getResources().getDimensionPixelSize(com.stkj.common.R.dimen.dp_0));
            }


            rv_goods_inventory_list.setVisibility(View.GONE);
            ll_inventory_list_empty.setVisibility(View.VISIBLE);

        }


        if (consumerListener != null) {
            consumerListener.onCreateTitleLayout(htlConsumer);
        }
//        sflOrderList = (ShapeFrameLayout) findViewById(R.id.sfl_order_list);
//        rvGoodsList = (RecyclerView) findViewById(R.id.rv_goods_list);
//        llFastPayPresentation = (LinearLayout) findViewById(R.id.ll_fast_pay_presentation);
//        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
//        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
//        mOrderAdapter = new CommonRecyclerAdapter(false);
//        rvGoodsList.setAdapter(mOrderAdapter);
        llTakeMealWay = (LinearLayout) findViewById(R.id.ll_take_meal_way);
        stvTakeMealByCode = (ShapeTextView) findViewById(R.id.stv_take_meal_by_code);
        stvTakeMealByCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(true);
                }
                sflInputNumber.setVisibility(View.VISIBLE);
                sinNumber.setInputNumberCount(4);
            }
        });
        stvTakeMealByPhone = (ShapeTextView) findViewById(R.id.stv_take_meal_by_phone);
        stvTakeMealByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(true);
                }
                sflInputNumber.setVisibility(View.VISIBLE);
                sinNumber.setInputNumberCount(11);
            }
        });
        sinNumber.setInputNumberListener(new OnInputNumberListener() {
            @Override
            public void onConfirmNumber(String number) {
                sflInputNumber.setVisibility(View.GONE);
                if (facePassConfirmListener != null) {
                    int inputNumberCount = sinNumber.getInputNumberCount();
                    if (inputNumberCount == 4) {
                        facePassConfirmListener.onConfirmTakeMealCode(number);
                    } else if (inputNumberCount == 11) {
                        facePassConfirmListener.onConfirmPhone(number);
                    }
                }
            }

            @Override
            public void onClickBack() {
                if (facePassConfirmListener != null) {
                    facePassConfirmListener.onShowSimpleInputNumber(false);
                }
                sflInputNumber.setVisibility(View.GONE);
            }

            @Override
            public void onConfirmError(boolean hasInputNumber) {
                int inputNumberCount = sinNumber.getInputNumberCount();
                if (inputNumberCount == 4) {
                    speakTTSVoice(hasInputNumber ? "请输入完整的取餐码" : "请输入取餐码");
                } else if (inputNumberCount == 11) {
                    speakTTSVoice(hasInputNumber ? "请输入完整的手机号" : "请输入手机号");
                }
            }
        });
    }

    public void refreshTotalPrice() {

        double totalPrice = 0;
        int totalCount = 0;

        for (int i = 0; i < foodListShowAdapter.getData().size(); i++) {
            FoodInfoTable foodInfoTable = ((FoodInfoTable)(foodListShowAdapter.getData().get(i)));
            if (foodInfoTable.isWeightGoods()) {
                totalCount += 1;
                totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getWeightGoodsCountWithDouble() * foodInfoTable.getUnitPriceMoney_amount()));
            } else {
                totalCount += foodInfoTable.getStandardGoodsCountWithInt();
                totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getStandardGoodsCountWithInt() * foodInfoTable.getUnitPriceMoney_amount()));
            }
        }

        tv_show_total_count.setText("共 " + totalCount +" 件商品");
        tv_show_yingshou.setText("应收：￥ " + PriceUtils.formatPrice(totalPrice));
        tv_show_shishou.setText("￥ " + PriceUtils.formatPrice(totalPrice));

        if (totalCount > 0){
            setConsumerAuthTips("合计金额");
            stvPayPrice.setVisibility(View.VISIBLE);
            stvPayPrice.setText("¥ " + PriceUtils.formatPrice(totalPrice));

        }else {
            //fpcFace.resetFaceInfoLayout();
            //setConsumerAuthTips("欢迎光临!");
            if (stv_pay_price_balance != null){
                stv_pay_price_balance.setVisibility(View.GONE);
            }
            Log.d(TAG, "limeresetFaceInfoLayout: " + 291);
            stvPayPrice.setVisibility(View.GONE);
        }

    }

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
        this.facePassConfirmListener = facePassConfirmListener;
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    @Override
    public void setFacePreview(boolean preview) {

    }

    private boolean isConsumerAuthTips;

    @Override
    public void setConsumerAuthTips(String tips) {

    }

    @Override
    public boolean isConsumerAuthTips() {
        return isConsumerAuthTips;
    }

    @Override
    public void setConsumerTips(String tips) {
        setConsumerTips(tips, 0);
    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {

    }

    @Override
    public void setConsumerConfirmFaceInfo(FacePassPeopleInfo facePassPeopleInfo, boolean needConfirm, int consumerType) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);

    }

    @Override
    public void setConsumerConfirmCardInfo(String cardNumber, boolean needConfirm) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);

    }

    @Override
    public void setConsumerConfirmScanInfo(String scanData, boolean needConfirm) {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
        if (needConfirm) {
            llFaceConfirm.setVisibility(View.VISIBLE);
            stvFaceLeftBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onConfirmScanData(scanData);
                    }
                    llFaceConfirm.setVisibility(View.GONE);
                }
            });
            stvFaceRightBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onCancelScanData(scanData);
                    }
                }
            });
        } else {
            if (facePassConfirmListener != null) {
                facePassConfirmListener.onConfirmScanData(scanData);
            }
        }
    }

    @Override
    public void setConsumerTakeMealWay() {
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNormalConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        sflInputNumber.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(View.GONE);
    }

    @Override
    public void setPayConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        sflInputNumber.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(isSetPayOrderInfo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPayPrice(String payPrice, boolean showCancelPay) {
        setCanCancelPay(showCancelPay);
        pbConsumer.setVisibility(View.GONE);
        stvPayPrice.setVisibility(View.VISIBLE);
        stvPayPrice.setText("¥ " + payPrice);
    }

    @Override
    public void setCanCancelPay(boolean showCancelPay) {
        if (showCancelPay) {
            stvCancelPay.setVisibility(View.VISIBLE);
            stvCancelPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (facePassConfirmListener != null) {
                        facePassConfirmListener.onConsumerCancelPay();
                    }
                }
            });
        } else {
            stvCancelPay.setVisibility(View.GONE);
        }
    }

    @Override
    public void resetFaceConsumerLayout() {

    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
        if (consumerListener != null) {
            consumerListener.onConsumerDismiss();
        }
        LogHelper.print("ConsumerPresentation--onDisplayRemoved");
    }

    @Override
    public void onDisplayChanged() {
        super.onDisplayChanged();
        if (consumerListener != null) {
            consumerListener.onConsumerChanged();
        }
        LogHelper.print("ConsumerPresentation--onDisplayChanged");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeConsumerModeEvent(ChangeConsumerModeEvent eventBus) {
        currentConsumerMode = eventBus.getMode();
        if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE){
            sfl_goods_show_left.setVisibility(View.VISIBLE);
        }else if (currentConsumerMode== PayConstants.CONSUMER_GOODS_UPDATE_MODE){
            foodListShowAdapter.setNewInstance(eventBus.getFoods());
            if (eventBus.getFoods().size() > 0){
                rv_goods_inventory_list.setVisibility(View.VISIBLE);
                ll_inventory_list_empty.setVisibility(View.GONE);
            }else {
                rv_goods_inventory_list.setVisibility(View.GONE);
                ll_inventory_list_empty.setVisibility(View.VISIBLE);
            }
            refreshTotalPrice();
        }else {
            sfl_goods_show_left.setVisibility(View.GONE);
        }
        //findViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodsClearAllEvent(GoodsClearAllEvent eventBus) {

        if (foodListShowAdapter != null){
            foodListShowAdapter.setNewInstance(null);
            rv_goods_inventory_list.setVisibility(View.GONE);
            ll_inventory_list_empty.setVisibility(View.VISIBLE);
            refreshTotalPrice();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateBlanceEvent(UpdateBlanceEvent eventBus) {
        ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(AppManager.INSTANCE.getMainActivity());
        currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
        if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE){
           stv_pay_price_balance.setVisibility(View.VISIBLE);
           stv_pay_price_balance.setText("账户余额：¥ " + PriceUtils.formatPrice(eventBus.getBlance()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void findViewsResume(GoodsClearAllEvent eventBus) {

        if (foodListShowAdapter != null){
            foodListShowAdapter.setNewInstance(null);
            rv_goods_inventory_list.setVisibility(View.GONE);
            ll_inventory_list_empty.setVisibility(View.VISIBLE);
            refreshTotalPrice();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindViewResumeEvent(FindViewResumeEvent eventBus) {
        findViews();
    }



    @Override
    public void dismiss() {
        super.dismiss();
        EventBusUtils.unRegisterEventBus(this);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            //非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Throwable e) {
            e.printStackTrace();
            AppToast.toastMsg("副屏初始化失败");
        }
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words) {
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mainActivity;
            baseActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words);
        }
    }
}
