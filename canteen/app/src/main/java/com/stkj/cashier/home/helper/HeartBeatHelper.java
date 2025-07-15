package com.stkj.cashier.home.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.alibaba.fastjson.JSON;
import com.stkj.cashier.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.cashier.base.greendao.GreenDBConstants;
import com.stkj.cashier.base.greendao.generate.DaoMaster;
import com.stkj.cashier.base.greendao.generate.DaoSession;
import com.stkj.cashier.base.greendao.generate.FoodInfoTableDao;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.home.model.HeartBeatInfo;
import com.stkj.cashier.home.model.OfflineSetInfo;
import com.stkj.cashier.home.service.HomeService;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.GoodsSyncSuccessEvent;
import com.stkj.cashier.pay.model.RefreshUpdateGoodsEvent;
import com.stkj.cashier.setting.data.ServerSettingMMKV;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.cashier.setting.model.FoodBean;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.cashier.setting.model.FoodListInfo;
import com.stkj.cashier.setting.model.FoodSyncCallback;
import com.stkj.cashier.setting.service.SettingService;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 心跳帮助类
 */
public class HeartBeatHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    public final static String TAG = "HeartBeatHelper";
    //下发人脸数据
    public static final String COMMAND_UPDATE_FACE_PASS = "1";
    //按次消费金额信息+餐厅时段信息
    public static final String COMMAND_INTERVAL_CARD_TYPE = "2";
    //离线
    public static final String COMMAND_OFFLINE_SET = "3";
    //商店信息
    public static final String COMMAND_UPDATE_STORE_INFO = "4";
    //菜品信息
    public static final String COMMAND_UPDATE_FOODS_INFO = "5";
    private int currentTotalBeatSecond;
    private int mServerBeatDelayTime;//默认30秒
    private boolean forbidHeatBeat;
    private int totalPage = 1;
    private int pageIndex = 0;
    private String syncNo = "";
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;

    public HeartBeatHelper(@NonNull Activity activity) {
        super(activity);
        mServerBeatDelayTime = ServerSettingMMKV.getHeartBeatInterval();
        currentTotalBeatSecond = 0;
    }

    public void setForbidHeatBeat(boolean forbidHeatBeat) {
        this.forbidHeatBeat = forbidHeatBeat;
    }

    public void setServerBeatDelay(int mBeatDelay) {
        this.mServerBeatDelayTime = mBeatDelay;
        currentTotalBeatSecond = 0;
    }

    @Override
    public void onCountDown() {
        currentTotalBeatSecond += 1;
        if (currentTotalBeatSecond >= mServerBeatDelayTime) {
            LogHelper.print("---HeartBeatHelper---requestHeartBeat");
            requestHeartBeat();
            currentTotalBeatSecond = 0;
        }
    }

    public void requestHeartBeat() {
        if (forbidHeatBeat) {
            return;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .heartBeat(ParamsUtils.newMachineParamsMap())
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<HeartBeatInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<HeartBeatInfo> baseNetResponse) {
                        if (forbidHeatBeat) {
                            return;
                        }

                        HeartBeatInfo heartBeatInfo = baseNetResponse.getData();
                        if (heartBeatInfo != null) {
                            String updateUserInfo = heartBeatInfo.getUpdateUserInfo();
                            if (!TextUtils.isEmpty(updateUserInfo)) {
                                Log.i(TAG, "limefoodSyncCallback: " + 122 + "   updateUserInfo: " + updateUserInfo);
                                String[] split = updateUserInfo.split("&");
                                for (String command : split) {
                                    switch (command) {
                                        case COMMAND_UPDATE_FACE_PASS:
                                            Activity activity = getHolderActivityWithCheck();
                                            if (activity != null) {

                                            }
                                            break;
                                        case COMMAND_INTERVAL_CARD_TYPE:
                                            Activity activity1 = getHolderActivityWithCheck();
                                            if (activity1 != null) {
                                                ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, activity1);
                                                if (consumerModeHelper != null) {
                                                    consumerModeHelper.requestIntervalCardType();
                                                    consumerModeHelper.requestCanteenCurrentTimeInfo();
                                                }
                                            }
                                            break;
                                        case COMMAND_OFFLINE_SET:
                                            offlineSet();
                                            break;
                                        case COMMAND_UPDATE_STORE_INFO:
                                            Activity activity3 = getHolderActivityWithCheck();
                                            if (activity3 != null) {
                                                StoreInfoHelper storeInfoHelper = ActivityHolderFactory.get(StoreInfoHelper.class, activity3);
                                                if (storeInfoHelper != null) {
                                                    storeInfoHelper.requestStoreInfo();
                                                }
                                            }
                                            break;
                                        case COMMAND_UPDATE_FOODS_INFO:
                                            Log.i(TAG, "limefoodSyncCallback: " + 160 + "   updateUserInfo: " + updateUserInfo);
                                            foodSync(1);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                });
    }


    /**
     * 同步菜品
     */
    @SuppressLint("AutoDispose")
    public void foodSync(int inferior_type) {
        Log.i(TAG, "limefoodSyncCallback: " + 177);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodSync");
        paramsMap.put("inferior_type", String.valueOf(inferior_type));
        paramsMap.put("pageIndex", String.valueOf(pageIndex));
        paramsMap.put("pageSize", String.valueOf(999));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .getAllFood(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<FoodListInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodListInfo> baseNetResponse) {
                        Log.i(TAG, "limefoodSyncCallback 189: " + JSON.toJSONString(baseNetResponse));
                        try {
                            FoodListInfo responseData = baseNetResponse.getData();
                            if (responseData != null && responseData.getResults() != null && !responseData.getResults().isEmpty()) {
                                syncNo = responseData.getSyncNo();
                                totalPage = responseData.getTotalPage();
                                List<FoodBean> foodInfoList = responseData.getResults();
                                addFacePassToLocal(foodInfoList);
                                if (pageIndex >= totalPage) {
                                    pageIndex = 0;
                                    foodSyncCallback();
                                } else {
                                    pageIndex++;
                                    if (pageIndex >= totalPage) {
                                        pageIndex = 0;
                                        foodSyncCallback();
                                    } else {
                                        foodSync(inferior_type);
                                    }

                                }

                            } else {
                                //AppToast.toastMsg(baseNetResponse.getMessage());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "limefoodSyncCallback 209: " +  e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //AppToast.toastMsg(e.getMessage());
                        Log.e(TAG, "limefoodSyncCallback: " +  e.getMessage());
                    }
                });
    }

    /**
     * 同步菜品
     */
    @SuppressLint("AutoDispose")
    public void foodSyncCallback() {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodSyncCallback");
        paramsMap.put("syncNo", syncNo);
        //paramsMap.put("pageSize", String.valueOf(50));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .foodSyncCallback(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<FoodSyncCallback>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodSyncCallback> baseNetResponse) {
                        Log.d(TAG, "limefoodSyncCallback: " + 322);
                        EventBus.getDefault().post(new RefreshUpdateGoodsEvent());
                        EventBus.getDefault().post(new GoodsSyncSuccessEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "limefoodSyncCallback  241: " +  e.getMessage());
                    }
                });
    }

    private void initFoodInfoTableDao() {

        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.FACE_DB_NAME, null);
        Database database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        foodInfoTableDao = daoSession.getFoodInfoTableDao();

    }

    private void addFacePassToLocal(List<FoodBean> foodInfoList) {
        // 同步食物到本地数据库
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {

                if (foodInfoTableDao == null) {
                    initFoodInfoTableDao();
                }
                if (foodInfoList != null && foodInfoList.size() > 0) {
                    daoSession.runInTx(new Runnable() {
                        @Override
                        public void run() {

                            for (FoodBean foodInfo : foodInfoList) {
                                foodInfoTableDao.insertOrReplace(
                                        new FoodInfoTable(
                                                foodInfo.getId(),
                                                foodInfo.getTenantId(),
                                                foodInfo.getDeleteFlag(),
                                                foodInfo.getCreateTime(),
                                                foodInfo.getCreateUser(),
                                                foodInfo.getUpdateTime(),
                                                foodInfo.getUpdateUser(),
                                                foodInfo.getRestaurantId(),
                                                JSON.toJSONString(foodInfo.getCategoryMap()),
                                                foodInfo.getName(),
                                                foodInfo.getImgpath(),
                                                foodInfo.getDeviceId(),
                                                foodInfo.getPricingMethod(),
                                                foodInfo.getPricingUnit(),
                                                foodInfo.getType(),
                                                foodInfo.getUnitPriceMoney().getCent(),
                                                foodInfo.getUnitPriceMoney().getCurrency(),
                                                foodInfo.getUnitPriceMoney().getAmount(),
                                                foodInfo.getUnitPriceMoney().getCentFactor(),
                                                Integer.parseInt(TextUtils.isEmpty(foodInfo.getSort()) ?  "0" : foodInfo.getSort()),
                                                false,
                                                foodInfo.getStatus(),
                                                foodInfo.getTemplateId(),
                                                foodInfo.getRemark(),
                                                "0","1","1"
                                        )
                                );

                            }

                            //AppToast.toastMsg("同步商品成功");


                        }
                    });
                }


            }
        });



    }

    public void offlineSet() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        TreeMap<String, String> offlineSetParams = ParamsUtils.newSortParamsMapWithMode("OfflineSet");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .offlineSet(ParamsUtils.signSortParamsMap(offlineSetParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<OfflineSetInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OfflineSetInfo> baseNetResponse) {
                        if (baseNetResponse.getData() != null) {
                            LogHelper.print("--HeartBeatHelper--offlineSet success: " + baseNetResponse.getData());
                        } else {
                            LogHelper.print("--HeartBeatHelper--offlineSet success: data is null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.print("--HeartBeatHelper--offlineSet error: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onClear() {

    }

}
