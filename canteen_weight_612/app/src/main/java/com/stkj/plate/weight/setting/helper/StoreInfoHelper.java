package com.stkj.plate.weight.setting.helper;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.alibaba.fastjson.JSON;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.home.callback.OnGetStoreInfoListener;
import com.stkj.plate.weight.home.model.StoreInfo;
import com.stkj.plate.weight.home.service.HomeService;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * 店铺信息帮助类
 */
public class StoreInfoHelper extends ActivityWeakRefHolder {

    public final static String TAG = "StoreInfoHelper";
    private boolean isRequestStoreInfo;
    private Set<OnGetStoreInfoListener> onGetStoreInfoListenerSet = new HashSet<>();
    private StoreInfo mStoreInfo;

    public StoreInfoHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void requestStoreInfo() {
        if (isRequestStoreInfo) {
            return;
        }
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity == null) {
            return;
        }
        isRequestStoreInfo = true;
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("company_setup");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .getStoreInfo(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) mainActivity))
                .subscribe(new DefaultObserver<BaseNetResponse<StoreInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<StoreInfo> storeInfoBaseNetResponse) {
                        Log.d(TAG, "limecompany_setup: " + JSON.toJSONString(storeInfoBaseNetResponse));
                        isRequestStoreInfo = false;
                        StoreInfo storeInfo = storeInfoBaseNetResponse.getData();
                        if (storeInfo != null && !TextUtils.isEmpty(storeInfo.getDeviceName())) {
                            mStoreInfo = storeInfo;
                            for (OnGetStoreInfoListener listener : onGetStoreInfoListenerSet) {
                                listener.onGetStoreInfo(storeInfo);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isRequestStoreInfo = false;
                        Log.e(TAG, "limecompany_setup: " + e.getMessage());
                    }
                });
    }

    public void addGetStoreInfoListener(OnGetStoreInfoListener getStoreInfoListener) {
        onGetStoreInfoListenerSet.add(getStoreInfoListener);
    }

    public void removeGetStoreInfoListener(OnGetStoreInfoListener getStoreInfoListener) {
        onGetStoreInfoListenerSet.remove(getStoreInfoListener);
    }

    /**
     * 获取保存的店铺信息
     */
    public StoreInfo getStoreInfo() {
        return mStoreInfo;
    }

    @Override
    public void onClear() {
        onGetStoreInfoListenerSet.clear();
    }
}
