package com.stkj.plate.weight.pay.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.stkj.plate.weight.MainApplication;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.plate.weight.base.greendao.GreenDBConstants;
import com.stkj.plate.weight.base.greendao.generate.DaoMaster;
import com.stkj.plate.weight.base.greendao.generate.DaoSession;
import com.stkj.plate.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.utils.CommonDialogUtils;
import com.stkj.plate.weight.home.model.StoreInfo;
import com.stkj.plate.weight.pay.data.PayConstants;
import com.stkj.plate.weight.pay.helper.GoodsCateSpecHelper;
import com.stkj.plate.weight.pay.model.CategoryBean;
import com.stkj.plate.weight.pay.model.DeviceFoodTemplateParam;
import com.stkj.plate.weight.pay.model.GoodsSyncSuccessEvent;
import com.stkj.plate.weight.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.plate.weight.pay.model.RefreshUpdateGoodsEvent;
import com.stkj.plate.weight.pay.model.TTSSpeakEvent;
import com.stkj.plate.weight.pay.service.PayService;
import com.stkj.plate.weight.pay.ui.weight.GoodsDetailInfoLayout;
import com.stkj.plate.weight.setting.data.PaymentSettingMMKV;
import com.stkj.plate.weight.setting.helper.StoreInfoHelper;
import com.stkj.plate.weight.setting.model.FoodBean;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.plate.weight.setting.model.FoodSave;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.TimeUtils;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.plate.weight.pay.goods.callback.OnGoodsCateSpecListener;
import com.stkj.plate.weight.pay.goods.model.GoodsBaseInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 增加商品页面
 */
public class AddGoodsFragment extends BaseRecyclerFragment {
    public final static String TAG = "AddGoodsFragment";
    private ScrollView svContent;
    private GoodsDetailInfoLayout goodsDetailLay;
    private HashMap<String, String> packageFoodMap = null;
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add_goods;
    }

    @Override
    protected void initViews(View rootView) {
        svContent = (ScrollView) findViewById(R.id.sv_content);
        goodsDetailLay = (GoodsDetailInfoLayout) findViewById(R.id.goods_detail_lay);
        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RefreshConsumerGoodsModeEvent(0));
            }
        };
        ShapeTextView stvCancelAdd = (ShapeTextView) findViewById(R.id.stv_cancel_add);
        stvCancelAdd.setOnClickListener(backClickListener);
        findViewById(R.id.iv_add_goods_back).setOnClickListener(backClickListener);
        ShapeTextView stvContinueAdd = (ShapeTextView) findViewById(R.id.stv_continue_add);
        stvContinueAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGoodsCateSpec();
                svContent.scrollTo(0, 0);
                goodsDetailLay.resetAllLayoutAndData();
            }
        });
        ShapeTextView stvSaveStorage = (ShapeTextView) findViewById(R.id.stv_save_storage);
        stvSaveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoodsStorage();
            }
        });

        //设备信息
        StoreInfoHelper storeInfoHelper = mActivity.getWeakRefHolder(StoreInfoHelper.class);
        StoreInfo storeInfo = storeInfoHelper.getStoreInfo();
        if (storeInfo != null) {
            goodsDetailLay.getEt_goods_canting().setText(storeInfo.getCompany());
            goodsDetailLay.getEt_goods_chuangkou().setText(storeInfo.getDeviceName());
        }

    }


    //二维码扫码 商品 start
    private OnScanQRCodeListener mScanQRCodeListener = new OnScanQRCodeListener() {
        @Override
        public void onScanQrCode(String data) {
            if (!isDetached()) {
//                requestQrCodeDetail(data);
            }
        }

        @Override
        public void onScanQRCodeError(String message) {
            if (!isDetached()) {
                CommonDialogUtils.showTipsDialog(mActivity, message);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterScanQRCodeListener(mScanQRCodeListener);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            requestGoodsCateSpec();
        }
        waitScanQrCode();
    }

    /**
     * 等待扫码新增商品
     */
    private void waitScanQrCode() {
        DeviceManager.INSTANCE.getDeviceInterface().scanQrCode(mScanQRCodeListener);
    }
    //二维码扫码 商品 end



    /**
     * 请求商品分类规格
     */
    private void requestGoodsCateSpec() {
        showLoadingDialog();
        GoodsCateSpecHelper goodsCateSpecHelper = mActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
        goodsCateSpecHelper.requestAllList(new OnGoodsCateSpecListener() {
            @Override
            public void onGetCateSpecListEnd() {
                hideLoadingDialog();
            }

            @Override
            public void onGetCateSpecError(String msg) {
                hideLoadingDialog();
            }
        });
    }

    /**
     * 保存商品入库
     */
    @SuppressLint("AutoDispose")
    public void saveGoodsStorage() {
        //菜品/套餐名称
        String goodsName = goodsDetailLay.getGoodsName();
        if (TextUtils.isEmpty(goodsName)) {
            AppToast.toastMsg("菜品/套餐名称不能为空!");
            return;
        }

        if (!goodsName.matches("[\\u3400-\\u4DBF\\u4E00-\\u9FFF]*")) {
            AppToast.toastMsg("菜品名称只能为二十位以内汉字");
            return;
        }
        //计价方式
        String goodsPriceType = goodsDetailLay.getStv_goods_jjfs().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceType)) {
            AppToast.toastMsg("请选择计价方式!");
            return;
        }
        //计价规格
        String goodsPriceGuiGe = goodsDetailLay.getStv_goods_jjgg().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceGuiGe)) {
            AppToast.toastMsg("请选择计价规格!");
            return;
        }
        //单价
        String goodsPriceUnit = goodsDetailLay.getSet_goods_qrcode().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceUnit)) {
            AppToast.toastMsg("单价不能为空!");
            return;
        }

        //分类
        String goodsPriceTab = goodsDetailLay.getGoods_fl_summary().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceTab)) {
            AppToast.toastMsg("请选择分类!");
            return;
        }


        //类型
        String goodsType = goodsDetailLay.getGoods_lx_summary().getText().toString().trim();
        if (TextUtils.isEmpty(goodsType)) {
            AppToast.toastMsg("请选择类型!");
            return;
        }



        //排序
        String goodsSort = goodsDetailLay.getSet_goods_sort().getText().toString().trim();

        if (TextUtils.isEmpty(goodsSort)){
            goodsSort = null;
        }else {
            if (Double.parseDouble(goodsSort) < 1){
                AppToast.toastMsg("请输入排序(1~999)");
                return;
            }
        }



        //菜品图片
        String uploadPicUrl = TextUtils.isEmpty(goodsDetailLay.getUploadPicUrl()) ? "" : goodsDetailLay.getUploadPicUrl().split(",")[0];

        //备注
        String goodsNote = goodsDetailLay.getGoodsNote();
        GoodsBaseInfo goodsBaseInfo = new GoodsBaseInfo();
        goodsBaseInfo.setGoodsName(goodsPriceType);


        goodsBaseInfo.setGoodsNote(goodsNote);


        String createOrderNumber = PayConstants.createOrderNumber();
        MainApplication.createOrderNumber = createOrderNumber;
        TreeMap<String, String> foodSaveParams = ParamsUtils.newSortParamsMapWithMode("foodSave");

        boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();

//        (String , String name, int type, HashMap<String, String> categoryMap, String deviceNo,
//        int status, int pricingMethod, String pricingUnit, String tenantId, double foodPrice, int sort, String foodImgPath, String remark, HashMap<String, String> packageFoodMap) {

        String jsonStr = goodsPriceType.equals("按份") ? PaymentSettingMMKV.getTab0List():PaymentSettingMMKV.getTab1List();
        Log.i(TAG, "limesaveGoodsStorage goodsPriceType: " + goodsPriceType);
        Log.d(TAG, "limesaveGoodsStorage jsonStr: " + jsonStr);
        List<CategoryBean> categroyList =  JSON.parseArray(jsonStr,CategoryBean.class);
        HashMap<String, String> categoryMap = new HashMap<>();

        for (CategoryBean categoryBean : categroyList){
            Log.d(TAG, "limesaveGoodsStorage categoryBean.getName(): " + categoryBean.getName() +"   goodsPriceTab: " + goodsPriceTab);
            if (categoryBean.getName().equals(goodsPriceTab)){
                categoryMap.put(categoryBean.getId(),categoryBean.getName());
            }
        }

        //套餐
        if (goodsType.equals("套餐") && (goodsDetailLay.getListTagFl().size() > 0)){

//            if (goodsDetailLay.getListTagFl() == null || goodsDetailLay.getListTagFl().size() < 1) {
//                AppToast.toastMsg("请选择套餐!");
//                return;
//            }
            packageFoodMap = new HashMap<>();
//            if (goodsDetailLay.getTcMap() != null && goodsDetailLay.getTcMap().size() > 0) {
//                goodsDetailLay.getTcMap().forEach((key, value) -> {
//                    packageFoodMap.put(key, value);
//                });
//            }

            if (foodInfoTableDao == null) {
                initFoodInfoTableDao();
            }
            QueryBuilder<FoodInfoTable> qbCount   = foodInfoTableDao.queryBuilder();
            qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
            qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
            List<FoodInfoTable> foods   =  qbCount.list();

            for (int i = 0; i < goodsDetailLay.getListTagFl().size(); i++) {
                for (int j = 0; j < foods.size(); j++) {
                    if (goodsDetailLay.getListTagFl().get(i).getItemText().toString().trim().equals(foods.get(j).getName())) {
                        packageFoodMap.put(foods.get(j).getTemplateId(), foods.get(j).getName());
                        break;
                    }
                }
            }

        } else {
            packageFoodMap = null;
        }


        DeviceFoodTemplateParam deviceFoodTemplateParam = new DeviceFoodTemplateParam(PayConstants.createOrderNumber(),goodsName,goodsType.equals("单品") ? 1: 2,categoryMap,DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                0,goodsPriceType.equals("按份") ? 1 : 2,goodsPriceType.equals("按份")?"份":"1kg",null,Double.parseDouble(goodsPriceUnit),goodsSort,uploadPicUrl,goodsNote,packageFoodMap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            foodSaveParams.put("foodSaveParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodTemplateParam).getBytes()));
        }
        Log.d(TAG, "limesaveGoodsStorage deviceFoodTemplateParam: " + JSON.toJSONString(deviceFoodTemplateParam));
        showLoadingDialog();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodSave(ParamsUtils.signSortParamsMap(foodSaveParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<FoodSave>() {
                    @Override
                    protected void onSuccess(FoodSave foodSave) {
                        hideLoadingDialog();
                        if (foodSave != null && foodSave.getData() != null) {
                            AppToast.toastMsg("保存成功!");
                            List<FoodBean> foodInfoList = new ArrayList<>();
                            foodInfoList.add(foodSave.getData());
                            Log.d(TAG, "limeFoodSave: " + JSON.toJSONString(foodSave.getData()));
                            addFacePassToLocal(foodInfoList);

                        }else {
                            EventBus.getDefault().post(new TTSSpeakEvent(foodSave.getMsg()));
                            AppToast.toastMsg(foodSave.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        AppToast.toastMsg("保存失败!");

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
                                                TextUtils.isEmpty(foodInfo.getCreateTime()) ? TimeUtils.date2String(new Date(), "yyyy-MM-dd HH:mm:ss") : foodInfo.getCreateTime(),
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
                                                0,
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

                    EventBus.getDefault().post(new RefreshUpdateGoodsEvent());
                    EventBus.getDefault().post(new RefreshConsumerGoodsModeEvent(0));
                    EventBus.getDefault().post(new GoodsSyncSuccessEvent());
                }

            }
        });



    }

    private void onSaveStorageError(String msg) {
        CommonDialogUtils.showTipsDialog(mActivity, "保存失败!" + msg);
    }
}
