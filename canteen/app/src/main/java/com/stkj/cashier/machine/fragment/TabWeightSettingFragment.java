package com.stkj.cashier.machine.fragment;

import static java.lang.Math.ceil;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.cashier.base.greendao.GreenDBConstants;
import com.stkj.cashier.base.greendao.generate.DaoMaster;
import com.stkj.cashier.base.greendao.generate.DaoSession;
import com.stkj.cashier.base.greendao.generate.FoodInfoTableDao;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.AppNetManager;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.ui.dialog.BindingCoastAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.BindingPwdAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonBindAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonBindSignleAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.FacePassSettingBindAlertFragment;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.home.ui.widget.ToastUtil;
import com.stkj.cashier.home.ui.widget.switchbutton.LimeSwitchButton;
import com.stkj.cashier.machine.adapter.SettingBindTabInfoViewHolder;
import com.stkj.cashier.machine.model.SettingBindTabInfo;
import com.stkj.cashier.machine.utils.ToastUtils;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.goods.callback.GoodsAutoSearchListener;
import com.stkj.cashier.pay.goods.model.GoodsBaseInfo;
import com.stkj.cashier.pay.model.BindFragmentBackEvent;
import com.stkj.cashier.pay.model.BindFragmentSwitchEvent;
import com.stkj.cashier.pay.model.CategoryBean;
import com.stkj.cashier.pay.model.DeviceFoodTemplateParam;
import com.stkj.cashier.pay.model.FoodCategoryListInfo;
import com.stkj.cashier.pay.model.RefreshUpdateGoodsEvent;
import com.stkj.cashier.pay.model.TTSSpeakEvent;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.pay.ui.adapter.FoodGridAdapter;
import com.stkj.cashier.pay.ui.fragment.GoodsConsumerFragment;
import com.stkj.cashier.pay.ui.fragment.SimpleCardFragment;
import com.stkj.cashier.pay.ui.weight.GoodsAutoSearchLayout;
import com.stkj.cashier.pay.ui.weight.GoodsDetailInfoLayout;
import com.stkj.cashier.pay.ui.weight.GoodsWeightDetailInfoLayout;
import com.stkj.cashier.pay.ui.weight.GridSpacingItemDecoration;
import com.stkj.cashier.setting.callback.FacePassSettingCallback;
import com.stkj.cashier.setting.data.DeviceSettingMMKV;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.helper.AppUpgradeHelper;
import com.stkj.cashier.setting.model.FoodBean;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.cashier.setting.model.FoodListInfo;
import com.stkj.cashier.setting.model.FoodSave;
import com.stkj.cashier.setting.model.FoodSyncCallback;
import com.stkj.cashier.setting.model.PauseFacePassDetect;
import com.stkj.cashier.setting.service.SettingService;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.tabs.RoundTabLayout;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.SpanUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

/**
 * 设置页面
 */
public class TabWeightSettingFragment extends BaseRecyclerFragment implements View.OnClickListener, FacePassSettingCallback, AppUpgradeHelper.OnAppUpgradeListener {

    public final static String TAG = "TabBindSettingFragment";
    private RecyclerView rvTopTab;
    private ImageView iv_back;
    private LinearLayout ll_app_settings;
    private LinearLayout ll_app_warning;
    private LinearLayout ll_app_face;
    private LinearLayout ll_app_coast;
    private LinearLayout ll_app_foods;
    private LinearLayout ll_app_food_add;
    private TextView tv_title_name;
    private TextView tv_sync_foods;
    private ShapeTextView stv_page_back;
    private ShapeTextView stv_page_next;
    private GoodsWeightDetailInfoLayout goodsDetailLay;
    private TextView tv_add_food;
    private RelativeLayout rl_server_addr;
    private RelativeLayout rl_version;
    private RelativeLayout rl_restart_app;
    private RelativeLayout rl_face_value;
    private RelativeLayout rl_face_sync;
    private RelativeLayout rl_coast_total;
    private LinearLayout ll_app_weight;
    private RoundTabLayout slidingTablayoutt;
    private LinearLayout rv_goods_storage_list_empty;
    private RecyclerView rv_goods_storage_list;
    private GoodsAutoSearchLayout goods_auto_search;
    private LimeSwitchButton switch_warning;
    private TextView tv_face_count;
    private TextView tv_app_version;
    private TextView tv_server_addr;
    private TextView tv_total_count;
    private BaseDialogFragment dialogFragment;
    private FoodGridAdapter foodGridAdapter;
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    private String[] mTitles = {"全部"};
    private String[] beforeTitles = {};
    private MyPagerAdapter mAdapter;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private int pageIndex = 0;
    private String syncNo = "";
    private String queryName = "";
    private int pageNumberGlobal = 0;
    private int totalPages = 0;
    private long totalCount = 0;
    private int pageSize = 12;
    private int offset = 0;
    public static String queryPricingMethod =  "2";
    public static String queryTab = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_bind_setting;
    }

    @Override
    protected void initViews(View rootView) {
        rvTopTab = (RecyclerView) findViewById(R.id.rv_top_tab);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        ll_app_settings = (LinearLayout) findViewById(R.id.ll_app_settings);
        ll_app_face = (LinearLayout) findViewById(R.id.ll_app_face);
        ll_app_food_add = (LinearLayout) findViewById(R.id.ll_app_food_add);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_sync_foods = (TextView) findViewById(R.id.tv_sync_foods);
        switch_warning = (LimeSwitchButton) findViewById(R.id.switch_warning);
        tv_add_food = (TextView) findViewById(R.id.tv_add_food);
        goodsDetailLay = (GoodsWeightDetailInfoLayout) findViewById(R.id.goods_detail_lay);
        tv_face_count = (TextView) findViewById(R.id.tv_face_count);
        goods_auto_search = (GoodsAutoSearchLayout) findViewById(R.id.goods_auto_search);
        rl_server_addr = (RelativeLayout) findViewById(R.id.rl_server_addr);
        rl_version = (RelativeLayout) findViewById(R.id.rl_version);
        rl_restart_app = (RelativeLayout) findViewById(R.id.rl_restart_app);
        rl_face_value = (RelativeLayout) findViewById(R.id.rl_face_value);
        rl_face_sync = (RelativeLayout) findViewById(R.id.rl_face_sync);
        tv_app_version = (TextView) findViewById(R.id.tv_app_version);
        tv_server_addr = (TextView) findViewById(R.id.tv_server_addr);
        ll_app_weight = (LinearLayout) findViewById(R.id.ll_app_weight);
        ll_app_warning = (LinearLayout) findViewById(R.id.ll_app_warning);
        ll_app_foods = (LinearLayout) findViewById(R.id.ll_app_foods);
        ll_app_coast = (LinearLayout) findViewById(R.id.ll_app_coast);
        rl_coast_total = (RelativeLayout) findViewById(R.id.rl_coast_total);
        tv_total_count = (TextView) findViewById(R.id.tv_total_count);
        rv_goods_storage_list_empty = (LinearLayout) findViewById(R.id.rv_goods_storage_list_empty);
        rv_goods_storage_list = (RecyclerView) findViewById(R.id.rv_goods_storage_list);
        stv_page_back = (ShapeTextView) findViewById(R.id.stv_page_back);
        stv_page_next = (ShapeTextView) findViewById(R.id.stv_page_next);
        if (BuildConfig.DEBUG) {
            tv_server_addr.setText(AppNetManager.API_TEST_URL);
        } else {
            tv_server_addr.setText(AppNetManager.API_OFFICIAL_URL);
        }
        RxView.clicks(rl_version)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        AppUpgradeHelper appUpgradeHelper = mActivity.getWeakRefHolder(AppUpgradeHelper.class);
                        appUpgradeHelper.setOnAppUpgradeListener(TabWeightSettingFragment.this);
                        appUpgradeHelper.checkAppVersion();
                    }
                });
        goods_auto_search = (GoodsAutoSearchLayout) findViewById(R.id.goods_auto_search);
        goods_auto_search.setGoodsAutoSearchListener(this, new GoodsAutoSearchListener() {


            @Override
            public void onStartGetGoodsItemDetail(FoodInfoTable foodInfoTable) {
                queryName = foodInfoTable.getName();
                pageNumberGlobal = 0;
                nextPage(pageNumberGlobal);
                goods_auto_search.getEtGoodsSearch().setText(queryName);
            }

            @Override
            public void onSuccessGetGoodsItemDetail(FoodInfoTable saleListInfo) {

            }

            @Override
            public void onErrorGetGoodsItemDetail(FoodInfoTable goodsIdBaseListInfo, String msg) {

            }

            @Override
            public void onSearchGoodsList(String key, List<FoodInfoTable> goodsIdBaseListInfoList) {

            }
        });

        stv_page_back.setOnClickListener(this);
        stv_page_next.setOnClickListener(this);
        slidingTablayoutt = (RoundTabLayout) findViewById(R.id.sliding_tablayoutt);
        rv_goods_storage_list = (RecyclerView) findViewById(R.id.rv_goods_storage_list);
        rv_goods_storage_list.setItemAnimator(null);
        rv_goods_storage_list.setItemViewCacheSize(12);
        rv_goods_storage_list.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        rv_goods_storage_list.setLayoutManager(layoutManager);

        foodGridAdapter = new FoodGridAdapter(getActivity());
        rv_goods_storage_list.setAdapter(foodGridAdapter);
        rv_goods_storage_list.addItemDecoration(new GridSpacingItemDecoration(4, 20, false));
        rv_goods_storage_list.setClipToPadding(false);
        rv_goods_storage_list.setClipChildren(false);
//        rv_goods_storage_list.setVisibility(View.GONE);
//        rv_goods_storage_list_empty.setVisibility(View.VISIBLE);
        List<FoodInfoTable> foods = new ArrayList<>();
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foods.add(new FoodInfoTable());
        foodGridAdapter.getData().clear();
        foodGridAdapter.addData(foods);
        foodGridAdapter.notifyDataSetChanged();

        rl_coast_total.setOnClickListener(this);
        rl_server_addr.setOnClickListener(this);
        tv_add_food.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_restart_app.setOnClickListener(this);
        rl_face_value.setOnClickListener(this);
        rl_face_sync.setOnClickListener(this);
        tv_sync_foods.setOnClickListener(this);
        tv_app_version.setText("慧餐宝v" + BuildConfig.VERSION_NAME);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackEvent();
            }
        });

        switch_warning.setOnToggleChanged(new LimeSwitchButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                DeviceSettingMMKV.putOpenWarning(on);
            }
        });

        if (DeviceSettingMMKV.isOpenWarning()){
            switch_warning.toggleOn();
        }else {
            switch_warning.toggleOff();
        }

        initTab();
        foodCategory("2");
    }

    private void initTab(){

        if (JSON.toJSONString(mTitles).equals(JSON.toJSONString(beforeTitles))){
            return;
        }

        beforeTitles = mTitles;
        mFragments.clear();
        slidingTablayoutt.getTabs().clear();
        Log.d(TAG, "limemTitles: " + JSON.toJSONString(mTitles));
        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }
        ViewPager vp = (ViewPager) findViewById(R.id.vp);
        mAdapter = new MyPagerAdapter(getFragmentManager());
        vp.setAdapter(mAdapter);
        slidingTablayoutt.setupWithViewPager(vp);
        vp.setOffscreenPageLimit(3);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mTitles[position].equals("全部")){
                    queryTab = "";
                }else {
                    queryTab = mTitles[position];
                }
                queryName = goods_auto_search.getEtGoodsSearch().getText().toString().trim();
                pageNumberGlobal = 0;
                nextPage(pageNumberGlobal);
                slidingTablayoutt.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (position < (mAdapter.getCount() - 1)) {
                            slidingTablayoutt.onPageSelected(position);
                        }

                    }
                }, 100);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void onBackEvent(){

        if (dialogFragment != null && dialogFragment.isVisible()){
            dialogFragment.dismiss();
            return ;
        }

        if (ll_app_food_add.getVisibility() == View.VISIBLE ||ll_app_foods.getVisibility() == View.VISIBLE ||ll_app_coast.getVisibility() == View.VISIBLE || ll_app_settings.getVisibility() == View.VISIBLE || ll_app_face.getVisibility() == View.VISIBLE|| ll_app_weight.getVisibility() == View.VISIBLE|| ll_app_warning.getVisibility() == View.VISIBLE){
            ll_app_face.setVisibility(View.GONE);
            ll_app_settings.setVisibility(View.GONE);
            ll_app_weight.setVisibility(View.GONE);
            ll_app_warning.setVisibility(View.GONE);
            ll_app_coast.setVisibility(View.GONE);
            ll_app_foods.setVisibility(View.GONE);
            tv_sync_foods.setVisibility(View.GONE);
            ll_app_food_add.setVisibility(View.GONE);
            tv_sync_foods.setText("更新菜品");
            rvTopTab.setVisibility(View.VISIBLE);
            tv_title_name.setText("后台管理");
        }else {
            EventBus.getDefault().post(new BindFragmentSwitchEvent(0));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindFragmentBackEvent(BindFragmentBackEvent eventBus) {
        onBackEvent();
    }


    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            EventBus.getDefault().register(this);
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }







    /**
     * 统一处理删除人脸库成功和失败
     */
    private void handleDeleteAllFacePass(boolean needRequestAllFace) {
        refreshFacePassTotalCount(0);
    }




    private void initData() {
        Log.d(TAG, "limeTabBindSettingFragment: " + 48);
        //添加设置tab
        List<SettingBindTabInfo> settingTabInfoList = new ArrayList<>();
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_FOODS, SettingBindTabInfo.TAB_TYPE_FOODS,R.mipmap.ic_settings_foods));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_ID, SettingBindTabInfo.TAB_TYPE_ID,R.mipmap.ic_settings_id));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_WEIGHT, SettingBindTabInfo.TAB_TYPE_WEIGHT,R.mipmap.ic_settings_weight));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_COAST, SettingBindTabInfo.TAB_TYPE_COAST,R.mipmap.ic_settings_coast));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_WARNING, SettingBindTabInfo.TAB_TYPE_WARNING,R.mipmap.ic_settings_warning));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_SYSTEM, SettingBindTabInfo.TAB_TYPE_SYSTEM,R.mipmap.ic_settings_set));

        CommonRecyclerAdapter tabInfoAdapter = new CommonRecyclerAdapter(false);
        tabInfoAdapter.addViewHolderFactory(new SettingBindTabInfoViewHolder.Factory());

        tabInfoAdapter.addDataList(settingTabInfoList);
        rvTopTab.setAdapter(tabInfoAdapter);
        rvTopTab.setItemAnimator(null);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        rvTopTab.setLayoutManager(layoutManager);

        tabInfoAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                SettingBindTabInfo settingTabInfo = (SettingBindTabInfo) obj;

                if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_ID)){
                    CommonDialogUtils.showTipsBindDialog(getActivity(), "设备ID",DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(), "取消", new CommonBindSignleAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindSignleAlertDialogFragment alertDialogFragment) {

                        }
                    });
                }else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_SYSTEM)){
                    ll_app_settings.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("系统设置");
                } else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_WEIGHT)){
                    ll_app_weight.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("称重校准");
                }
                else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_WARNING)){
                    ll_app_warning.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("报警设置");
                } else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_COAST)){
                    ll_app_coast.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("消费设置");
                }else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_FOODS)){
                    ll_app_foods.setVisibility(View.VISIBLE);
                    tv_sync_foods.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("菜品设置");

//                    ToastUtil toastUtil2 = new ToastUtil(getActivity(), R.layout.toast_center_horizontal, "菜品更新成功");
//                    toastUtil2.show();
                }

            }
        });


    }


    private void refreshFacePassTotalCount(long count) {
        SpanUtils.with(tv_face_count)
                .append(String.valueOf(count))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append("人已入库")
                .create();
    }

    private void deleteAllFacePass() {
    }

    @Override
    public void needUpdateFacePass() {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rl_coast_total){
            BindingCoastAlertDialogFragment.build()
                    .setAlertTitleTxt("营业统计")
                    .setRightNavTxt("取消")
                    .show(getActivity());
        }else if (v.getId() == R.id.rl_version){

        }else if (v.getId() == R.id.rl_restart_app){
            dialogFragment = CommonBindAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("确定重启应用?")
                    .setLeftNavTxt("确认重启")
                    .setLeftNavClickListener(new CommonBindAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindAlertDialogFragment alertDialogFragment) {
                            DeviceManager.INSTANCE.getDeviceInterface().release();
                            AndroidUtils.restartApp();
                        }
                    }).setRightNavTxt("取消");
            dialogFragment.show(mActivity);
        }else if (v.getId() == R.id.rl_face_value){
            //暂停人脸识别功能
            EventBus.getDefault().post(new PauseFacePassDetect());
            dialogFragment = FacePassSettingBindAlertFragment.build();
            dialogFragment.show(mActivity);
        }else if (v.getId() == R.id.rl_face_sync){
            dialogFragment = CommonBindAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("确定清空人脸数据库吗")
                    .setLeftNavTxt("确定")
                    .setLeftNavClickListener(new CommonBindAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindAlertDialogFragment alertDialogFragment) {
                            deleteAllFacePass();
                        }
                    })
                    .setRightNavTxt("取消");
            dialogFragment.show(mActivity);
        }else if (v.getId() == R.id.tv_sync_foods){
            if(tv_sync_foods.getText().toString().trim().equals("保存")){
                saveGoodsStorage();
            }else {
                dialogFragment = CommonBindAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("全量更新将删除本地数据库?")
                        .setLeftNavTxt("确认重启")
                        .setLeftNavClickListener(new CommonBindAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonBindAlertDialogFragment alertDialogFragment) {
                                foodSync(0);
                            }
                        }).setRightNavTxt("取消");
                dialogFragment.show(mActivity);
            }


        }else if (v.getId() == R.id.tv_add_food){

            ll_app_foods.setVisibility(View.GONE);
            tv_sync_foods.setVisibility(View.VISIBLE);
            ll_app_food_add.setVisibility(View.VISIBLE);
            rvTopTab.setVisibility(View.GONE);
            tv_title_name.setText("新增菜品");
            tv_sync_foods.setText("保存");

        }else if (v.getId() == R.id.stv_page_back){
            if (pageNumberGlobal < 0){
                return;
            }
            pageNumberGlobal = pageNumberGlobal - 1;
            if (pageNumberGlobal <= 0){
                ToastUtils.toastMsgError("当前为菜品首页");
            }
            nextPage(pageNumberGlobal);
        }else if (v.getId() == R.id.stv_page_next){
            if (pageNumberGlobal > (totalPages - 1)){
                return;
            }
            pageNumberGlobal = pageNumberGlobal + 1;
            Log.d(TAG, "limepageNumberGlobal pageNumberGlobal: " + pageNumberGlobal + "   totalPages: " + totalPages);
            if (pageNumberGlobal >= (totalPages - 1)){
                ToastUtils.toastMsgError("当前为菜品最后一页");
            }
            nextPage(pageNumberGlobal);
        }


    }


    /**
     * 保存商品入库
     */
    @SuppressLint("AutoDispose")
    public void saveGoodsStorage() {
        //菜品/套餐名称
        String goodsName = goodsDetailLay.getGoodsName();
        if (TextUtils.isEmpty(goodsName)) {
            ToastUtils.toastMsgError("菜品名称不能为空!");
            return;
        }

        if (!goodsName.matches("[\\u3400-\\u4DBF\\u4E00-\\u9FFF]*")) {
            ToastUtils.toastMsgError("菜品名称只能为二十位以内汉字");
            return;
        }
        //计价方式
        String goodsPriceType = goodsDetailLay.getStv_goods_jjfs().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceType)) {
            ToastUtils.toastMsgError("请选择计价方式!");
            return;
        }
        //计价规格
        String goodsPriceGuiGe = goodsDetailLay.getStv_goods_jjgg().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceGuiGe)) {
            ToastUtils.toastMsgError("请选择计价规格!");
            return;
        }
        //单价
        String goodsPriceUnit = goodsDetailLay.getSet_goods_qrcode().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceUnit)) {
            ToastUtils.toastMsgError("单价不能为空!");
            return;
        }

        //分类
        String goodsPriceTab = goodsDetailLay.getGoods_fl_summary().getText().toString().trim();
        if (TextUtils.isEmpty(goodsPriceTab)) {
            ToastUtils.toastMsgError("请选择分类!");
            return;
        }


        //类型
        String goodsType = goodsDetailLay.getGoods_lx_summary().getText().toString().trim();
        if (TextUtils.isEmpty(goodsType)) {
            ToastUtils.toastMsgError("请选择类型!");
            return;
        }


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


        DeviceFoodTemplateParam deviceFoodTemplateParam = new DeviceFoodTemplateParam(PayConstants.createOrderNumber(),goodsName,goodsType.equals("单品") ? 1: 2,categoryMap,DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                0,goodsPriceType.equals("按份") ? 1 : 2,goodsPriceType.equals("按份")?"份":"1kg",null,Double.parseDouble(goodsPriceUnit),"0",null,null,null);

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
                            ToastUtils.toastMsgError("保存成功!");
                            List<FoodBean> foodInfoList = new ArrayList<>();
                            foodInfoList.add(foodSave.getData());
                            Log.d(TAG, "limeFoodSave: " + JSON.toJSONString(foodSave.getData()));
                            addFacePassToLocal(foodInfoList);

                        }else {
                            EventBus.getDefault().post(new TTSSpeakEvent(foodSave.getMsg()));
                            ToastUtils.toastMsgError(foodSave.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        ToastUtils.toastMsgError("保存失败!");

                    }
                });


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
                            ToastUtils.toastMsgError(baseNetResponse.getMessage());
                        }



                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.toastMsgError(e.getMessage());
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
                        ToastUtils.toastMsgSuccess("菜品更新成功");
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


    @Override
    public void onCheckVersionEnd(String msg) {

    }

    @Override
    public void onCheckVersionStart() {

    }

    @Override
    public void onCheckVersionError(String msg) {

    }

    @Override
    public void onNoVersionUpgrade() {

    }

    /**
     * 菜品分类
     */
    @SuppressLint("AutoDispose")
    public void foodCategory(String pricingMethod) {

        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodCategory");
        paramsMap.put("pricingMethod", pricingMethod);
        paramsMap.put("pageIndex", String.valueOf(0));
        paramsMap.put("pageSize", String.valueOf(100));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodCategory(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<FoodCategoryListInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodCategoryListInfo> baseNetResponse) {
                        if (baseNetResponse != null && baseNetResponse.getData()  != null && baseNetResponse.getData().getRecords() != null && !baseNetResponse.getData().getRecords().isEmpty()) {
                            List<String> tabList = new ArrayList<>();
                            List<CategoryBean> categroyList = baseNetResponse.getData().getRecords();
                            //categroyList.sort(new CategoryComparator());
                            for (int i = 0; i < categroyList.size(); i++) {
                                tabList.add(categroyList.get(i).getName());
                            }
                            tabList.add(0, "全部");
                            mTitles = tabList.toArray(new String[tabList.size()]);
                            if (pricingMethod.equals("2")){
                                PaymentSettingMMKV.putTab1(JSON.toJSONString(mTitles));
                                PaymentSettingMMKV.putTab1List(JSON.toJSONString(categroyList));
                            }else {
                                PaymentSettingMMKV.putTab0(JSON.toJSONString(mTitles));
                                PaymentSettingMMKV.putTab0List(JSON.toJSONString(categroyList));
                            }

                            initTab();
                        } else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void refreshPageCount(long currentPage,long totalPages) {
        SpanUtils.with(tv_total_count)
                .append(String.valueOf(currentPage))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append(" /" + totalPages)
                .create();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    public void nextPage(int pageNumber) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "limenextPage refreshFourPageData: " + 355);
                //totalCount = foodInfoTableDao.queryBuilder().where(FoodInfoTableDao.Properties.Status.eq(1)).count();
                QueryBuilder<FoodInfoTable>  qbCount   = foodInfoTableDao.queryBuilder();
                qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
                qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));

                if (!TextUtils.isEmpty(queryPricingMethod)) {
                    qbCount.where(FoodInfoTableDao.Properties.PricingMethod.eq(queryPricingMethod));
                }

                if (!TextUtils.isEmpty(queryName)) {
                    qbCount.where(FoodInfoTableDao.Properties.Name.like("%" + queryName + "%"));
                }

                if (!TextUtils.isEmpty(queryTab)) {
                    qbCount.where(FoodInfoTableDao.Properties.CategoryMap.like("%\"" + queryTab + "\"%"));
                }


//                for (String tab:mTitles){
//                    if (!TextUtils.isEmpty(queryTab) && !tab.equals(queryTab) && tab.contains(queryTab)){
//                        if (!tab.equals("全部")) {
//                            qbCount.where(new WhereCondition.StringCondition(
//                                    FoodInfoTableDao.Properties.CategoryMap.columnName + " LIKE ? AND " +
//                                            FoodInfoTableDao.Properties.CategoryMap.columnName + " NOT LIKE ?",
//                                    queryTab, tab
//                            ));
//                        }
//                    }
//                }

//                qbCount.orderDesc(FoodInfoTableDao.Properties.CreateTime);
                totalCount = qbCount.count();
                Log.d(TAG, "limenextPage refreshFourPageData: " + 359   + "    totalCount: " + totalCount);
                totalPages = (int)ceil((double)totalCount/ pageSize);
                if (totalPages == 0){
                    totalPages = 1;
                }
                offset = pageNumber * pageSize;
                Log.w(TAG, "limenextPage refreshFourPageData pageNumber: " + pageNumber);
                Log.w(TAG, "limenextPage refreshFourPageData totalPages: " + totalPages);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_total_count.setText("共 " + totalCount + " 件");
                        int currentPage = pageNumber + 1;
                        refreshPageCount(currentPage,totalPages);

                        if (pageNumber == 0 && totalCount == 0){
                            rv_goods_storage_list.setVisibility(View.GONE);
                            rv_goods_storage_list_empty.setVisibility(View.VISIBLE);
                        }else {
                            rv_goods_storage_list.setVisibility(View.VISIBLE);
                            rv_goods_storage_list_empty.setVisibility(View.GONE);
                        }

                        if (totalPages == 1){
                            stv_page_back.setEnabled(false);
                            stv_page_back.setTextColor(getActivity().getColor(R.color.color_999999));
                            stv_page_back.setSolidColor(getActivity().getColor(R.color.color_E3E9F5));
                            stv_page_next.setEnabled(false);
                            stv_page_next.setTextColor(getActivity().getColor(R.color.color_999999));
                            stv_page_next.setSolidColor(getActivity().getColor(R.color.color_E3E9F5));
                        } else {
                            if (currentPage == totalPages) {
                                stv_page_back.setEnabled(true);
                                stv_page_back.setTextColor(getActivity().getColor(com.stkj.common.R.color.color_333333));
                                stv_page_back.setSolidColor(getActivity().getColor(R.color.white));
                                stv_page_next.setEnabled(false);
                                stv_page_next.setTextColor(getActivity().getColor(R.color.color_999999));
                                stv_page_next.setSolidColor(getActivity().getColor(R.color.color_E3E9F5));

                            } else if (currentPage == 1) {
                                stv_page_back.setEnabled(false);
                                stv_page_back.setTextColor(getActivity().getColor(R.color.color_999999));
                                stv_page_back.setSolidColor(getActivity().getColor(R.color.color_E3E9F5));
                                stv_page_next.setEnabled(true);
                                stv_page_next.setTextColor(getActivity().getColor(com.stkj.common.R.color.color_333333));
                                stv_page_next.setSolidColor(getActivity().getColor(R.color.white));

                            } else if (currentPage < totalPages) {
                                stv_page_back.setEnabled(true);
                                stv_page_back.setTextColor(getActivity().getColor(com.stkj.common.R.color.color_333333));
                                stv_page_back.setSolidColor(getActivity().getColor(R.color.white));
                                stv_page_next.setEnabled(true);
                                stv_page_next.setTextColor(getActivity().getColor(com.stkj.common.R.color.color_333333));
                                stv_page_next.setSolidColor(getActivity().getColor(R.color.white));

                            }

                        }

                        Log.d(TAG, "limenextPage refreshFourPageData: " + 367);
                        foodGridAdapter.notifyDataSetChanged();
                        Log.d(TAG, "limenextPage refreshFourPageData: " + 369);
                    }
                });

                if (totalCount == 0L){
                    return;
                }
                Log.d(TAG, "limenextPage refreshFourPageData: " + 376 + "    offset: " + offset);
                if (offset < totalCount) {

//                QueryBuilder<FoodInfoTable>  qb   = foodInfoTableDao.queryBuilder();
//                qb.where(FoodInfoTableDao.Properties.Status.eq(1));
//                qb.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
//                if (!TextUtils.isEmpty(queryPricingMethod)) {
//                    qb.where(FoodInfoTableDao.Properties.PricingMethod.eq(queryPricingMethod));
//                 }
//
//               if (!TextUtils.isEmpty(queryName)) {
//                   qb.where(FoodInfoTableDao.Properties.Name.like("%" + queryName + "%"));
//                }
//
//                if (!TextUtils.isEmpty(queryTab)) {
//                    qb.where(FoodInfoTableDao.Properties.CategoryMap.like("%" + queryTab + "%"));
//                }

                    qbCount.orderAsc(FoodInfoTableDao.Properties.Sort);
                    qbCount.orderDesc(FoodInfoTableDao.Properties.CreateTime);
                    qbCount.offset(offset); // 跳过的记录数
                    qbCount.limit(pageSize); // 限制返回的记录数
                    // 执行查询并返回结果列

                    List<FoodInfoTable>  foods   =  qbCount.list();
                    Log.d(TAG, "limeRefreshUpdateGoodsEvent 935: " + JSON.toJSONString(foods));
                    Log.i(TAG, "limenextPage refreshFourPageData  384  foods.size(): " + foods.size());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (foods != null) {
                                Log.i(TAG, "limenextPage refreshFourPageData: " + 389);
                                foodGridAdapter.getData().clear();
                                foodGridAdapter.addData(foods);
                                foodGridAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, "limenextPage totalCount: " + totalCount + "    offset: " + offset + "    totalPages: " + totalPages + "    pageNumber: " + pageNumber);
                        }
                    });
                }
            }
        });
    }
}
