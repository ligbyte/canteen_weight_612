package com.stkj.cashier.machine.fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.consumer.callback.ConsumerController;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.BindFragmentSwitchEvent;
import com.stkj.cashier.pay.model.RefreshBindModeEvent;
import com.stkj.cashier.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.cashier.pay.ui.fragment.AddGoodsFragment;
import com.stkj.cashier.pay.ui.fragment.BasePayHelperFragment;
import com.stkj.cashier.pay.ui.fragment.GoodsConsumerFragment;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.core.AppManager;
import com.stkj.common.ui.widget.common.CircleProgressBar;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 称重机页面——首页
 */
public class TabWeightHomeFragment extends BasePayHelperFragment implements OnConsumerModeListener , ConsumerController {

    public final static String TAG = "TabWeightHomeFragment";
    //    private LinearLayout llOrderList;
//    private RecyclerView rvGoodsList;
//    private LinearLayout llFastPayPresentation;
//    private TextView tvGoodsCount;
//    private TextView tvGoodsPrice;
//    private ShapeFrameLayout sflOrderList;
//    private CommonRecyclerAdapter mOrderAdapter;

    private ConsumerListener consumerListener;
    private OnConsumerConfirmListener facePassConfirmListener;
    private boolean isSetPayOrderInfo;
    private ShapeTextView stvPayPrice;
    private ShapeTextView stv_pay_price_balance;
    private LinearLayout llTakeMealWay;
    private CircleProgressBar pbConsumer;
    private ShapeTextView stvCancelPay;
    private Context context;
    private int currentConsumerMode;
    private boolean isConsumerAuthTips;
    private Handler handler = new Handler() {
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_bind;
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);
        ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(AppManager.INSTANCE.getMainActivity());
        currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
        //findViews();

    }

    public void findViews() {
        if (DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes() != 2) {



            stvCancelPay = (ShapeTextView) findViewById(R.id.stv_cancel_pay);
            pbConsumer = (CircleProgressBar) findViewById(R.id.pb_consumer);
            stvPayPrice = (ShapeTextView) findViewById(R.id.stv_pay_price);
            stv_pay_price_balance = (ShapeTextView) findViewById(R.id.stv_pay_price_balance);

        }

//        sflOrderList = (ShapeFrameLayout) findViewById(R.id.sfl_order_list);
//        rvGoodsList = (RecyclerView) findViewById(R.id.rv_goods_list);
//        llFastPayPresentation = (LinearLayout) findViewById(R.id.ll_fast_pay_presentation);
//        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
//        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
//        mOrderAdapter = new CommonRecyclerAdapter(false);
//        rvGoodsList.setAdapter(mOrderAdapter);
        llTakeMealWay = (LinearLayout) findViewById(R.id.ll_take_meal_way);



    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerGoodsModeEvent(RefreshConsumerGoodsModeEvent eventBus) {
        if (eventBus.getPageMode() == 1){
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AddGoodsFragment(), R.id.fl_pay_second_content);
        } else {
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new GoodsConsumerFragment(), R.id.fl_pay_second_content);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "limerunnable: " + 124);
            EventBus.getDefault().post(new BindFragmentSwitchEvent(0));
            stopAllAuth();
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBindModeEvent(RefreshBindModeEvent eventBus) {
        Log.d(TAG, "limegoToAllAuth: " + 245);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,10 * 1000);
        goToPay("100");
    }

    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

    @Override
    public void setFacePreview(boolean preview) {

    }

    @Override
    public void setConsumerAuthTips(String tips) {

    }

    @Override
    public void setConsumerTips(String tips) {
        setConsumerTips(tips, 0);
    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {

    }

    @Override
    public boolean isConsumerAuthTips() {
        return isConsumerAuthTips;
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

    }

    @Override
    public void setConsumerTakeMealWay() {
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.VISIBLE);
    }

    @Override
    public void resetFaceConsumerLayout() {

    }

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
        this.facePassConfirmListener = facePassConfirmListener;
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    @Override
    public void setNormalConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(View.GONE);
    }

    @Override
    public void setPayConsumeStatus() {
        stvCancelPay.setVisibility(View.GONE);
        pbConsumer.setVisibility(View.GONE);
        llTakeMealWay.setVisibility(View.GONE);
//        sflOrderList.setVisibility(isSetPayOrderInfo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPayPrice(String payPrice, boolean showCancelPay) {
        setCanCancelPay(showCancelPay);
        pbConsumer.setVisibility(View.GONE);
        setConsumerAuthTips("请刷脸、刷卡或扫码");
//        stvPayPrice.setVisibility(View.VISIBLE);
//        stvPayPrice.setText("¥ " + payPrice);
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
}