package com.stkj.cashier.pay.ui.fragment;

import static java.lang.Math.ceil;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.cashier.base.greendao.GreenDBConstants;
import com.stkj.cashier.base.greendao.generate.DaoMaster;
import com.stkj.cashier.base.greendao.generate.DaoSession;
import com.stkj.cashier.base.greendao.generate.FoodInfoTableDao;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.ui.dialog.CashierPayDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.goods.callback.GoodsAutoSearchListener;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.helper.PayHelper;
import com.stkj.cashier.pay.model.CategoryBean;
import com.stkj.cashier.pay.model.ChangeConsumerModeEvent;
import com.stkj.cashier.pay.model.ConsumeFoodInfo;
import com.stkj.cashier.pay.model.FacePassRetryEvent;
import com.stkj.cashier.pay.model.FoodCategoryListInfo;
import com.stkj.cashier.pay.model.GoodsClearAllEvent;
import com.stkj.cashier.pay.model.GoodsInventoryListInfoViewHolder;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.model.RefreshConsumerGoodsEvent;
import com.stkj.cashier.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.cashier.pay.model.RefreshGoodsModeEvent;
import com.stkj.cashier.pay.model.RefreshUpdateGoodsEvent;
import com.stkj.cashier.pay.model.TabEntity;
import com.stkj.cashier.pay.model.UpdateBlanceEvent;
import com.stkj.cashier.pay.service.PayService;
import com.stkj.cashier.pay.ui.adapter.FoodGridAdapter;
import com.stkj.cashier.pay.ui.weight.GoodsAutoSearchLayout;
import com.stkj.cashier.pay.ui.weight.GridSpacingItemDecoration;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.tabs.RoundTabLayout;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.deviceinterface.callback.OnReadWeightListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 商品模式
 */
public class GoodsConsumerFragment extends BasePayHelperFragment implements OnPayListener, OnConsumerConfirmListener,View.OnClickListener {

//    private SimpleCalculator scCalc;
    private RoundTabLayout slidingTablayoutt;
    private CommonTabLayout titleTab;
    private MyPagerAdapter mAdapter;
    private TextView tv_total_count;
    private TextView tv_goods_total_count;
    private TextView tv_page_index;
    private TextView tv_shi_shou;
    private TextView tv_ying_shou;
    private EditText et_kg;
    private EditText et_yuan;
    private ShapeTextView stv_page_back;
    private ShapeTextView stv_page_next;
    private ShapeTextView stv_query;
    private ShapeTextView stv_clear_all;
    private ShapeTextView stv_reset;
    private ShapeLinearLayout pay_money;
    private ShapeTextView stv_xjjs;
    private ShapeTextView stv_card;
    private ShapeTextView stv_qrcode;
    private LinearLayout rv_goods_storage_list_empty;
    private LinearLayout ll_kg_price;
    private LinearLayout ll_inventory_list_empty;
    private CommonRecyclerAdapter goodsInventoryAdapter;
    private FoodGridAdapter foodGridAdapter;
    private ShapeLinearLayout addGoodsLayout;
    private GoodsAutoSearchLayout goods_auto_search;
    private RecyclerView rv_goods_storage_list;
    private RecyclerView rv_goods_inventory_list;
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    private CashierPayDialogFragment cashierPayDialogFragment;
    private String[] mTitles = {"全部"};
    private String[] beforeTitles = {"全部"};
    public static List<FoodInfoTable> foods = new ArrayList<>();

    private final String[] tabTitles = {"按份商品", "称重商品"};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private int pageNumberGlobal = 0;
    private int totalPages = 0;
    private long totalCount = 0;
    private int pageSize = 12;
    private int offset = 0;
    public static String queryPricingMethod =  "1";
    private String queryName = "";
    public static String queryTab = "";
    private boolean forbidRefreshPrice;
    private int mCurrentAutoWeightCount;
    private String mLastWeightCount;
    private Handler handler = new Handler();
    public static  boolean canDelete = true;
    private double unitPrice = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //stopWeightPay();
            forbidRefreshPrice = false;
            startWeight();

            Log.d(TAG, "limerunnable : " + 161);
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_cunsumer;
    }

    @Override
    protected void initViews(View rootView) {
        EventBusUtils.registerEventBus(this);
        tv_total_count = (TextView) findViewById(R.id.tv_total_count);
        tv_page_index= (TextView) findViewById(R.id.tv_page_index);
        stv_page_back = (ShapeTextView) findViewById(R.id.stv_page_back);
        stv_page_next = (ShapeTextView) findViewById(R.id.stv_page_next);
        tv_goods_total_count = (TextView) findViewById(R.id.tv_goods_total_count);
        stv_query = (ShapeTextView) findViewById(R.id.stv_query);
        stv_reset = (ShapeTextView) findViewById(R.id.stv_reset);
        stv_card = (ShapeTextView) findViewById(R.id.stv_card);
        ll_kg_price = (LinearLayout) findViewById(R.id.ll_kg_price);
        stv_qrcode = (ShapeTextView) findViewById(R.id.stv_qrcode);
        et_kg = (EditText) findViewById(R.id.et_kg);
        et_yuan = (EditText) findViewById(R.id.et_price);
        stv_card.setOnClickListener(this);
        stv_qrcode.setOnClickListener(this);
        ll_inventory_list_empty = (LinearLayout) findViewById(R.id.ll_inventory_list_empty);
        tv_shi_shou = (TextView) findViewById(R.id.tv_shi_shou);
        tv_ying_shou = (TextView) findViewById(R.id.tv_ying_shou);
        stv_clear_all = (ShapeTextView) findViewById(R.id.stv_clear_all);
        stv_clear_all.setOnClickListener(this);
        rv_goods_storage_list_empty = (LinearLayout) findViewById(R.id.rv_goods_storage_list_empty);
        stv_query.setOnClickListener(this);
        stv_reset.setOnClickListener(this);
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
        rv_goods_inventory_list = (RecyclerView) findViewById(R.id.rv_goods_inventory_list);
        rv_goods_storage_list.setItemAnimator(null);
        rv_goods_storage_list.setItemViewCacheSize(12);
        rv_goods_storage_list.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rv_goods_storage_list.setLayoutManager(layoutManager);

        foodGridAdapter = new FoodGridAdapter(getActivity());
        rv_goods_storage_list.setAdapter(foodGridAdapter);
        rv_goods_storage_list.addItemDecoration(new GridSpacingItemDecoration(3, 8, false));
        rv_goods_storage_list.setClipToPadding(false);
        rv_goods_storage_list.setClipChildren(false);



        goodsInventoryAdapter = new CommonRecyclerAdapter(false);
        goodsInventoryAdapter.addViewHolderFactory(new GoodsInventoryListInfoViewHolder.Factory());
        rv_goods_inventory_list.setAdapter(goodsInventoryAdapter);

        rv_goods_inventory_list.setItemViewCacheSize(12);
        rv_goods_inventory_list.setHasFixedSize(true);

        rv_goods_inventory_list.setVisibility(View.GONE);
        ll_inventory_list_empty.setVisibility(View.VISIBLE);



        foodGridAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

                if (stv_xjjs.getText().toString().equals("取消结算")|| stv_card.getText().toString().equals("取消结算") || stv_qrcode.getText().toString().equals("取消结算")){
                    AppToast.toastMsg("请先取消结算");
                    speakTTSVoice("请先取消结算");
                    return;
                }

                if (queryPricingMethod.equals("2") && !DeviceManager.INSTANCE.getDeviceInterface().isSupportReadWeight()){
                    AppToast.toastMsg("当前设备不支持称重商品");
                    speakTTSVoice("当前设备不支持称重商品");
                    return;
                }


                if (!goodsInventoryAdapter.getDataList().isEmpty()){
                    for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
                        FoodInfoTable foodInfoTable = ((FoodInfoTable)(goodsInventoryAdapter.getDataList().get(i)));
                        if (foodInfoTable.getId().equals("-1")){
                            AppToast.toastMsg("请先完成待支付账单");
                            speakTTSVoice("请先完成待支付账单");
                            return;

                        }
                        if (foodInfoTable.getId().equals(foodGridAdapter.getItem(position).getId())){
                            if (queryPricingMethod.equals("2")){
                                AppToast.toastMsg("已添加该菜品");
                                speakTTSVoice("已添加该菜品");
                                return;
                            }
                            double newInputGoodsCount = foodInfoTable.isWeightGoods() ? BigDecimalUtils.add(foodInfoTable.getWeightGoodsCountWithDouble(), 1) : foodInfoTable.getStandardGoodsCountWithInt() + 1;
                            if (newInputGoodsCount >= 1) {
                                if (newInputGoodsCount >= 9999) {
                                    newInputGoodsCount = 9999;
                                }
                            }
                            String newCountStr;
                            if (foodInfoTable.isWeightGoods()) {
                                newCountStr = String.valueOf(newInputGoodsCount);
                                ((FoodInfoTable)(goodsInventoryAdapter.getDataList().get(i))).setWeightGoodsCount(newCountStr);
                            } else {
                                newCountStr = String.valueOf((int) newInputGoodsCount);
                                ((FoodInfoTable)(goodsInventoryAdapter.getDataList().get(i))).setStandardGoodsCount(newCountStr);
                            }
                            goodsInventoryAdapter.notifyDataSetChanged();
//                            tvGoodsCount.setText(newCountStr);
//                            refreshTotalPrice();
                            refreshTotalPrice();
                            return;
                        }

                    }
                }
                if (queryPricingMethod.equals("2")){
                    if (Double.parseDouble(getKg()) > 0){
                        foodGridAdapter.getItem(position).setWeightGoodsCount(getKg());
                        unitPrice =  foodGridAdapter.getItem(position).getUnitPriceMoney_amount();

                        et_yuan.setText(PriceUtils.formatPrice(Double.parseDouble(getKg()) * unitPrice));

                        Log.d(TAG, "limeet_yuan: " + 322);
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 3000);

                    }else {
                        speakTTSVoice("请将菜品放至秤上");
                        AppToast.toastMsg("请将菜品放至秤上");
                        return;
                    }


                }
                rv_goods_inventory_list.setVisibility(View.VISIBLE);
                ll_inventory_list_empty.setVisibility(View.GONE);
                foods.clear();
                if (queryPricingMethod.equals("2")){
                    foodGridAdapter.getItem(position).setWeightGoodsCount(getKg());
                    forbidRefreshPrice = false;
                }else {
                    foodGridAdapter.getItem(position).setStandardGoodsCount("1");
                }
                foods.add(foodGridAdapter.getItem(position));
                MainApplication.isQuick = 0;
                goodsInventoryAdapter.addDataList(foods);
                refreshTotalPrice();
            }
        });


        titleTab = (CommonTabLayout) findViewById(R.id.title_tab);
        addGoodsLayout = (ShapeLinearLayout) findViewById(R.id.sll_goods_add);
        addGoodsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stv_xjjs.getText().toString().equals("取消结算")|| stv_card.getText().toString().equals("取消结算") || stv_qrcode.getText().toString().equals("取消结算")){
                    AppToast.toastMsg("请先取消结算");
                    speakTTSVoice("请先取消结算");
                    return;
                }

                if (foodInfoTableDao == null) {
                    initFoodInfoTableDao();
                }

                QueryBuilder<FoodInfoTable>  qbCount   = foodInfoTableDao.queryBuilder();
                qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
                qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
                if (qbCount.count() == 0){
                    AppToast.toastMsg("请先同步菜品");
                    speakTTSVoice("请先同步菜品");
                    return;
                }
                EventBus.getDefault().post(new GoodsClearAllEvent());
                EventBus.getDefault().post(new RefreshConsumerGoodsModeEvent(1));
            }
        });

        mTitles = new String[]{"全部"};
        if (PaymentSettingMMKV.getTabIndex() == 0 && !PaymentSettingMMKV.getTab0().isEmpty()){
            mTitles = JSON.parseObject(PaymentSettingMMKV.getTab0(), String[].class);
        } else if (PaymentSettingMMKV.getTabIndex() == 1 && !PaymentSettingMMKV.getTab1().isEmpty()){
            mTitles = JSON.parseObject(PaymentSettingMMKV.getTab1(), String[].class);
        }
        initTab();

        for (int i = 0; i < tabTitles.length; i++) {
            mTabEntities.add(new TabEntity(tabTitles[i], 0, 0));
        }
        titleTab.setTabData(mTabEntities);
        titleTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i) {
                Log.d(TAG, "limeonTabSelect: " + i);
                goods_auto_search.getEtGoodsSearch().setText("");
                goods_auto_search.hideSearchGoodsList();
                titleTabSelectLogic(i);
            }

            @Override
            public void onTabReselect(int i) {

            }
        });
        pay_money = (ShapeLinearLayout) findViewById(R.id.pay_money);
        pay_money.setOnClickListener(this);
        stv_xjjs = (ShapeTextView) findViewById(R.id.stv_xjjs);
        stv_xjjs.setOnClickListener(this);
//        foodCategory(queryPricingMethod);

        initFoodInfoTableDao();
//        nextPage(0);
        titleTab.setCurrentTab(PaymentSettingMMKV.getTabIndex());
        titleTabSelectLogic(PaymentSettingMMKV.getTabIndex());

        et_kg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (text.isEmpty()) {
                    // 重量归零
                    if (MainApplication.hasWeight) {
                        MainApplication.hasWeight = false;
                        foodGridAdapter.notifyDataSetChanged();
                    }
                }else {
                    if (!MainApplication.hasWeight){
                        MainApplication.hasWeight = true;
                        foodGridAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void titleTabSelectLogic(int i) {
        PaymentSettingMMKV.putTabIndex(i);
        Log.d(TAG, "limenextPage titleTabSelectLogic: " + 396);
        queryTab = "";
        if (i == 1){
            queryPricingMethod =  "2";
            pageSize = 9;
//            pageSize = 12;
            ll_kg_price.setVisibility(View.VISIBLE);
            startWeight();
        }else {
            handler.removeCallbacks(runnable);
            queryPricingMethod =  "1";
            pageSize = 12;
            ll_kg_price.setVisibility(View.GONE);
        }
        Log.d(TAG, "limenextPage titleTabSelectLogic: " + 409);
        titleTab.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryName = goods_auto_search.getEtGoodsSearch().getText().toString().trim();
                foodGridAdapter.getData().clear();
                pageNumberGlobal = 0;
                mTitles = new String[]{"全部"};
                if (i == 0 && !PaymentSettingMMKV.getTab0().isEmpty()){
                    mTitles = JSON.parseObject(PaymentSettingMMKV.getTab0(), String[].class);
                }
                Log.d(TAG, "limenextPage titleTabSelectLogic: " + 420);
                if (i == 1 && !PaymentSettingMMKV.getTab1().isEmpty()){
                    mTitles = JSON.parseObject(PaymentSettingMMKV.getTab1(), String[].class);
                }

                if (foodInfoTableDao == null){
                    initFoodInfoTableDao();
                }
                Log.d(TAG, "limenextPage titleTabSelectLogic: " + 428);
                foodInfoTableDao.detachAll();
                initTab();
                foodCategory(queryPricingMethod);
                nextPage(pageNumberGlobal);
            }
        },200);
    }

    private void startWeight(){
        et_kg.setText("");
        et_yuan.setText("");
        Log.d(TAG, "limeet_kg: " + 397);
        Log.d(TAG, "limeet_yuan: " + 443);
        mCurrentAutoWeightCount = 0;
        mLastWeightCount = "";
        //speakTTSVoice("请将菜品放至秤上");
        DeviceManager.INSTANCE.getDeviceInterface().readWeight(onReadWeightListener);
    }


    public void refreshTotalPrice() {
        List<FoodInfoTable> foods = new ArrayList<>();
        for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
            FoodInfoTable foodInfoTable = ((FoodInfoTable) (goodsInventoryAdapter.getDataList().get(i)));
            foods.add(foodInfoTable);
        }

        EventBus.getDefault().post(new ChangeConsumerModeEvent(PayConstants.CONSUMER_GOODS_UPDATE_MODE,foods));

        double totalPrice = 0;
        int totalCount = 0;

        for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
            FoodInfoTable foodInfoTable = ((FoodInfoTable)(goodsInventoryAdapter.getDataList().get(i)));
                if (foodInfoTable.isWeightGoods()) {
                    totalCount += 1;
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getWeightGoodsCountWithDouble() * foodInfoTable.getUnitPriceMoney_amount()));
                } else {
                    totalCount += foodInfoTable.getStandardGoodsCountWithInt();
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getStandardGoodsCountWithInt() * foodInfoTable.getUnitPriceMoney_amount()));
                }
        }

        tv_goods_total_count.setText("共" + totalCount +"件商品");
        tv_ying_shou.setText("应收：￥ " + PriceUtils.formatPrice(totalPrice));
        tv_shi_shou.setText("￥ " + PriceUtils.formatPrice(totalPrice));
        if (totalCount <= 0){
            rv_goods_inventory_list.setVisibility(View.GONE);
            ll_inventory_list_empty.setVisibility(View.VISIBLE);
        }

    }

    private void initFoodInfoTableDao() {
        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.FACE_DB_NAME, null);
        Database database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        foodInfoTableDao = daoSession.getFoodInfoTableDao();
        foodInfoTableDao.detachAll();
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


    @Override
    public void onCancelCardNumber(String cardNumber) {
        super.onCancelCardNumber(cardNumber);
//        scCalc.setCalcEnable(true);
//        scCalc.setConfirmTxt("结算");
    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        super.onCancelFacePass(passPeopleInfo);
//        scCalc.setCalcEnable(true);
//        scCalc.setConfirmTxt("结算");
    }

    /**
     * 获取最终结算价格
     */
    private String getAmountRealPayMoney() {
        String currentInputText = tv_shi_shou.getText().toString().replace("￥","").trim();
        return PriceUtils.formatPrice(currentInputText);
    }


    private void showPayDialog(int currentMode) {
        if (cashierPayDialogFragment != null) {
            cashierPayDialogFragment.dismiss();
            cashierPayDialogFragment.onDestroy();
        }

        cashierPayDialogFragment = CashierPayDialogFragment.build(currentMode,  Double.parseDouble(getAmountRealPayMoney()))
                .setOnSelectListener(new CashierPayDialogFragment.OnSelectListener() {


                    @Override
                    public void onConfirmSelectItem(int currentMode, String payMoney) {
                        if (currentMode == CashierPayDialogFragment.MODE_QUICK){

                            if (TextUtils.isEmpty(payMoney)){
                                speakTTSVoice("请输入金额");
                                AppToast.toastMsg("请输入金额");
                                return;
                            }

                            if (Double.parseDouble(payMoney) <= 0.00){
                                speakTTSVoice("请输入正确金额");
                                AppToast.toastMsg("请输入正确金额");
                                return;
                            }

                            rv_goods_inventory_list.setVisibility(View.VISIBLE);
                            ll_inventory_list_empty.setVisibility(View.GONE);

                            foods.clear();
                            FoodInfoTable foodInfoTable = new FoodInfoTable();
                            foodInfoTable.setId("-1");
                            foodInfoTable.setName("快速结算");
                            foodInfoTable.setUnitPriceMoney_amount(Double.parseDouble(payMoney));
                            foodInfoTable.setType(1);
                            foodInfoTable.setPricingMethod(1);
                            foodInfoTable.setStandardGoodsCount("1");
                            MainApplication.isQuick = 1;
                            foods.add(foodInfoTable);
                            goodsInventoryAdapter.addDataList(foods);

                            refreshTotalPrice();

                            cashierPayDialogFragment.dismiss();


                        }else {

                            if (TextUtils.isEmpty(payMoney)){
                                speakTTSVoice("请输入收银额度");
                                AppToast.toastMsg("请输入收银额度");
                                return;
                            }

                            if (Double.parseDouble(payMoney) < Double.parseDouble(getAmountRealPayMoney())){
                                speakTTSVoice("收银金额不能小于商品价格");
                                AppToast.toastMsg("收银金额不能小于商品价格");
                                return;
                            }
                            cashierPayDialogFragment.dismiss();
                            MainApplication.consumeFoodInfoList  = new ArrayList<>();
                            for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
                                FoodInfoTable foodInfoTable = ((FoodInfoTable) (goodsInventoryAdapter.getDataList().get(i)));
                                double foodQuantity = foodInfoTable.getPricingMethod() == 1 ? foodInfoTable.getStandardGoodsCountWithInt():foodInfoTable.getWeightGoodsCountWithDouble();
                                ConsumeFoodInfo consumeFoodInfo = new ConsumeFoodInfo(foodInfoTable.getId(),foodInfoTable.getName(),foodInfoTable.getStandardGoodsCountWithInt(),
                                        foodInfoTable.getWeightGoodsCountWithDouble(),foodInfoTable.getPricingMethod(),foodInfoTable.getUnitPriceMoney_amount(),
                                        Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getUnitPriceMoney_amount() * foodQuantity)));
                                Log.d(TAG, "limeconsumeFoodInfo: " + JSON.toJSONString(consumeFoodInfo));
                                MainApplication.consumeFoodInfoList.add(consumeFoodInfo);
                            }
                            CBGFacePassHandlerHelper.imageCache = null;
                            PayHelper payHelper = getPayHelper();
                            payHelper.setOnPayListener(GoodsConsumerFragment.this);
                            double totalPrice = 0;
                            int totalCount = 0;

                            for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
                                FoodInfoTable foodInfoTable = ((FoodInfoTable)(goodsInventoryAdapter.getDataList().get(i)));
                                if (foodInfoTable.isWeightGoods()) {
                                    totalCount += 1;
                                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getWeightGoodsCountWithDouble() * foodInfoTable.getUnitPriceMoney_amount()));
                                } else {
                                    totalCount += foodInfoTable.getStandardGoodsCountWithInt();
                                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getStandardGoodsCountWithInt() * foodInfoTable.getUnitPriceMoney_amount()));
                                }
                            }
                            MainApplication.isJuHePay = false;
                            payHelper.goToPay(PayConstants.PAY_TYPE_CASH, getDeductionType(), PriceUtils.formatPrice(totalPrice), "");
                        }
                    }

                    @Override
                    public void onDismiss() {
                        Log.d(TAG, "limestopAmountPay: " + currentMode);
                        if (currentMode == CashierPayDialogFragment.MODE_QUICK){






                        }else {
                            if (stv_xjjs.getText().equals("取消结算")) {
                                canDelete = true;
                                stv_xjjs.setText("现金结算");
                                setCalcEnable(stv_xjjs, true);
                                Log.d(TAG, "limestopAmountPay: " + 612);
                                stopAmountPay();
                            }
                        }

                    }
                });
        cashierPayDialogFragment.show(getActivity());
    }


    /**
     * 金额结算
     */
    private void goToAmountPay() {
        MainApplication.consumeFoodInfoList  = new ArrayList<>();
        for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
            FoodInfoTable foodInfoTable = ((FoodInfoTable) (goodsInventoryAdapter.getDataList().get(i)));
            double foodQuantity = foodInfoTable.getPricingMethod() == 1 ? foodInfoTable.getStandardGoodsCountWithInt():foodInfoTable.getWeightGoodsCountWithDouble();
            ConsumeFoodInfo consumeFoodInfo = new ConsumeFoodInfo(foodInfoTable.getId(),foodInfoTable.getName(),foodInfoTable.getStandardGoodsCountWithInt(),
                    foodInfoTable.getWeightGoodsCountWithDouble(),foodInfoTable.getPricingMethod(),foodInfoTable.getUnitPriceMoney_amount(),
                    Double.parseDouble(PriceUtils.formatPrice(foodInfoTable.getUnitPriceMoney_amount() * foodQuantity)));
            Log.i(TAG, "limeconsumeFoodInfo: " + JSON.toJSONString(consumeFoodInfo));
            MainApplication.consumeFoodInfoList.add(consumeFoodInfo);
        }
        CBGFacePassHandlerHelper.imageCache = null;
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        String realPayMoney = getAmountRealPayMoney();
        int goToPay = goToPay(realPayMoney);
        if (goToPay == NORMAL_TO_PAY) {
//            scCalc.setCalcEnable(false);
//            scCalc.setConfirmTxt("取消结算");

        } else {
            CommonDialogUtils.showTipsDialog(mActivity, getGoToPayStatus(goToPay));
        }
    }

    /**
     * 取消金额结算
     */
    private void stopAmountPay() {

        resetButtonState();
        MainApplication.isNeedCache = false;
        speakTTSVoice("取消结算");
        stopToPay();
//        scCalc.setCalcEnable(true);
//        scCalc.setConfirmTxt("结算");
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
//        if (isFirstOnResume) {
//            EventBusUtils.registerEventBus(this);
//            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new ConsumerRecordListFragment(), R.id.fl_consumer_list_content);
//        }
    }

    @Override
    public void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg) {
        super.onPayError(responseCode, payRequest, modifyBalanceResult, msg);
        resetButtonState();
//        scCalc.setCalcEnable(true);
//        scCalc.setConfirmTxt("结算");
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        super.onPaySuccess(payRequest, modifyBalanceResult);
        if (!TextUtils.isEmpty(modifyBalanceResult.getBalance())){
            EventBus.getDefault().post(new UpdateBlanceEvent(modifyBalanceResult.getBalance()));
        }
        String consumeSuccessVoice = TTSSettingMMKV.getConsumeSuccessVoice();
        if (!TextUtils.isEmpty(consumeSuccessVoice)){
            AppToast.toastMsg(consumeSuccessVoice);
        }
        resetButtonState();
        goodsInventoryAdapter.getDataList().clear();
        goodsInventoryAdapter.notifyDataSetChanged();
        tv_ying_shou.setText("应收：￥ 0.00");
        tv_shi_shou.setText("￥ 0.00");
        tv_goods_total_count.setText("共0件商品");
        rv_goods_inventory_list.setVisibility(View.GONE);
        ll_inventory_list_empty.setVisibility(View.VISIBLE);

        List<FoodInfoTable> foods = new ArrayList<>();
        for (int i = 0; i < goodsInventoryAdapter.getDataList().size(); i++) {
            FoodInfoTable foodInfoTable = ((FoodInfoTable) (goodsInventoryAdapter.getDataList().get(i)));
            foods.add(foodInfoTable);
        }

        EventBus.getDefault().post(new ChangeConsumerModeEvent(PayConstants.CONSUMER_GOODS_UPDATE_MODE,foods));

        if (queryPricingMethod.equals("2")){
            unitPrice = 0;
            forbidRefreshPrice = false;
            startWeight();
        }

    }

    @Override
    protected void onPayCancel(int payType) {
        Log.d(TAG, "limestopAmountPay: " + 716);
        stopAmountPay();
    }


    public void resetButtonState() {
        canDelete = true;
        if (stv_xjjs.getText().toString().equals("取消结算")) {
            stv_xjjs.setText("现金结算");
            setCalcEnable(stv_xjjs, true);
        }


        if (stv_card.getText().equals("取消结算")){
            stv_card.setText("餐卡结算");
            setCalcEnable(stv_card, true);
            Log.d(TAG, "limestopAmountPay: " + 720);
        }

        if (stv_qrcode.getText().toString().equals("取消结算")){
            stv_qrcode.setText("聚合结算");
            setCalcEnable(stv_qrcode, true);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerAmountModeEvent(RefreshConsumerGoodsEvent eventBus) {
        if (eventBus.getMode() == RefreshConsumerGoodsEvent.REFRESH_PRICE_MODE){
            refreshTotalPrice();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshGoodsModeEvent(RefreshGoodsModeEvent eventBus) {
        stopAmountPay();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUpdateGoodsEvent(RefreshUpdateGoodsEvent eventBus) {
        Log.d(TAG, "limeRefreshUpdateGoodsEvent: " + 652);
        pageNumberGlobal = 0;
        foodInfoTableDao.detachAll();
        nextPage(pageNumberGlobal);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //stopToPay();
        EventBusUtils.unRegisterEventBus(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pay_money) {
            if (goodsInventoryAdapter.getDataList().size() > 0) {
                AppToast.toastMsg("请先完成待支付账单");
                speakTTSVoice("请先完成待支付账单");
            }else {
                showPayDialog(CashierPayDialogFragment.MODE_QUICK);
            }

        }else if (v.getId() == R.id.stv_xjjs){
            if (goodsInventoryAdapter.getDataList().size() > 0) {

                if (stv_xjjs.getText().equals("取消结算")){
                    canDelete = true;
                    stv_xjjs.setText("现金结算");
                    setCalcEnable(stv_xjjs, true);
                    Log.d(TAG, "limestopAmountPay: " + 771);
                    stopAmountPay();
                }else {
                    if (stv_card.getText().toString().equals("取消结算") || stv_qrcode.getText().toString().equals("取消结算")){
                        AppToast.toastMsg("请先取消结算");
                        speakTTSVoice("请先取消结算");
                        return;
                    }
                    canDelete = false;
                    stv_xjjs.setText("取消结算");
                    setCalcEnable(stv_xjjs, false);
                    showPayDialog(CashierPayDialogFragment.MODE_CASH);
                }
            }else {
                AppToast.toastMsg("请先添加菜品");
                speakTTSVoice("请先添加菜品");
            }
        }else if (v.getId() == R.id.stv_page_back){
            if (pageNumberGlobal < 0){
                return;
            }
            pageNumberGlobal = pageNumberGlobal - 1;
            if (pageNumberGlobal <= 0){
                AppToast.toastMsg("当前为该分类首页");
            }
            nextPage(pageNumberGlobal);
        }else if (v.getId() == R.id.stv_page_next){
            if (pageNumberGlobal > (totalPages - 1)){
                return;
            }
            pageNumberGlobal = pageNumberGlobal + 1;
            Log.d(TAG, "limepageNumberGlobal pageNumberGlobal: " + pageNumberGlobal + "   totalPages: " + totalPages);
            if (pageNumberGlobal >= (totalPages - 1)){
                AppToast.toastMsg("当前为该分类最后一页");
            }
            nextPage(pageNumberGlobal);
        }else if (v.getId() == R.id.stv_query){
            if (TextUtils.isEmpty(goods_auto_search.getEtGoodsSearch().getText().toString().trim())){
                AppToast.toastMsg("请输入商品名称查询");
                speakTTSVoice("请输入商品名称查询");
            }else {
                queryName = goods_auto_search.getEtGoodsSearch().getText().toString().trim();
                foodGridAdapter.getData().clear();
                nextPage(pageNumberGlobal);
            }

        }else if (v.getId() == R.id.stv_reset){
            goods_auto_search.getEtGoodsSearch().setText("");
            queryName = "";
            foodGridAdapter.getData().clear();
            nextPage(pageNumberGlobal);
        }else if (v.getId() == R.id.stv_card){
            if (goodsInventoryAdapter.getDataList().size() > 0) {

                if (stv_card.getText().equals("取消结算")){
                    canDelete = true;
                    stv_card.setText("餐卡结算");
                    setCalcEnable(stv_card, true);
                    Log.d(TAG, "limestopAmountPay: " + 818);
                    stopAmountPay();
                }else {
                    if (stv_xjjs.getText().toString().equals("取消结算")|| stv_qrcode.getText().toString().equals("取消结算")){
                        AppToast.toastMsg("请先取消结算");
                        speakTTSVoice("请先取消结算");
                        return;
                    }
                    canDelete = false;
                    stv_card.setText("取消结算");
                    setCalcEnable(stv_card, false);
                    MainApplication.isJuHePay = false;
                    goToAmountPay();
                }

            }else {
                AppToast.toastMsg("请先添加菜品");
                speakTTSVoice("请先添加菜品");
            }

        }else if (v.getId() == R.id.stv_qrcode){
            if (goodsInventoryAdapter.getDataList().size() > 0) {
                if (stv_qrcode.getText().equals("取消结算")){
                    canDelete = true;
                    stv_qrcode.setText("聚合结算");
                    setCalcEnable(stv_qrcode, true);
                    Log.d(TAG, "limestopAmountPay: " + 837);
                    stopAmountPay();
                }else {
                    if (stv_xjjs.getText().toString().equals("取消结算")|| stv_card.getText().toString().equals("取消结算")){
                        AppToast.toastMsg("请先取消结算");
                        speakTTSVoice("请先取消结算");
                        return;
                    }
                    canDelete = false;
                    stv_qrcode.setText("取消结算");
                    setCalcEnable(stv_qrcode, false);
                    MainApplication.isJuHePay = true;
                    goToAmountPay();
                }


            }else {
                AppToast.toastMsg("请先添加菜品");
                speakTTSVoice("请先添加菜品");
            }

        }else if (v.getId() == R.id.stv_clear_all){

            if (goodsInventoryAdapter.getDataList().size() == 0){
                AppToast.toastMsg("购物车为空");
                return;
            }

            if (stv_xjjs.getText().toString().equals("取消结算")|| stv_card.getText().toString().equals("取消结算") || stv_qrcode.getText().toString().equals("取消结算")){
                AppToast.toastMsg("请先取消结算");
                speakTTSVoice("请先取消结算");
                return;
            }

            CommonAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("清空全部购物车商品?")
                    .setLeftNavTxt("确定")
                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            goodsInventoryAdapter.getDataList().clear();
                            goodsInventoryAdapter.notifyDataSetChanged();
                            tv_ying_shou.setText("应收：￥ 0.00");
                            tv_shi_shou.setText("￥ 0.00");
                            tv_goods_total_count.setText("共0件商品");
                            rv_goods_inventory_list.setVisibility(View.GONE);
                            ll_inventory_list_empty.setVisibility(View.VISIBLE);
                            EventBus.getDefault().post(new GoodsClearAllEvent());
                            stopToPay();
                        }
                    })
                    .setRightNavTxt("取消").show(mActivity);


        }
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
        SpanUtils.with(tv_page_index)
                .append(String.valueOf(currentPage))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append(" /" + totalPages)
                .create();
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

    private void stopWeightPay() {
        et_kg.setText("");
        Log.d(TAG, "limeet_kg: " + 924);
        et_yuan.setText("");
        Log.d(TAG, "limeet_yuan: " + 1161);
        resetWeightPrice();
        DeviceManager.INSTANCE.getDeviceInterface().unRegisterReadWeightListener(onReadWeightListener);
        //stopToPay();
        if (queryPricingMethod.equals("2")){
            unitPrice = 0;
            goToWeightPay("");
        }
    }

    /**
     * 重置称重价格（包含附加金额）
     */
    private void resetWeightPrice() {
        String weightAttachAmount = PaymentSettingMMKV.getWeightAttachAmount();
        double parseDouble = 0;
        try {
            parseDouble = Double.parseDouble(weightAttachAmount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        et_kg.setText("");
        if (parseDouble <= 0) {
            Log.d(TAG, "limeet_kg: " + 944);
            et_yuan.setText("");
            Log.d(TAG, "limeet_yuan: " + 1182);
        } else {
            if (weightAttachAmount.equals("0.00") || weightAttachAmount.equals("-0.00")){
                et_kg.setText("");
            }else {
                et_kg.setText(weightAttachAmount);
            }


            Log.d(TAG, "limeet_kg: " + 948);
        }
    }



    private String getKg(){
        if (TextUtils.isEmpty(et_kg.getText().toString().trim())){
            return "0";
        }
        return et_kg.getText().toString();
    }

    private String getWeightCount() {
        return et_kg.getText().toString();
    }

    /**
     * 称重结算
     */
    private void goToWeightPay(String voiceExtra) {
//        boolean checkWeightUnitPrice = checkWeightUnitPrice(mCurrentUnitWeightPrice);
//        if (checkWeightUnitPrice) {
//            if (!TextUtils.isEmpty(voiceExtra)) {
//                speakTTSVoice(voiceExtra + ",请将菜品放至秤上");
//            } else {
//                speakTTSVoice("请将菜品放至秤上");
//            }
//            ConsumerManager.INSTANCE.resetFaceConsumerLayout();
//            ConsumerManager.INSTANCE.setConsumerTips("请将菜品放至秤上");
            DeviceManager.INSTANCE.getDeviceInterface().readWeight(onReadWeightListener);
            forbidRefreshPrice = false;
            mLastWeightCount = "";
            mCurrentAutoWeightCount = 0;
//            setCalcEnable(true);
//        }
    }

    private OnReadWeightListener onReadWeightListener = new OnReadWeightListener() {
        @Override
        public void onReadWeightData(String weightCount, String unit) {
            Log.d(TAG, "limeonReadWeightData: " + weightCount);
//            if (forbidRefreshPrice) {
                //商品总量
//                boolean switchWeightAutoCancelPay = PaymentSettingMMKV.getSwitchWeightAutoCancelPay();
//                if (switchWeightAutoCancelPay) {
//                    String formatWeightCount = PriceUtils.formatPrice(weightCount);
//                    double parseWeightCount = 0;
//                    try {
//                        parseWeightCount = Double.parseDouble(formatWeightCount);
//                        if (parseWeightCount <= 0) {
//                            et_kg.setText("");
//                            et_yuan.setText("");
//                            //stopWeightPay();
//                            //goToWeightPay("取消结算");
//                            LogHelper.print("onReadWeightData-forbidRefreshPrice-formatWeightCount: 0");
//                            return;
//                        }
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                    }
//                }
//                return;
//            }

//            String formatWeightUnitPrice = mCurrentUnitWeightPrice;
//            if (TextUtils.isEmpty(formatWeightUnitPrice) || TextUtils.isEmpty(weightCount)) {
//                ConsumerManager.INSTANCE.setConsumerTips("请将菜品放至秤上");
//                et_kg.setText("0.00");
//                resetWeightPrice();
//                return;
//            }
            //商品单价
//            double parseWeightUnitPrice = 0;
//            try {
//                parseWeightUnitPrice = Double.parseDouble(formatWeightUnitPrice);
//                if (parseWeightUnitPrice <= 0) {
//                    ConsumerManager.INSTANCE.setConsumerTips("请将菜品放至秤上");
//                    setCalcEnable(true);
//                    tvWeight.setText("0.00");
//                    resetWeightPrice();
//                    LogHelper.print("onReadWeightData--formatWeightUnitPrice: 0");
//                    return;
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
            //商品总量
            String formatWeightCount = PriceUtils.formatPrice(weightCount);
//            double parseWeightCount = 0;
//            try {
//                parseWeightCount = Double.parseDouble(formatWeightCount);
//                if (parseWeightCount <= 0) {
////                    ConsumerManager.INSTANCE.setConsumerTips("请将菜品放至秤上");
//                    et_kg.setText("");
//                    Log.d(TAG, "limeet_kg: " + 1038);
//                    et_yuan.setText("");
//                    Log.d(TAG, "limeet_yuan: " + 1279);
//                    resetWeightPrice();
//                    LogHelper.print("onReadWeightData--formatWeightCount: 0");
//                    return;
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//            double parseWeightPrice = BigDecimalUtils.mul(parseWeightUnitPrice, parseWeightCount);
//            double weightAttachAmount = getWeightAttachAmount();
//            if (weightAttachAmount > 0) {
//                parseWeightPrice = BigDecimalUtils.add(parseWeightPrice, weightAttachAmount);
//            }
//            String weightPrice = PriceUtils.formatPrice(parseWeightPrice);
            if (formatWeightCount.equals("0.00") || formatWeightCount.equals("-0.00")){
                et_kg.setText("");
                et_yuan.setText("");
            }else {
                et_kg.setText(formatWeightCount);
                et_yuan.setText(PriceUtils.formatPrice(Double.parseDouble(formatWeightCount) * unitPrice));
            }


            if (Double.parseDouble(getKg()) <= 0 && unitPrice > 0){
                unitPrice = 0;
            }

            Log.d(TAG, "limeet_kg: " + 1310 + "  formatWeightCount: " + formatWeightCount);
            LogHelper.print("--onReadWeightData mLastWeightCount: " + mLastWeightCount + " | currentWeightCount: " + weightCount);
            //判读称重稳定次数
            if (TextUtils.equals(mLastWeightCount, weightCount)) {
                mCurrentAutoWeightCount++;
                int autoWeightCount = PaymentSettingMMKV.getAutoWeightCount();
                if (autoWeightCount > 0) {
                    int pro = (int) ((mCurrentAutoWeightCount * 1.0f / autoWeightCount) * 360);
//                    ConsumerManager.INSTANCE.setConsumerTips("计价中", pro);
                }
                //重量已经稳定
                if (mCurrentAutoWeightCount > autoWeightCount) {
                    forbidRefreshPrice = true;
                    //判断称重商品重量
                    String weightCountTemp = getWeightCount();
                    String formatWeightCountTemp = PriceUtils.formatPrice(weightCountTemp);
                    double parseWeightCountTemp = 0;
                    try {
                        parseWeightCountTemp = Double.parseDouble(formatWeightCountTemp);
                        if (parseWeightCountTemp <= 0) {
                            goToWeightPay("重量为零");
                            return;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        goToWeightPay("称重重量异常");
                        return;
                    }
                    //int goToPay = goToPay(getWeightRealPayMoney());
//                    if (goToPay == PAYING_TO_PAY) {
//                        speakTTSVoice("支付中,请稍等");
//                    } else if (goToPay == MONEY_ZERO_TO_PAY) {
//                        goToWeightPay("支付金额为零");
//                    }



                }
            } else {
                //setCalcWeight(0);
                mCurrentAutoWeightCount = 0;
                mLastWeightCount = weightCount;
            }
        }

        @Override
        public void onReadWeightError(String message) {
            et_kg.setText("");
            Log.d(TAG, "limeet_kg: " + 1103);
            et_yuan.setText("");
            Log.d(TAG, "limeet_yuan: " + 1344);
            resetWeightPrice();
        }
    };

    public void setCalcEnable(ShapeTextView tvConfirm,boolean calcEnable) {
        if (calcEnable) {
            tvConfirm.setTextColor(getActivity().getColor(R.color.selector_calc_item_confirm));
            tvConfirm.setSolidColor(getActivity().getColor(R.color.color_0087FA));
            tvConfirm.setStrokeColor(getActivity().getColor(R.color.color_0087FA));
        } else {
            tvConfirm.setTextColor(getActivity().getColor(R.color.black));
            tvConfirm.setSolidColor(getActivity().getColor(R.color.color_E3E9F5));
            tvConfirm.setStrokeColor(getActivity().getColor(R.color.color_E3E9F5));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleFaceRetry(FacePassRetryEvent eventBus) {
        ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(AppManager.INSTANCE.getMainActivity());
        int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
        if (currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE) {
            goToAmountPay();
        }
    }

}
