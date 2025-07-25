package com.stkj.plate.weight.setting.ui.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.plate.weight.base.greendao.GreenDBConstants;
import com.stkj.plate.weight.base.greendao.generate.DaoMaster;
import com.stkj.plate.weight.base.greendao.generate.DaoSession;
import com.stkj.plate.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.plate.weight.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.pay.model.GoodsSyncSuccessEvent;
import com.stkj.plate.weight.pay.model.RefreshUpdateGoodsEvent;
import com.stkj.plate.weight.setting.callback.FacePassSettingCallback;
import com.stkj.plate.weight.setting.model.FoodBean;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.plate.weight.setting.model.FoodListInfo;
import com.stkj.plate.weight.setting.model.FoodSyncCallback;
import com.stkj.plate.weight.setting.model.SearchFacePassPeopleParams;
import com.stkj.plate.weight.setting.service.SettingService;
import com.stkj.plate.weight.setting.ui.adapter.FacePassPeopleViewHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.SpanUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 商品设置
 */
public class TabGoodsSettingFragment extends BaseRecyclerFragment implements  FacePassSettingCallback {
    public final static String TAG = "TabGoodsSettingFragment";
    private LinearLayout llFaceSetting;
    private ShapeTextView stvFacepassSetting;
    private TextView stvFacepassCount;
    private ShapeSelectTextView sstvFacepassDetail;
    private ShapeSelectTextView sstvFacepassDelAll;
    private ShapeSelectTextView sstvFacepassUpdate;
    private ImageView ivFaceDataBack;
    private LinearLayout llFaceData;
    private FrameLayout flAllPeople;
    private ShapeTextView stvAllPeople;
    private ImageView ivAllPeople;
    private FrameLayout flAllDepartment;
    private ShapeTextView stvAllDepartment;
    private ImageView ivAllDepartment;
    private EditText etPeopleSearch;
    private ShapeTextView tvPeopleSearch;
    private RecyclerView rvFacePass;
    private CommonRecyclerAdapter facePassAdapter;
    private ProgressBar ivFacePassLoading;
    private TextView tvFacePassLoading;
    private AppSmartRefreshLayout srlFacePassList;
    private TextView tvFacePassEmpty;
    private TextView tv_goods_count;
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    private SearchFacePassPeopleParams searchFacePassParams = new SearchFacePassPeopleParams();
    private int pageIndex = 0;
    private String syncNo = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_goods_setting;
    }

    @Override
    protected void initViews(View rootView) {

        EventBusUtils.registerEventBus(this);
        tv_goods_count = (TextView) findViewById(R.id.tv_goods_count);
        initFoodInfoTableDao();
        QueryBuilder<FoodInfoTable> qb   = foodInfoTableDao.queryBuilder();
        qb.where(FoodInfoTableDao.Properties.Status.eq(1));
        qb.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
        refreshFacePassTotalCount(qb.count());
        tvFacePassLoading = (TextView) findViewById(R.id.tv_face_pass_loading);
        ivFacePassLoading = (ProgressBar) findViewById(R.id.iv_face_pass_loading);


        llFaceSetting = (LinearLayout) findViewById(R.id.ll_face_setting);
        stvFacepassSetting = (ShapeTextView) findViewById(R.id.stv_facepass_setting);

//        stvFacepassSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //正在更新人脸库，不可以设置
//                FacePassHelper facePassHelper = getFacePassHelper();
//                if (facePassHelper.isAddOrDeleteLocalFacePass()) {
//                    CommonDialogUtils.showTipsDialog(mActivity, "人脸库正在更新中,不可操作,请稍等~");
//                    return;
//                }
//                //暂停人脸识别功能
//                EventBus.getDefault().post(new PauseFacePassDetect());
//                FacePassSettingAlertFragment alertFragment = new FacePassSettingAlertFragment();
//                alertFragment.setFacePassSettingCallback(TabGoodsSettingFragment.this);
//                FragmentUtils.safeReplaceFragment(getParentFragmentManager(), alertFragment, R.id.fl_setting_second_content);
//            }
//        });




        sstvFacepassUpdate = (ShapeSelectTextView) findViewById(R.id.sstv_facepass_update);
        sstvFacepassUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("全量更新将删除本地数据库?")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                setFacePassOperateEnable(false);
                                foodSync(0);
                            }
                        })
                        .setRightNavTxt("取消").show(mActivity);
            }
        });

        ivFaceDataBack = (ImageView) findViewById(R.id.iv_face_data_back);
        ivFaceDataBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facePassAdapter.removeAllData();
                ivFaceDataBack.setVisibility(View.GONE);
                llFaceData.setVisibility(View.GONE);
                llFaceSetting.setVisibility(View.VISIBLE);
            }
        });
        llFaceData = (LinearLayout) findViewById(R.id.ll_face_data);
        flAllPeople = (FrameLayout) findViewById(R.id.fl_all_people);
        stvAllPeople = (ShapeTextView) findViewById(R.id.stv_all_people);
        ivAllPeople = (ImageView) findViewById(R.id.iv_all_people);
        flAllDepartment = (FrameLayout) findViewById(R.id.fl_all_department);
        stvAllDepartment = (ShapeTextView) findViewById(R.id.stv_all_department);
        ivAllDepartment = (ImageView) findViewById(R.id.iv_all_department);
        etPeopleSearch = (EditText) findViewById(R.id.et_people_search);
        tvPeopleSearch = (ShapeTextView) findViewById(R.id.tv_people_search);
        rvFacePass = (RecyclerView) findViewById(R.id.rv_face_pass);
        facePassAdapter = new CommonRecyclerAdapter(false);
        facePassAdapter.addViewHolderFactory(new FacePassPeopleViewHolder.Factory());
        rvFacePass.setAdapter(facePassAdapter);
        srlFacePassList = (AppSmartRefreshLayout) findViewById(R.id.srl_face_pass_list);



        setFacePassOperateEnable(true);

    }



    /**
     * 获取人脸识别库人员列表
     */
    private void getFacePassPeopleList(boolean isFirstPage) {
        if (isFirstPage) {
            searchFacePassParams.setRequestOffset(0);
        } else {
            searchFacePassParams.setRequestOffset(facePassAdapter.getItemCount());
        }
        searchFacePassParams.setSearchKey(etPeopleSearch.getText().toString());
        searchFacePassParams.setAccountType(stvAllPeople.getText().toString());
        searchFacePassParams.setDepartment(stvAllDepartment.getText().toString());

    }

    private void setFacePassOperateEnable(boolean enable) {
        if (enable) {
//            sstvFacepassDetail.setShapeSelect(true);
//            sstvFacepassDetail.setClickable(true);
//            sstvFacepassDelAll.setShapeSelect(true);
//            sstvFacepassDelAll.setClickable(true);
            sstvFacepassUpdate.setShapeSelect(true);
            sstvFacepassUpdate.setClickable(true);
            ivFacePassLoading.setVisibility(View.GONE);
            tvFacePassLoading.setVisibility(View.GONE);
        } else {
            //置灰不可操作
//            sstvFacepassDetail.setShapeSelect(false);
//            sstvFacepassDetail.setClickable(false);
//            sstvFacepassDelAll.setShapeSelect(false);
//            sstvFacepassDelAll.setClickable(false);
            sstvFacepassUpdate.setShapeSelect(false);
            sstvFacepassUpdate.setClickable(false);
            ivFacePassLoading.setVisibility(View.VISIBLE);
            tvFacePassLoading.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 同步菜品
     */
    @SuppressLint("AutoDispose")
    public void foodSync(int inferior_type) {

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
                        setFacePassOperateEnable(true);

                        FoodListInfo responseData = baseNetResponse.getData();
                        if (responseData != null && responseData.getResults() != null && !responseData.getResults().isEmpty()) {
                            syncNo = responseData.getSyncNo();
                            List<FoodBean> foodInfoList = responseData.getResults();
                            addFacePassToLocal(foodInfoList);

                            if (pageIndex >= responseData.getTotalPage()){
                                pageIndex = 0;
                                foodSyncCallback();
                            }else {
                                pageIndex++;
                                foodSync(inferior_type);
                            }

                        } else {
                            AppToast.toastMsg(baseNetResponse.getMessage());
                        }



                    }

                    @Override
                    public void onError(Throwable e) {
                        setFacePassOperateEnable(true);
                        AppToast.toastMsg(e.getMessage());
//                        callbackFinishFacePass(e.getMessage(), true);
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
                        AppToast.toastMsg("同步商品成功");
                        EventBus.getDefault().post(new RefreshUpdateGoodsEvent());
                    }

                    @Override
                    public void onError(Throwable e) {

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
                                                0,
                                                false,
                                                foodInfo.getStatus(),
                                                foodInfo.getTemplateId(),
                                                foodInfo.getRemark(),
                                                "0","1","1"
                                        )
                                );

                            }
                            QueryBuilder<FoodInfoTable> qbDelete   = foodInfoTableDao.queryBuilder();
                            qbDelete.where(FoodInfoTableDao.Properties.DeleteFlag.eq("DELETED"));
                            DeleteQuery<FoodInfoTable> dq = qbDelete.buildDelete();
                            dq.executeDeleteWithoutDetachingEntities();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    QueryBuilder<FoodInfoTable> qb   = foodInfoTableDao.queryBuilder();
                                    qb.where(FoodInfoTableDao.Properties.Status.eq(1));
                                    qb.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
                                    refreshFacePassTotalCount(qb.count());

                                }
                            });




                            daoSession.clear();


                        }
                    });
                }


            }
        });



    }

    private void deleteAllFacePass() {
        setFacePassOperateEnable(false);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
//        FacePassHelper facePassHelper = getFacePassHelper();
//        facePassHelper.addOnFacePassListener(this);
//        //正在操作本地人脸库时，按钮不可操作
//        if (!facePassHelper.isAddOrDeleteLocalFacePass()) {
//            setFacePassOperateEnable(true);
//            facePassHelper.getFacePassLocalCount();
//        } else {
//            setFacePassOperateEnable(false);
//        }
    }

    @Override
    public void onDetach() {
//        FacePassHelper facePassHelper = getFacePassHelper();
//        facePassHelper.removeOnFacePassListener(this);
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }







    /**
     * 统一处理删除人脸库成功和失败
     */
    private void handleDeleteAllFacePass(boolean needRequestAllFace) {
        refreshFacePassTotalCount(0);
        if (!needRequestAllFace) {
            setFacePassOperateEnable(true);
        }
    }




    private void refreshFacePassTotalCount(long count) {
        SpanUtils.with(tv_goods_count)
                .append("当前有 ")
                .append(String.valueOf(count))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append(" 件商品 ")
                .create();
    }



    @Override
    public void needUpdateFacePass() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodsSyncSuccessEvent(GoodsSyncSuccessEvent eventBus) {
        initFoodInfoTableDao();
        foodInfoTableDao.detachAll();
        QueryBuilder<FoodInfoTable> qb   = foodInfoTableDao.queryBuilder();
        qb.where(FoodInfoTableDao.Properties.Status.eq(1));
        qb.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
        refreshFacePassTotalCount(qb.count());
    }

}
