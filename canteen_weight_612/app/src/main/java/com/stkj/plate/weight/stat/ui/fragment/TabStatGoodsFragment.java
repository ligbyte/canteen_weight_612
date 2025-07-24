package com.stkj.plate.weight.stat.ui.fragment;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.plate.weight.MainApplication;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.model.CommonExpandItem;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.adapter.FoodListStatAdapter;
import com.stkj.plate.weight.base.ui.dialog.DatepickerDialogFragment;
import com.stkj.plate.weight.base.ui.dialog.OrderAlertDialogFragment;
import com.stkj.plate.weight.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.plate.weight.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.plate.weight.base.utils.CommonDialogUtils;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.pay.goods.callback.OrderNameAutoSearchListener;
import com.stkj.plate.weight.pay.goods.callback.OrderNoAutoSearchListener;
import com.stkj.plate.weight.pay.model.ConsumerOrderRefreshEvent;
import com.stkj.plate.weight.pay.model.ConsumerRecordListQueryInfo;
import com.stkj.plate.weight.pay.model.ConsumerSuccessEvent;
import com.stkj.plate.weight.pay.model.DeviceFoodBillStatPageParam;
import com.stkj.plate.weight.pay.model.DeviceFoodConsumePageParam;
import com.stkj.plate.weight.pay.model.FoodBillRecords;
import com.stkj.plate.weight.pay.model.FoodBillStatDayPageResponse;
import com.stkj.plate.weight.pay.model.FoodConsumeBean;
import com.stkj.plate.weight.pay.model.FoodConsumeDetailResponse;
import com.stkj.plate.weight.pay.model.FoodConsumePageResponse;
import com.stkj.plate.weight.pay.service.PayService;
import com.stkj.plate.weight.pay.ui.weight.OrderNameAutoSearchLayout;
import com.stkj.plate.weight.pay.ui.weight.OrderNoAutoSearchLayout;
import com.stkj.plate.weight.stat.ui.adapter.FoodOrderListAdapter;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.AutoScaleTextView;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.TimeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.TreeMap;

/**
 * 统计页面
 */
public class TabStatGoodsFragment extends BaseRecyclerFragment implements View.OnClickListener{

    public final static String TAG = "TabStatGoodsFragment";
    private AppSmartRefreshLayout srlRecordList;
    private AutoScaleTextView tv_ysze;
    private AutoScaleTextView tv_ssze;
    private AutoScaleTextView tv_ddzs;
    private AutoScaleTextView tv_spzs;
    private LinearLayout ll_order_empty;
    private LinearLayout ll_order_right_empty;
    private RecyclerView rvContent;
    private RecyclerView rv_content_right;
    private ShapeTextView stv_order_type;
    private ShapeTextView stv_query;
    private ShapeTextView stv_reset;
    private ShapeTextView stv_start_date;
    private ShapeTextView stv_end_date;
    private OrderNameAutoSearchLayout set_query_name;
    private OrderNoAutoSearchLayout set_query_order;
//    private BarChart chartConsumerNumber;
//    private TextView tvConsumerNumber;
//    private StatPieChart chartConsumerAmount;
    private FoodOrderListAdapter orderListAdapter;
    private FoodListStatAdapter foodListShowAdapter;
    //网络请求查询参数
    private ConsumerRecordListQueryInfo queryInfo = new ConsumerRecordListQueryInfo();
    private boolean needRefreshStatData;
    private String queryName = null;
    private String queryNameItem = null;
    private String queryOrderNO = null;
    private String queryOrderNOItem = null;
    private String queryStartTime = null;
    private String queryEndTime = null;
    private int queryBillType = 0;
    private int pageTotal = 1;
    private int pageIndex = 1;
    private int pageSize = 10;

    private int pageTotalRight = 1;
    private int pageIndexRight = 0;
    private int pageSizeRight = 10;
    private OrderAlertDialogFragment orderAlertDialogFragment;
    private DatepickerDialogFragment datepickerDialogFragment;


    private void resetRequestPage() {
        pageIndex = 1;
        pageTotal = 1;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_stat_goods;
    }

    @Override
    protected void initViews(View rootView) {
        srlRecordList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        rv_content_right = (RecyclerView) findViewById(R.id.rv_content_right);
        foodListShowAdapter = new FoodListStatAdapter(getActivity());
        foodListShowAdapter.setUseEmpty(true);
        foodListShowAdapter.setEmptyView(R.layout.layout_empty_view);
        rv_content_right.setAdapter(foodListShowAdapter);
        rv_content_right.setItemAnimator(null);
        rv_content_right.setItemViewCacheSize(12);
        rv_content_right.setHasFixedSize(true);
        set_query_name = (OrderNameAutoSearchLayout) findViewById(R.id.set_query_name);
        set_query_order = (OrderNoAutoSearchLayout) findViewById(R.id.set_query_order);
        stv_start_date = (ShapeTextView) findViewById(R.id.stv_start_date);
        stv_end_date = (ShapeTextView) findViewById(R.id.stv_end_date);
        stv_start_date.setOnClickListener(this);
        stv_end_date.setOnClickListener(this);
        stv_start_date.setText(TimeUtils.getLastDateFormatted());
        //stv_start_date.setText(TimeUtils.getCurrentDateFormatted());
        stv_end_date.setText(TimeUtils.getCurrentDateFormatted());
        stv_query = (ShapeTextView) findViewById(R.id.stv_query);
        stv_reset = (ShapeTextView) findViewById(R.id.stv_reset);
        stv_query.setOnClickListener(this);
        stv_reset.setOnClickListener(this);
         tv_ysze =  (AutoScaleTextView) findViewById(R.id.tv_ysze);
        tv_ssze = (AutoScaleTextView) findViewById(R.id.tv_ssze);
         tv_ddzs = (AutoScaleTextView) findViewById(R.id.tv_ddzs);
         tv_spzs = (AutoScaleTextView) findViewById(R.id.tv_spzs);
        stv_order_type = (ShapeTextView) findViewById(R.id.stv_order_type);
        ll_order_empty = (LinearLayout) findViewById(R.id.ll_order_empty);
        ll_order_right_empty = (LinearLayout) findViewById(R.id.ll_order_right_empty);

        set_query_name.setGoodsAutoSearchListener(this, new OrderNameAutoSearchListener() {


            @Override
            public void onStartGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo) {
                queryNameItem = goodsIdBaseListInfo.getCustomerName();
                pageIndex = 1;
                pageIndexRight = 0;
                set_query_name.getEtGoodsSearch().setText(goodsIdBaseListInfo.getCustomerName());
                getOrderList();
                foodBillStatDayPage();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(FoodConsumeBean saleListInfo) {

            }

            @Override
            public void onErrorGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo, String msg) {

            }

            @Override
            public void onSearchGoodsList(String key, List<FoodConsumeBean> goodsIdBaseListInfoList) {
                queryName = key;
                pageIndex = 1;
                pageIndexRight = 0;
                set_query_name.getEtGoodsSearch().setText(key);
                getOrderList();
                foodBillStatDayPage();
            }
        });


        set_query_name.getEtGoodsSearch().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                set_query_order.hideSearchGoodsList();
                return false;
            }
        });

        set_query_order.setGoodsAutoSearchListener(this, new OrderNoAutoSearchListener() {


            @Override
            public void onStartGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo) {
                Log.d(TAG, "limeset_query_order onStartGetGoodsItemDetail: ");
                queryOrderNOItem = TextUtils.isEmpty(goodsIdBaseListInfo.getBillNo()) ? goodsIdBaseListInfo.getOrderNo() : goodsIdBaseListInfo.getBillNo();
                pageIndex = 1;
                pageIndexRight = 0;
                set_query_order.getEtGoodsSearch().setText(queryOrderNOItem);
                getOrderList();
                foodBillStatDayPage();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(FoodConsumeBean saleListInfo) {
                Log.d(TAG, "limeset_query_order onSuccessGetGoodsItemDetail: ");
            }

            @Override
            public void onErrorGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo, String msg) {

            }

            @Override
            public void onSearchGoodsList(String key, List<FoodConsumeBean> goodsIdBaseListInfoList) {
                Log.d(TAG, "limeset_query_order onSearchGoodsList: ");
                queryOrderNO = key;
                pageIndex = 1;
                pageIndexRight = 0;
                set_query_order.getEtGoodsSearch().setText(key);
                getOrderList();
                foodBillStatDayPage();
            }
        });
        set_query_order.getEtGoodsSearch().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                set_query_name.hideSearchGoodsList();
                return false;
            }
        });

        List<CommonExpandItem> heartBeatExpandListlx = new ArrayList<>();
        heartBeatExpandListlx.add(new CommonExpandItem(0, "用户账单"));
        heartBeatExpandListlx.add(new CommonExpandItem(1, "三方账单"));
        stv_order_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(stv_order_type.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        showLoadingDialog();
                        stv_order_type.setText(commonExpandItem.getName());
                        queryBillType = commonExpandItem.getTypeInt();
                        set_query_name.setBillType(queryBillType);
                        set_query_order.setBillType(queryBillType);
                        set_query_name.hideSearchGoodsList();
                        set_query_order.hideSearchGoodsList();
                        pageIndex = 1;
                        pageTotal = 1;
                        pageIndexRight = 0;
                        pageTotalRight = 1;
                        getOrderList();
                        foodBillStatDayPage();
                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandListlx);
                commonExpandListPopWindow.showAsDropDown(stv_order_type);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });
//        List<FoodItemEntity> caiPinItemEntityList = new ArrayList<>();
//        caiPinItemEntityList.add(new FoodItemEntity());
//        caiPinItemEntityList.add(new FoodItemEntity());
//        caiPinItemEntityList.add(new FoodItemEntity());
//        caiPinItemEntityList.add(new FoodItemEntity());
//        caiPinItemEntityList.add(new FoodItemEntity());
//        caiPinItemEntityList.add(new FoodItemEntity());
//        foodListShowAdapter.setNewInstance(caiPinItemEntityList);
//        chartConsumerNumber = (BarChart) findViewById(R.id.chart_consumer_number);
//        tvConsumerNumber = (TextView) findViewById(R.id.tv_consumer_number);
//        chartConsumerAmount = (StatPieChart) findViewById(R.id.chart_consumer_amount);
        srlRecordList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                set_query_name.hideSearchGoodsList();
                set_query_order.hideSearchGoodsList();
                pageIndex += 1;
                pageIndexRight += 1;
                getOrderList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                set_query_name.hideSearchGoodsList();
                set_query_order.hideSearchGoodsList();
                pageIndex = 1;
                pageTotal = 1;
                pageIndexRight = 0;
                pageTotalRight = 1;
                resetRequestPage();
                queryInfo.resetDefaultData();
                getOrderList();
                foodBillStatDayPage();
            }
        });
        getOrderList();
        foodBillStatDayPage();
        orderListAdapter = new FoodOrderListAdapter(getActivity());

        orderListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                showLoadingDialog();
                getOrderDetail(orderListAdapter.getData().get(position).getId());
            }
        });

        orderListAdapter.setUseEmpty(true);
        orderListAdapter.setEmptyView(R.layout.layout_empty_view);
        rvContent.setAdapter(orderListAdapter);
        rvContent.setItemAnimator(null);
        rvContent.setItemViewCacheSize(12);
        rvContent.setHasFixedSize(true);
    }

    private boolean isOnPause;

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        isOnPause = false;
        EventBusUtils.registerEventBus(this);
        if (isFirstOnResume) {
            srlRecordList.autoRefresh();
        } else {
            if (needRefreshStatData) {
                needRefreshStatData = false;
                srlRecordList.autoRefresh();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerRecordList(ConsumerSuccessEvent eventBus) {
        LogHelper.print("--EventBusUtils-onRefreshConsumerRecordList");
        if (isOnPause) {
            needRefreshStatData = true;
        } else {
            srlRecordList.autoRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConsumerOrderRefreshEvent(ConsumerOrderRefreshEvent eventBus) {
        pageIndex = 1;
        pageTotal = 1;
        pageIndexRight = 0;
        pageTotalRight = 1;
        resetRequestPage();
        queryInfo.resetDefaultData();
        getOrderList();
        foodBillStatDayPage();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

    private void getOrderList() {
        Log.d(TAG, "limegetOrderList pageIndex: " + pageIndex);
        if (pageIndex > 1 && pageIndex  > pageTotal){
            hideLoadingDialog();
            srlRecordList.finishRefresh();
            srlRecordList.finishLoadMore();
            AppToast.toastMsg("没有更多数据了!");
            return;
        }

        queryName = set_query_name.getEtGoodsSearch().getText().toString().trim();
        if (TextUtils.isEmpty(queryName)){
            queryName = null;
        }

        if (queryNameItem != null){
            queryName = queryNameItem;
            queryNameItem = null;
        }

        queryOrderNO = set_query_order.getEtGoodsSearch().getText().toString().trim();
        if (TextUtils.isEmpty(queryOrderNO)){
            queryOrderNO = null;
        }


        if (queryOrderNOItem != null){
            queryOrderNO = queryOrderNOItem;
            queryOrderNOItem = null;
        }


        Log.d(TAG, "limefoodConsumePage: " + 279);
        queryStartTime = stv_start_date.getText().toString().trim();
        if (TextUtils.isEmpty(queryStartTime)){
            queryStartTime = null;
        }else {
            queryStartTime += " 00:00:00";
        }

        queryEndTime = stv_end_date.getText().toString().trim();
        if (TextUtils.isEmpty(queryEndTime)){
            queryEndTime = null;
        }else {
            queryEndTime += " 23:59:59";
        }
        Log.d(TAG, "limefoodConsumePage: " + 293);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodConsumePage");
        DeviceFoodConsumePageParam deviceFoodConsumePageParam = new DeviceFoodConsumePageParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                queryName,queryOrderNO,queryStartTime,queryEndTime,queryBillType,pageIndex,pageSize);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsMap.put("foodConsumePageParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodConsumePageParam).getBytes()));
        }
        Log.d(TAG, "limefoodConsumePage params: "  + JSON.toJSONString(deviceFoodConsumePageParam));

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodConsumePage(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<FoodConsumePageResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodConsumePageResponse> responseBaseResponse) {
                        try {
                            hideLoadingDialog();
                            Log.d(TAG, "limeFoodConsumePageResponse 340  pageIndex: " + pageIndex +  "  "+ JSON.toJSONString(responseBaseResponse));
                            //FileUtils.saveContentToSdcard("foodConsumePage.txt",JSON.toJSONString(responseBaseResponse) + "\n\n");
                            srlRecordList.finishRefresh();
                            srlRecordList.finishLoadMore();
                            FoodConsumePageResponse responseData = responseBaseResponse.getData();
                            if (responseBaseResponse.isSuccess() && responseData != null) {

                                pageTotal = responseData.getPages();
                                List<FoodConsumeBean> dataRecords = responseData.getRecords();

                                if (dataRecords != null && !dataRecords.isEmpty()) {
                                    rvContent.setVisibility(View.VISIBLE);
                                    ll_order_empty.setVisibility(View.GONE);
                                    if (pageIndex == 1) {
                                        orderListAdapter.getData().clear();
                                        orderListAdapter.notifyDataSetChanged();
                                    }
                                    orderListAdapter.addData(dataRecords);
                                } else {
                                    Log.d(TAG, "limeFoodConsumePageResponse: " + 358);
                                    if (pageIndex == 1) {
                                        orderListAdapter.getData().clear();
                                        orderListAdapter.notifyDataSetChanged();
                                        rvContent.setVisibility(View.GONE);
                                        ll_order_empty.setVisibility(View.VISIBLE);
                                    }else {
                                        AppToast.toastMsg("没有更多数据了");
                                    }

                                }
                            } else {
                                if (pageIndex == 1) {
                                    orderListAdapter.getData().clear();
                                    orderListAdapter.notifyDataSetChanged();
                                    rvContent.setVisibility(View.GONE);
                                    ll_order_empty.setVisibility(View.VISIBLE);

                                }else {
                                    AppToast.toastMsg("没有更多数据了");
                                }

                            }

                        }catch (Exception e){
                            Log.e(TAG, "limeFoodConsumePageResponse: " + e.getMessage());
                            AppToast.toastMsg("获取数据异常");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

    private void getOrderDetail(String consumeId) {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodConsumeDetail");
        paramsMap.put("consumeId", consumeId);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodConsumeDetail(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<FoodConsumeDetailResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodConsumeDetailResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        if (responseBaseResponse.isSuccess() && responseBaseResponse.getData() != null) {

                            Log.d(TAG, "limeFoodConsumeDetailResponse: " + JSON.toJSONString(responseBaseResponse));
                            MainApplication.foodConsumeDetailResponse = responseBaseResponse.getData();

                            if (orderAlertDialogFragment != null){
                                orderAlertDialogFragment.dismiss();
                            }
                            orderAlertDialogFragment = OrderAlertDialogFragment.build(responseBaseResponse.getData())
                                    .setAlertTitleTxt("订单详情")
                                    .setLeftNavTxt("打印小票")
                                    .setRightNavTxt("退款");

                            orderAlertDialogFragment.show(mActivity);

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }


    private void foodBillStatDayPage() {

        if (pageIndexRight > 0 && pageIndexRight >= (pageTotalRight - 1)){
            return;
        }

        queryName = set_query_name.getEtGoodsSearch().getText().toString().trim();
        if (TextUtils.isEmpty(queryName)){
            queryName = null;
        }

        queryOrderNO = set_query_order.getEtGoodsSearch().getText().toString().trim();
        if (TextUtils.isEmpty(queryOrderNO)){
            queryOrderNO = null;
        }

        queryStartTime = stv_start_date.getText().toString().trim();
        if (TextUtils.isEmpty(queryStartTime)){
            queryStartTime = null;
        }else {
            queryStartTime += " 00:00:00";
        }

        queryEndTime = stv_end_date.getText().toString().trim();
        if (TextUtils.isEmpty(queryEndTime)){
            queryEndTime = null;
        }else {
            queryEndTime += " 23:59:59";
        }

        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodBillStatDayPage");
        DeviceFoodBillStatPageParam deviceFoodConsumePageParam = new DeviceFoodBillStatPageParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                queryName,queryOrderNO,queryStartTime,queryEndTime,queryBillType,pageIndexRight,pageSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsMap.put("foodBillStatPageParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodConsumePageParam).getBytes()));
        }


        Log.i(TAG, "limeFoodBillStatDayPageResponse params: " + JSON.toJSONString(deviceFoodConsumePageParam));

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodBillStatDayPage(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<FoodBillStatDayPageResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodBillStatDayPageResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();

                        Log.d(TAG, "limeFoodBillStatDayPageResponse: " + JSON.toJSONString(responseBaseResponse));
                        FoodBillStatDayPageResponse responseData = responseBaseResponse.getData();
                        if (responseBaseResponse.isSuccess() && responseData != null) {
                            if (responseData.getActualTotalConsumeFee() != null) {
                                tv_ysze.setText(PriceUtils.formatPrice(responseData.getActualTotalConsumeFee().getAmount()));
                            }else {
                                tv_ysze.setText(PriceUtils.formatPrice(0.00));
                            }

                            if (responseData.getActualTotalRefundFee() != null) {
                                tv_ssze.setText(PriceUtils.formatPrice(responseData.getActualTotalRefundFee().getAmount()));
                            }else {
                                tv_ssze.setText(PriceUtils.formatPrice(0.00));
                            }



                            tv_ddzs.setText(responseData.getActualTotalOrderNoCount() + "");
                            tv_spzs.setText(responseData.getActualTotalFoodCount() + "");

//                            if (tv_ysze.getText().toString().trim().length() > 7 || tv_ssze.getText().toString().trim().length() > 7 || tv_ddzs.getText().toString().trim().length() > 7 || tv_spzs.getText().toString().trim().length() > 7){
//                                tv_ysze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                                tv_ssze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                                tv_ddzs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                                tv_spzs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                            }

                            pageTotalRight = responseData.getTotalPage();
                            List<FoodBillRecords> dataRecords = responseData.getRecords();
                            if (dataRecords != null && !dataRecords.isEmpty()) {
                                //orderListAdapter.addDataList(dataRecords);
                                rv_content_right.setVisibility(View.VISIBLE);
                                ll_order_right_empty.setVisibility(View.GONE);
                                foodListShowAdapter.setNewInstance(responseData.getRecords());
                            } else {
                                if (pageIndexRight == 0) {
                                    foodListShowAdapter.getData().clear();
                                    foodListShowAdapter.notifyDataSetChanged();
                                    rv_content_right.setVisibility(View.GONE);
                                    ll_order_right_empty.setVisibility(View.VISIBLE);

                                }
                            }
                        } else {
                            if (pageIndexRight == 0) {
                                foodListShowAdapter.getData().clear();
                                foodListShowAdapter.notifyDataSetChanged();
                                rv_content_right.setVisibility(View.GONE);
                                ll_order_right_empty.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }







    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.stv_query){
            pageIndex = 1;
            pageTotal = 1;
            pageIndexRight = 0;
            showLoadingDialog();
            getOrderList();
            foodBillStatDayPage();

        }else if (v.getId() == R.id.stv_reset){
            pageIndex = 1;
            pageTotal = 1;
            pageIndexRight = 0;
            showLoadingDialog();
            set_query_name.getEtGoodsSearch().setText("");
            set_query_order.getEtGoodsSearch().setText("");
            stv_start_date.setText(TimeUtils.getLastDateFormatted());
            stv_end_date.setText(TimeUtils.getCurrentDateFormatted());
            getOrderList();
            foodBillStatDayPage();
        } else if (v.getId() == R.id.stv_start_date){
            set_query_name.hideSearchGoodsList();
            set_query_order.hideSearchGoodsList();
            String[] date = stv_start_date.getText().toString().trim().split("-");
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);

            if (datepickerDialogFragment != null){
                datepickerDialogFragment.dismiss();
            }

            datepickerDialogFragment = DatepickerDialogFragment.build(year,month,day)
                    .setAlertTitleTxt(TimeUtils.getCurrentDateWithChineseDay())
                    .setLeftNavTxt("确认")
                    .setRightNavTxt("取消")
                    .setOnDateSetListener(new DatepickerDialogFragment.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatepickerDialogFragment dialogFragment, int year, int month, int dayOfMonth) {
                            String monthStr = (month) > 9 ? "" +(month) : "0" + (month);
                            String dayStr = dayOfMonth > 9 ? "" + dayOfMonth : "0" + dayOfMonth;
                            try{
                                long startTime = Long.parseLong(year + monthStr + dayStr);
                                long endTime = Long.parseLong(stv_end_date.getText().toString().trim().replaceAll("-",""));
                                Log.i(TAG, "limeonDateSet:  startTime: " + startTime + "  endTime: " + endTime);
                                if (startTime > endTime){
                                    AppToast.toastMsg("开始时间不能大于结束时间!");
                                    return;
                                }

                                if (TimeUtils.getDaysBetween(year + monthStr + dayStr,stv_end_date.getText().toString().trim().replaceAll("-","")) > 31){
                                    AppToast.toastMsg("时间跨度不能超过31天!");
                                    return;
                                }

                            }catch (Exception e){

                            }
                            stv_start_date.setText(year + "-" + monthStr + "-" + dayStr);
                            dialogFragment.dismiss();
                        }
                    });
            datepickerDialogFragment.show(mActivity);


        } else if (v.getId() == R.id.stv_end_date){
            set_query_name.hideSearchGoodsList();
            set_query_order.hideSearchGoodsList();
            String[] date = stv_end_date.getText().toString().trim().split("-");
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);

            if (datepickerDialogFragment != null){
                datepickerDialogFragment.dismiss();
            }

            datepickerDialogFragment = DatepickerDialogFragment.build(year,month,day)
                    .setAlertTitleTxt(TimeUtils.getCurrentDateWithChineseDay())
                    .setLeftNavTxt("确认")
                    .setRightNavTxt("取消")
                    .setOnDateSetListener(new DatepickerDialogFragment.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatepickerDialogFragment dialogFragment, int year, int month, int dayOfMonth) {
                            String monthStr = (month) > 9 ? "" +(month) : "0" + (month);
                            String dayStr = dayOfMonth > 9 ? "" + dayOfMonth : "0" + dayOfMonth;
                            try{
                                long startTime = Long.parseLong(stv_start_date.getText().toString().trim().replaceAll("-",""));
                                long endTime = Long.parseLong(year + monthStr + dayStr);
                                Log.d(TAG, "limeonDateSet:  startTime: " + startTime + "  endTime: " + endTime);
                                if (endTime < startTime){
                                    AppToast.toastMsg("结束时间不能小于开始时间!");
                                    return;
                                }

                                if (TimeUtils.getDaysBetween(stv_start_date.getText().toString().trim().replaceAll("-",""),year + monthStr + dayStr) > 31){
                                    AppToast.toastMsg("时间跨度不能超过31天!");
                                    return;
                                }

                            }catch (Exception e){

                                Log.e(TAG, "limeonDateSet: " + e.getMessage());
                            }
                            stv_end_date.setText(year + "-" + monthStr + "-" + dayStr);
                            dialogFragment.dismiss();
                        }
                    });
            datepickerDialogFragment.show(mActivity);


        }
    }
}
