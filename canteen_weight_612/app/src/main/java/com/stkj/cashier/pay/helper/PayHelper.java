package com.stkj.cashier.pay.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.common.util.Base64Utils;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.DeviceFoodConsumeParam;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 支付帮助类
 */
public class PayHelper extends ActivityWeakRefHolder {

    public final static String TAG = "PayHelper";
    public static final String ERROR_CONNECT = "-2";

    /**
     * 正在支付
     */
    private boolean isPaying;
    private OnPayListener onPayListener;

    public PayHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public boolean isPaying() {
        return isPaying;
    }

    /**
     * 去支付
     */
    @SuppressLint("NewApi")
    public void goToPay(int payType, int deductionType, String money, String cardNumber) {
        Log.d(TAG, "limegoToPaypayType : " + payType);
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (isPaying) {
            AppToast.toastMsg("正在支付中,请稍等");
            return;
        }

        ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(AppManager.INSTANCE.getMainActivity());
        int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
        if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE){

            String createOrderNumber = PayConstants.createOrderNumber();
            MainApplication.createOrderNumber = createOrderNumber;
            TreeMap<String, String> costBalanceParams = ParamsUtils.newSortParamsMapWithMode("foodConsume");

            boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();


            DeviceFoodConsumeParam deviceFoodConsumeParam = new DeviceFoodConsumeParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                    cardNumber, deductionType, Double.parseDouble(PriceUtils.formatPrice(money)), payType, createOrderNumber, Integer.parseInt(switchTongLianPay ? "1" : "0"),
                    MainApplication.isQuick, MainApplication.consumeFoodInfoList);

            costBalanceParams.put("deviceFoodConsumeParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodConsumeParam).getBytes()));
            Log.d(TAG, "limegoToPay deviceFoodConsumeParam: " + JSON.toJSONString(deviceFoodConsumeParam));
            
            if (onPayListener != null) {
                onPayListener.onStartPay(costBalanceParams);
            }
            isPaying = true;
            RetrofitManager.INSTANCE.getDefaultRetrofit()
                    .create(PayService.class)
                    .goToPayFoods(ParamsUtils.signSortParamsMap(costBalanceParams))
                    .compose(RxTransformerUtils.mainSchedulers())
                    .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                    .subscribe(new DefaultObserver<BaseNetResponse<ModifyBalanceResult>>() {
                        @Override
                        protected void onSuccess(BaseNetResponse<ModifyBalanceResult> baseNetResponse) {
                            ModifyBalanceResult modifyBalanceResult = baseNetResponse.getData();
                            if (baseNetResponse.isSuccess() && payType == PayConstants.PAY_TYPE_CASH) {
                                isPaying = false;
                                if (onPayListener != null) {
                                    Log.d(TAG, "limegoToPay: " + 113);
                                    onPayListener.onPaySuccess(costBalanceParams, modifyBalanceResult);
                                }

                                return;
                            }

                            Log.d(TAG, "limegoToPay: " + 119);
                            
                            if (modifyBalanceResult != null) {
                                if (baseNetResponse.isSuccess()) {
                                    isPaying = false;
                                    if (onPayListener != null) {
                                        onPayListener.onPaySuccess(costBalanceParams, modifyBalanceResult);
                                    }
                                } else if (TextUtils.equals(PayConstants.PAY_PROCESSING_STATUS, baseNetResponse.getCode())) {
                                    requestPayStatus(costBalanceParams, modifyBalanceResult);
                                } else {
                                    isPaying = false;
                                    if (onPayListener != null) {
                                        onPayListener.onPayError(baseNetResponse.getCode(), costBalanceParams, modifyBalanceResult, TextUtils.isEmpty(baseNetResponse.getMsg())? baseNetResponse.getMessage():baseNetResponse.getMsg());
                                    }
                                }
                            } else {
                                isPaying = false;
                                if (onPayListener != null) {
                                    onPayListener.onPayError(baseNetResponse.getCode(), costBalanceParams, null, TextUtils.isEmpty(baseNetResponse.getMsg())? baseNetResponse.getMessage():baseNetResponse.getMsg());
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            isPaying = false;
                            if (onPayListener != null) {
                                if (e instanceof java.net.ConnectException) {
                                    onPayListener.onPayError(ERROR_CONNECT, costBalanceParams, null, e.getMessage());
                                } else {
                                    onPayListener.onPayError("-1", costBalanceParams, null, e.getMessage());
                                }
                            }
                        }
                    });
            return;
        }


        String createOrderNumber = PayConstants.createOrderNumber();
        MainApplication.createOrderNumber = createOrderNumber;
        TreeMap<String, String> modifyBalanceParams = ParamsUtils.newSortParamsMapWithMode("ModifyBalance");
        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
        modifyBalanceParams.put("payType", switchTongLianPay ? "1" : "");
        modifyBalanceParams.put("cardNumber", cardNumber);
        modifyBalanceParams.put("consumption_type", String.valueOf(payType));
        modifyBalanceParams.put("deduction_Type", String.valueOf(deductionType));
        modifyBalanceParams.put("online_Order_number", createOrderNumber);
        modifyBalanceParams.put("money", money);
        if (onPayListener != null) {
            onPayListener.onStartPay(modifyBalanceParams);
        }
        isPaying = true;
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .goToPay(ParamsUtils.signSortParamsMap(modifyBalanceParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<ModifyBalanceResult>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<ModifyBalanceResult> baseNetResponse) {
                        ModifyBalanceResult modifyBalanceResult = baseNetResponse.getData();
                        if (modifyBalanceResult != null) {
                            if (baseNetResponse.isSuccess()) {
                                isPaying = false;
                                if (onPayListener != null) {
                                    onPayListener.onPaySuccess(modifyBalanceParams, modifyBalanceResult);
                                }
                            } else if (TextUtils.equals(PayConstants.PAY_PROCESSING_STATUS, baseNetResponse.getCode())) {
                                requestPayStatus(modifyBalanceParams, modifyBalanceResult);
                            } else {
                                isPaying = false;
                                if (onPayListener != null) {
                                    onPayListener.onPayError(baseNetResponse.getCode(), modifyBalanceParams, modifyBalanceResult, TextUtils.isEmpty(baseNetResponse.getMsg())? baseNetResponse.getMessage():baseNetResponse.getMsg());
                                }
                            }
                        } else {
                            isPaying = false;
                            if (onPayListener != null) {
                                onPayListener.onPayError(baseNetResponse.getCode(), modifyBalanceParams, null, TextUtils.isEmpty(baseNetResponse.getMsg())? baseNetResponse.getMessage():baseNetResponse.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isPaying = false;
                        if (onPayListener != null) {
                            if (e instanceof java.net.ConnectException) {
                                onPayListener.onPayError(ERROR_CONNECT, modifyBalanceParams, null, e.getMessage());
                            } else {
                                onPayListener.onPayError("-1", modifyBalanceParams, null, e.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * 请求支付状态
     */
    private void requestPayStatus(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<Long>() {
                    @Override
                    protected void onSuccess(Long aLong) {
                        TreeMap<String, String> payStatusParams = ParamsUtils.newSortParamsMapWithMode("PayStatus");
                        payStatusParams.put("payNo", modifyBalanceResult.getPayNo());
                        //通联支付
                        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
                        payStatusParams.put("payType", switchTongLianPay ? "1" : "");
                        RetrofitManager.INSTANCE.getDefaultRetrofit()
                                .create(PayService.class)
                                .getPayStatus(ParamsUtils.signSortParamsMap(payStatusParams))
                                .compose(RxTransformerUtils.mainSchedulers())
                                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                                .subscribe(new DefaultObserver<BaseNetResponse<ModifyBalanceResult>>() {
                                    @Override
                                    protected void onSuccess(BaseNetResponse<ModifyBalanceResult> baseNetResponse) {
                                        ModifyBalanceResult modifyBalanceResult = baseNetResponse.getData();
                                        if (modifyBalanceResult != null) {
                                            if (baseNetResponse.isSuccess()) {
                                                isPaying = false;
                                                if (onPayListener != null) {
                                                    onPayListener.onPaySuccess(payRequest, modifyBalanceResult);
                                                }
                                            } else if (TextUtils.equals(PayConstants.PAY_PROCESSING_STATUS, baseNetResponse.getCode())) {
                                                requestPayStatus(payRequest, modifyBalanceResult);
                                            } else {
                                                isPaying = false;
                                                if (onPayListener != null) {
                                                    onPayListener.onPayError(baseNetResponse.getCode(), payRequest, modifyBalanceResult, baseNetResponse.getMessage());
                                                }
                                            }
                                        } else {
                                            isPaying = false;
                                            if (onPayListener != null) {
                                                onPayListener.onPayError(baseNetResponse.getCode(), payRequest, null, baseNetResponse.getMessage());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        isPaying = false;
                                        if (onPayListener != null) {
                                            if (e instanceof java.net.ConnectException) {
                                                onPayListener.onPayError(ERROR_CONNECT, payRequest, modifyBalanceResult, e.getMessage());
                                            } else {
                                                onPayListener.onPayError("-1", payRequest, modifyBalanceResult, e.getMessage());
                                            }
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void onClear() {
        onPayListener = null;
    }
}
