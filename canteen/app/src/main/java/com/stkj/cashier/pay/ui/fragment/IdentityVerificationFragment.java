package com.stkj.cashier.pay.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomInExit;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.TTSVoiceListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.FaceChooseItemEntity;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.FaceChooseDialogFragment;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.base.utils.UsernameComparator;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.home.helper.CBGCameraHelper;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.FacePassRetryEvent;
import com.stkj.cashier.pay.model.RefreshConsumerAmountModeEvent;
import com.stkj.cashier.pay.model.RefreshConsumerNumberModeEvent;
import com.stkj.cashier.pay.model.RefreshConsumerTakeModeEvent;
import com.stkj.cashier.pay.model.RefreshConsumerWeightModeEvent;
import com.stkj.cashier.pay.model.RefreshGoodsModeEvent;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.helper.FacePassHelper;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.cashier.setting.model.PauseFacePassDetect;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.model.CBGFacePassRecognizeResult;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultDisposeObserver;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.FileUtils;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ReUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 身份校验基类
 */
public abstract class IdentityVerificationFragment extends BaseRecyclerFragment {

    public final static String TAG = "IdentityVerificationFragment";
    private boolean hasStartAuth;
    private NormalListDialog listDialog;

    //是否正在读卡身份校验
    private boolean isRunningICCardAuth;

    //是否正在人脸身份校验
    private boolean isRunningFacePassAuth;
    //是否正在人脸身份校验
    private boolean isRunningQRCodeAuth;

    private long beforeShowTime = 0;

    private FaceChooseDialogFragment faceChooseDialogFragment;

    private int itemPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBusUtils.registerEventBus(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshPayType(RefreshPayType refreshPayType) {
        if (hasStartAuth) {
            if (refreshPayType.isPayTypeFace()) {
                if (!isRunningFacePassAuth) {
                    speakTTSVoice("刷脸支付已开启");
                    goFacePassAuth();
                }
            } else {
                if (isRunningFacePassAuth) {
                    speakTTSVoice("刷脸支付已关闭");
                    stopFacePassAuth();
                }
            }
            if (refreshPayType.isPayTypeCard()) {
                if (!isRunningICCardAuth) {
                    speakTTSVoice("刷卡支付已开启");
                    goICCardAuth();
                }
            } else {
                if (isRunningICCardAuth) {
                    speakTTSVoice("刷卡支付已关闭");
                    stopICCardAuth();
                }
            }
            if (refreshPayType.isPayTypeScan()) {
                if (!isRunningQRCodeAuth) {
                    speakTTSVoice("扫码支付已开启");
                    goQRCodeAuth();
                }
            } else {
                if (isRunningQRCodeAuth) {
                    speakTTSVoice("扫码支付已关闭");
                    stopQRCodeAuth();
                }
            }
            //刷新副屏人脸底部提示
            boolean consumerAuthTips = ConsumerManager.INSTANCE.isConsumerAuthTips();
            if (consumerAuthTips) {
                String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
                ConsumerManager.INSTANCE.setConsumerAuthTips(payTypeVoice);
            }
        }
    }

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words) {
        mActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words);
    }

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words, String voiceId, TTSVoiceListener ttsVoiceListener) {
        mActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words, voiceId, ttsVoiceListener);
    }

    /**
     * start 刷卡认证 start
     */
    protected void goICCardAuth() {
        hasStartAuth = true;
        isRunningICCardAuth = true;
        DeviceManager.INSTANCE.getDeviceInterface().readICCard(mReadICCardListener);
    }

    protected void stopICCardAuth() {
        //是否正在读卡身份校验
        isRunningICCardAuth = false;
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterICCardListener(mReadICCardListener);
    }

    private OnReadICCardListener mReadICCardListener = new OnReadICCardListener() {
        @Override
        public void onReadCardData(String data) {
            if (TextUtils.isEmpty(data)) {
                handleReadICCardError("卡数据为空");
                return;
            }
            LogHelper.print("--IdentityVerificationFragment--onReadCardData " + data);
            //停止所有的识别检测
            stopAllAuth();
            //读卡成功去支付
            processReadCardResult(data);
        }

        @Override
        public void onReadCardError(String message) {
            //失败继续重试
            DeviceManager.INSTANCE.getDeviceInterface().registerICCardListener(this);
            LogHelper.print("--IdentityVerificationFragment--onReadCardError " + message);
            handleReadICCardError(message);
        }
    };

    /**
     * 处理读卡
     */
    private void processReadCardResult(String cardNumber) {
        LogHelper.print("--IdentityVerificationFragment--handleReadCardResult--cardNumber: " + cardNumber);
        FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
        facePassHelper.searchFacePassByCardNumber(cardNumber, new FacePassHelper.OnHandleCardNumberListener() {
            @Override
            public void onHandleLocalCardNumber(String cardNumber, FacePassPeopleInfo facePassPeopleInfo) {
                handleReadICCardSuccess(facePassPeopleInfo);
                LogHelper.print("--IdentityVerificationFragment--handleReadCardResult confirmFaceInfo");
            }

            @Override
            public void onHandleLocalCardNumberError(String cardNumber) {
                handleReadICCardSuccess(cardNumber);
                LogHelper.print("--IdentityVerificationFragment--handleReadCardResult confirmCardInfo");
            }
        });
    }

    /**
     * 读卡失败
     */
    protected abstract void handleReadICCardError(String message);

    /**
     * 读卡成功 （本地包含人脸信息）
     */
    protected abstract void handleReadICCardSuccess(FacePassPeopleInfo facePassPeopleInfo);

    /**
     * 读卡成功 （无人脸信息）
     */
    protected abstract void handleReadICCardSuccess(String cardNumber);

    /**
     * end 刷卡认证 end
     */

    /**
     * start 扫码认证 start
     */
    protected void goQRCodeAuth() {
        if (!Build.MODEL.equals("rk3568_h09")) {
            hasStartAuth = true;
            isRunningQRCodeAuth = true;
            DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mScanQRCodeListener);
        }
    }

    /**
     * 停止扫码识别
     */
    protected void stopQRCodeAuth() {
        //是否正在人脸身份校验
        isRunningQRCodeAuth = false;
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mScanQRCodeListener);
    }

    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (TextUtils.isEmpty(data)) {
                handleScanQRCodeError("扫码数据为空");
                return;
            }
            LogHelper.print("--IdentityVerificationFragment--onScanQrCode data: " + data);
            //停止所有的识别检测
            stopAllAuth();
            ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
            if (consumerModeHelper.getCurrentConsumerMode() ==  PayConstants.CONSUMER_GOODS_MODE) {
                boolean isZfb = ReUtil.isMatch("^(25|26|27|28|29|30)\\d{14,22}$", data);
                boolean isWx = ReUtil.isMatch("^(10|11|12|13|14|15)\\d{16}$", data);
                boolean isYsf = ReUtil.isMatch("^62\\d{14,17}$", data);
                boolean isTongLianRefund = ReUtil.isMatch("^250\\d{15}$", data);
                if (MainApplication.isJuHePay){
                    if (isZfb || isWx || isYsf || isTongLianRefund) {
                        handleScanQRCodeSuccess(data);
                    } else {
                        speakTTSVoice("请使用微信或支付宝扫码");
                        Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(getActivity())).subscribe(new DefaultObserver<Long>() {
                            @Override
                            protected void onSuccess(Long aLong) {
                                goQRCodeAuth();
                            }
                        });

                    }
                }else {

                    if (isZfb || isWx || isYsf || isTongLianRefund) {
                        speakTTSVoice("请使用慧餐宝小程序扫码");
                        Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(getActivity())).subscribe(new DefaultObserver<Long>() {
                            @Override
                            protected void onSuccess(Long aLong) {
                                goToAllAuth();
                            }
                        });
                    } else {
                        handleScanQRCodeSuccess(data);
                    }

                }


            }else {
                handleScanQRCodeSuccess(data);
            }

        }

        @Override
        public void onScanQRCodeError(String message) {
            //失败继续重试
            DeviceManager.INSTANCE.getDeviceInterface().registerScanQRCodeListener(this);
            LogHelper.print("--IdentityVerificationFragment--onScanQRCodeError message: " + message);
            handleScanQRCodeError(message);
        }
    };

    /**
     * 扫码失败
     */
    protected abstract void handleScanQRCodeError(String message);

    /**
     * 扫码成功
     */
    protected abstract void handleScanQRCodeSuccess(String qrcodeResult);

    /**
     * end 扫码认证 end
     */

    /**
     * start 刷脸认证 start
     */
    //人脸识别失败语音提示
    private boolean canSpeakFacePassFail;
    //人脸识别失败语音提示延迟
    private DefaultDisposeObserver<Long> canSpeakFacePassFailObserver;

    protected void goFacePassAuth() {
        Log.d(TAG, "limegoFacePassAuth: " + 301);
        hasStartAuth = true;
        isRunningFacePassAuth = true;
        canSpeakFacePassFail = true;
        if (canSpeakFacePassFailObserver != null) {
            canSpeakFacePassFailObserver.dispose();
            canSpeakFacePassFailObserver = null;
        }
        Log.d(TAG, "limegoToAllAuth: " + 352);
        ConsumerManager.INSTANCE.setFacePreview(true);
        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.setOnDetectFaceListener(new CBGFacePassHandlerHelper.OnDetectFaceListener() {
            @Override
            public void onDetectFaceToken(List<CBGFacePassRecognizeResult> faceTokenList) {
                processFacePassResult(faceTokenList);
            }

            @Override
            public void onNoDetectFaceToken() {
                processFacePassFailRetryDelay(-1);
                LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  onNoDetectFaceToken");
            }
        });
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "limegoToAllAuth: " + 371);
                    Log.d(TAG, "limegoFacePassAuth: " + 327);
                    cbgCameraHelper.prepareFacePassDetect();
                    Log.d(TAG, "limegoFacePassAuth: " + 329);
                    cbgCameraHelper.startFacePassDetect();
                    Log.d(TAG, "limegoFacePassAuth: " + 331);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 停止人脸识别
     */
    protected void stopFacePassAuth() {
        //是否正在人脸身份校验
        isRunningFacePassAuth = false;
        ConsumerManager.INSTANCE.setFacePreview(false);
        CBGCameraHelper cbgCameraHelper = mActivity.getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.stopFacePassDetect();
    }

    /**
     * 处理识别人脸结果
     *
     */
    protected void processFacePassResult(List<CBGFacePassRecognizeResult> faceTokenList) {

        if (faceTokenList != null && !faceTokenList.isEmpty()) {

            if (faceTokenList.size() == 1) {
                FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
                CBGFacePassRecognizeResult facePassRecognizeResult = faceTokenList.get(0);
                if (facePassRecognizeResult.isFacePassSuccess()) {
                    facePassHelper.searchFacePassByFaceToken(facePassRecognizeResult.getFaceToken(), new FacePassHelper.OnHandleFaceTokenListener() {
                        @Override
                        public void onHandleLocalFace(String faceToken, FacePassPeopleInfo facePassPeopleInfo) {
                            //停止所有的识别检测
                            stopAllAuth();
                            handleFacePassSuccess(facePassPeopleInfo);
                            LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  confirmFaceInfo");
                        }

                        @Override
                        public void onHandleLocalFaceError(String faceToken) {
                            processFacePassFailRetryDelay(-1);
                            LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  onHandleLocalFaceError");
                        }
                    });
                } else {
                    processFacePassFailRetryDelay(facePassRecognizeResult.getRecognitionState());
                }
            } else {

                CBGFacePassRecognizeResult facePassRecognizeResult = faceTokenList.get(0);
                if (facePassRecognizeResult.isFacePassSuccess()) {
                FacePassHelper facePassHelperDuo = mActivity.getWeakRefHolder(FacePassHelper.class);
                ArrayList<FaceChooseItemEntity> mMenuItems = new ArrayList<>();
                for (int i = 0; i < faceTokenList.size(); i++) {

                    facePassHelperDuo.searchFacePassByFaceToken(faceTokenList.get(i).getFaceToken(), new FacePassHelper.OnHandleFaceTokenListener() {
                        @Override
                        public void onHandleLocalFace(String faceToken, FacePassPeopleInfo facePassPeopleInfo) {
                            mMenuItems.add(new FaceChooseItemEntity(facePassPeopleInfo.getFull_Name(), facePassPeopleInfo.getPhone(), facePassPeopleInfo.getImgData(), facePassPeopleInfo.getCBGFaceToken(), false));
                            if (mMenuItems.size() == faceTokenList.size()) {
                                if (listDialog == null || !listDialog.isShowing()) {

                                    stopAllAuth();
                                    mMenuItems.sort(new UsernameComparator());
                                    NormalListDialog(faceTokenList, mMenuItems);

                                }

                            }
                        }

                        @Override
                        public void onHandleLocalFaceError(String faceToken) {
                            processFacePassFailRetryDelay(-1);
                            LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  onHandleLocalFaceError");
                        }
                    });

                }
                } else {
                    processFacePassFailRetryDelay(facePassRecognizeResult.getRecognitionState());
                }

        }
        }
    }


    /**
     * 处理单个识别人脸结果
     *
     */
    protected void processFacePassResultSingle(CBGFacePassRecognizeResult facePassRecognizeResult) {
        FacePassHelper facePassHelper = mActivity.getWeakRefHolder(FacePassHelper.class);
            if (facePassRecognizeResult.isFacePassSuccess()) {
                facePassHelper.searchFacePassByFaceToken(facePassRecognizeResult.getFaceToken(), new FacePassHelper.OnHandleFaceTokenListener() {
                    @Override
                    public void onHandleLocalFace(String faceToken, FacePassPeopleInfo facePassPeopleInfo) {
                        //停止所有的识别检测
                        stopAllAuth();
                        handleFacePassSuccess(facePassPeopleInfo);
                        LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  confirmFaceInfo");
                    }

                    @Override
                    public void onHandleLocalFaceError(String faceToken) {
                        processFacePassFailRetryDelay(-1);
                        LogHelper.print("--IdentityVerificationFragment--handleFacePassResult  onHandleLocalFaceError");
                    }
                });
            } else {
                processFacePassFailRetryDelay(facePassRecognizeResult.getRecognitionState());
            }
    }


    private void NormalListDialog(List<CBGFacePassRecognizeResult> faceTokenList,ArrayList<FaceChooseItemEntity> listData) {
        if (TakeMealConsumerFragment.rvTakeMealListVisibility){
            return;
        }


        MainApplication.isUnLockFrame = true;
        if (System.currentTimeMillis() - beforeShowTime < 1000){
            return;
        }
        beforeShowTime = System.currentTimeMillis();


       speakTTSVoice("检测到相似人脸，请选择");
        ConsumerManager.INSTANCE.setConsumerTips("检测到相似人脸");
        if (faceChooseDialogFragment != null) {
            faceChooseDialogFragment.dismiss();
        }

        faceChooseDialogFragment = FaceChooseDialogFragment.build()
                .setTitle("系统检测到有相似人脸，请选择")
                .setSelectListData(listData)
                .setOnSelectListener(new FaceChooseDialogFragment.OnSelectListener() {
                    @Override
                    public void onConfirmSelectItem(FaceChooseItemEntity faceChooseItemEntity, int position) {
                        //changeConsumerMode(commonSelectItem.getTypeInt());
                        for (int i = 0; i < faceTokenList.size(); i++) {
                            if (faceTokenList.get(i).getFaceToken().equals(listData.get(position).getFaceToken())){
                                itemPosition = i;
                            }
                        }
                        MainApplication.isNeedCache = true;
                        processFacePassResultSingle(faceTokenList.get(itemPosition));
                    }

                    @Override
                    public void onDismiss() {
                        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
                        if (consumerModeHelper.getCurrentConsumerMode() ==  PayConstants.CONSUMER_NUMBER_MODE) {
                            EventBus.getDefault().post(new RefreshConsumerNumberModeEvent());
                        }

                        if (consumerModeHelper.getCurrentConsumerMode() == PayConstants.CONSUMER_TAKE_MODE) {
                            EventBus.getDefault().post(new RefreshConsumerTakeModeEvent());
                        }


                        if (consumerModeHelper.getCurrentConsumerMode() == PayConstants.CONSUMER_AMOUNT_MODE) {
                            EventBus.getDefault().post(new RefreshConsumerAmountModeEvent());
                        }


                        if (consumerModeHelper.getCurrentConsumerMode() == PayConstants.CONSUMER_WEIGHT_MODE) {
                            EventBus.getDefault().post(new RefreshConsumerWeightModeEvent());
                        }

                        if (consumerModeHelper.getCurrentConsumerMode() == PayConstants.CONSUMER_GOODS_MODE) {
                            EventBus.getDefault().post(new RefreshGoodsModeEvent());
                        }


                    }
                });
        faceChooseDialogFragment.show(getActivity());
    }


    /**
     * 处理人脸识别失败自动重试
     */
    protected void processFacePassFailRetryDelay(int recognizeState) {
        handleFacePassError(canSpeakFacePassFail, recognizeState);
        if (canSpeakFacePassFail) {
            canSpeakFacePassFail = false;
            canSpeakFacePassFailObserver = new DefaultDisposeObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    canSpeakFacePassFail = true;
                    canSpeakFacePassFailObserver = null;
                    EventBus.getDefault().post(new FacePassRetryEvent());
                }
            };
            //4秒之后重置识别失败语音提醒
            Observable.timer(4, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(canSpeakFacePassFailObserver);
        }


        LogHelper.print("--IdentityVerificationFragment--facePassFailRetryDelay: recognizeState = " + recognizeState);
    }

    /**
     * 人脸识别失败
     */
    protected abstract void handleFacePassError(boolean canSpeakFacePassFail, int recognizeState);

    /**
     * 人脸识别成功
     */
    protected abstract void handleFacePassSuccess(FacePassPeopleInfo facePassPeopleInfo);

    /**
     * end 刷脸认证 end
     */

    protected void goToAllAuth() {
        Log.d(TAG, "limegoToAllAuth: " + 596);
        goToAllAuth("");
    }

    protected void goToAllAuth(String authVoiceExtra) {

        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        if (consumerModeHelper.getCurrentConsumerMode() ==  PayConstants.CONSUMER_GOODS_MODE) {

            String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
            //消费者屏幕提示
            if (MainApplication.isJuHePay){
                speakTTSVoice("请扫码支付");
                ConsumerManager.INSTANCE.setConsumerAuthTips("请扫码支付");
            }else {
                if (TextUtils.isEmpty(authVoiceExtra)) {
                    speakTTSVoice(payTypeVoice);
                } else {
                    speakTTSVoice(authVoiceExtra + "," + payTypeVoice);
                }
                ConsumerManager.INSTANCE.setConsumerAuthTips(payTypeVoice);
            }


            boolean switchPayTypeCard = PaymentSettingMMKV.getSwitchPayTypeCard();
            if (switchPayTypeCard && !MainApplication.isJuHePay) {
                goICCardAuth();
            }
            boolean switchPayTypeFace = PaymentSettingMMKV.getSwitchPayTypeFace();
            if (switchPayTypeFace && !MainApplication.isJuHePay) {
                goFacePassAuth();
            }
            boolean switchPayTypeScan = PaymentSettingMMKV.getSwitchPayTypeScan();
            if (switchPayTypeScan) {
                goQRCodeAuth();
            }

        }else {
            String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
            //消费者屏幕提示
            if (TextUtils.isEmpty(authVoiceExtra)) {
                speakTTSVoice(payTypeVoice);
            } else {
                speakTTSVoice(authVoiceExtra + "," + payTypeVoice);
            }
            //消费者屏幕提示
            ConsumerManager.INSTANCE.setConsumerAuthTips(payTypeVoice);
            boolean switchPayTypeCard = PaymentSettingMMKV.getSwitchPayTypeCard();
            if (switchPayTypeCard) {
                goICCardAuth();
            }
            boolean switchPayTypeFace = PaymentSettingMMKV.getSwitchPayTypeFace();
            if (switchPayTypeFace) {
                Log.d(TAG, "limegoToAllAuth: " + 647);
                goFacePassAuth();
            }
            boolean switchPayTypeScan = PaymentSettingMMKV.getSwitchPayTypeScan();
            if (switchPayTypeScan) {
                goQRCodeAuth();
            }

        }




        LogHelper.print("--IdentityVerificationFragment--goToAuth");
    }

    protected void stopAllAuth() {
        hasStartAuth = false;
        stopICCardAuth();
        stopFacePassAuth();
        stopQRCodeAuth();
        LogHelper.print("--IdentityVerificationFragment--stopAuth");
    }

}
