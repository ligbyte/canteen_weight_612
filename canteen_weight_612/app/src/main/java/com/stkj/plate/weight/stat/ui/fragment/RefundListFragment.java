package com.stkj.plate.weight.stat.ui.fragment;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.stkj.plate.weight.MainApplication;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.dialog.CommonRefundAlertDialogFragment;
import com.stkj.plate.weight.base.ui.dialog.OrderRefundAlertDialogFragment;
import com.stkj.plate.weight.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.plate.weight.base.utils.CommonDialogUtils;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.pay.data.PayConstants;
import com.stkj.plate.weight.pay.model.ConsumeFoodBean;
import com.stkj.plate.weight.pay.model.FoodConsumeDetailResponse;
import com.stkj.plate.weight.pay.model.FoodConsumeRefundResponse;
import com.stkj.plate.weight.pay.model.RefreshConsumerRefundModeEvent;
import com.stkj.plate.weight.pay.model.RefreshRefundGoodsEvent;
import com.stkj.plate.weight.pay.model.TTSSpeakEvent;
import com.stkj.plate.weight.stat.model.DeviceFoodRefundParam;
import com.stkj.plate.weight.stat.model.RefundFoodInfo;
import com.stkj.plate.weight.stat.service.StatService;
import com.stkj.plate.weight.stat.ui.adapter.FoodRefundListAdapter;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.TreeMap;

/**
 * 退款页面
 */
public class RefundListFragment extends BaseRecyclerFragment implements View.OnClickListener{

    public final static String TAG = "RefundListFragment";
    private AppSmartRefreshLayout srlRecordList;
    private ImageView iv_add_goods_back;
    private ShapeTextView stv_cancel_add;
    private ShapeTextView stv_save_storage;
    private ShapeTextView stv_refund_record;
    private TextView tv_ddzs;
    private TextView tv_spzs;
    private TextView tv_total_count;
    private TextView tv_total_money;
    private ImageView iv_select_all;
    private LinearLayout ll_select_all;
    private RecyclerView rvContent;
    private FoodRefundListAdapter foodRefundListAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_refund_list;
    }

    @Override
    protected void initViews(View rootView) {
        iv_add_goods_back =  rootView.findViewById(R.id.iv_add_goods_back);
        stv_cancel_add =  rootView.findViewById(R.id.stv_cancel_add);
        stv_save_storage =  rootView.findViewById(R.id.stv_save_storage);
        iv_add_goods_back.setOnClickListener(this);
        stv_cancel_add.setOnClickListener(this);
        stv_save_storage.setOnClickListener(this);
        rvContent = rootView.findViewById(R.id.rv_goods_list);
        iv_select_all =  rootView.findViewById(R.id.iv_select_all);
        ll_select_all =  rootView.findViewById(R.id.ll_select_all);
        tv_total_count =  rootView.findViewById(R.id.tv_total_count);
        tv_total_money =  rootView.findViewById(R.id.tv_total_money);
        stv_refund_record  =  rootView.findViewById(R.id.stv_refund_record);
        stv_refund_record.setOnClickListener(this);

        foodRefundListAdapter = new FoodRefundListAdapter(getActivity());
        foodRefundListAdapter.addChildClickViewIds(R.id.ll_checkbox);
        foodRefundListAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (foodRefundListAdapter.getData().get(position).getStandardGoodsCountWithInt() <= 0){
                    AppToast.toastMsg("该商品已全部退款");
                    EventBus.getDefault().post(new TTSSpeakEvent("该商品已全部退款"));
                    return;
                }

                foodRefundListAdapter.getData().get(position).setSelected(!foodRefundListAdapter.getData().get(position).isSelected());

                int selectCount = 0;

                for (int i = 0; i < foodRefundListAdapter.getData().size(); i++) {
                    if (foodRefundListAdapter.getData().get(i).isSelected()){
                        selectCount += 1;
                    }
                }

                if (foodRefundListAdapter.getData().size() == selectCount){
                    iv_select_all.setImageResource(R.mipmap.icon_goods_selected);
                }else {
                    iv_select_all.setImageResource(R.mipmap.icon_goods_unselect);
                }

                if (foodRefundListAdapter.getData().get(position).getFoodMethod() == 1 && foodRefundListAdapter.getData().get(position).getStandardGoodsCount().equals("0")) {
                    foodRefundListAdapter.getData().get(position).setStandardGoodsCount(String.valueOf(foodRefundListAdapter.getData().get(position).getFoodCount() - foodRefundListAdapter.getData().get(position).getRefundFoodCount()));
                }
                Log.e(TAG, "limefoodRefundListAdapter 151: " + JSON.toJSONString(foodRefundListAdapter.getData().get(position)));
                foodRefundListAdapter.notifyItemChanged(position);
                refreshTotalPrice();
            }
        });

        foodRefundListAdapter.setFoodRefundListAdapterListener(new FoodRefundListAdapter.FoodRefundListAdapterListener() {
            @Override
            public void countStand(int position, String newCount) {
                if (foodRefundListAdapter.getData().get(position).getFoodMethod() == 2){
                    foodRefundListAdapter.getData().get(position).setWeightGoodsCount(newCount);
                }else {
                    foodRefundListAdapter.getData().get(position).setStandardGoodsCount(newCount);
                }
            }
        });

        foodRefundListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {


            }
        });

        rvContent.setAdapter(foodRefundListAdapter);

        for (int i = 0; i < MainApplication.foodConsumeDetailResponse.getConsumeFoodList().size(); i++) {
            //if (foodRefundListAdapter.getData().get(i).getFoodMethod() == 1 && foodRefundListAdapter.getData().get(i).getStandardGoodsCount().equals("0")) {
                MainApplication.foodConsumeDetailResponse.getConsumeFoodList().get(i).setStandardGoodsCount(String.valueOf(MainApplication.foodConsumeDetailResponse.getConsumeFoodList().get(i).getFoodCount() - MainApplication.foodConsumeDetailResponse.getConsumeFoodList().get(i).getRefundFoodCount()));
            //}
            Log.d(TAG, "limefoodConsumeDetailResponse: " + JSON.toJSONString(MainApplication.foodConsumeDetailResponse.getConsumeFoodList().get(i)));

        }

        foodRefundListAdapter.setNewInstance(MainApplication.foodConsumeDetailResponse.getConsumeFoodList());
        iv_select_all.setSelected(false);
        iv_select_all.setImageResource(R.mipmap.icon_goods_unselect);
        ll_select_all.setOnClickListener(this);


    }

    private boolean isOnPause;

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshRefundGoodsEvent(RefreshRefundGoodsEvent eventBus) {
        refreshTotalPrice();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }




    private void refund() {

        showLoadingDialog();

        double totalPrice = 0;
        List<RefundFoodInfo> refundFoodInfoList = new ArrayList<>();
        for (int i = 0; i < foodRefundListAdapter.getData().size(); i++) {
            ConsumeFoodBean consumeFoodBean = foodRefundListAdapter.getData().get(i);
           if (foodRefundListAdapter.getData().get(i).isSelected()) {
               double foodTotalPrice = Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodMethod() == 2 ? consumeFoodBean.getFoodWeight() * consumeFoodBean.getFoodUnitPriceYuan().getAmount() : consumeFoodBean.getStandardGoodsCountWithInt() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
               refundFoodInfoList.add(new RefundFoodInfo(consumeFoodBean.getFoodId(), consumeFoodBean.getFoodName(), consumeFoodBean.getStandardGoodsCountWithInt(),
                       consumeFoodBean.getFoodWeight(), consumeFoodBean.getFoodMethod(), consumeFoodBean.getFoodUnitPriceYuan().getAmount(), foodTotalPrice));
               totalPrice += Double.parseDouble(PriceUtils.formatPrice(foodTotalPrice));
           }
        }

        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodRefund");
        DeviceFoodRefundParam deviceFoodRefundParam = new DeviceFoodRefundParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                MainApplication.foodConsumeDetailResponse.getSettleType(),MainApplication.foodConsumeDetailResponse.getOrder().getId(),
                Double.parseDouble(PriceUtils.formatPrice(totalPrice)),refundFoodInfoList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsMap.put("deviceFoodRefundParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodRefundParam).getBytes()));
        }

        Log.d(TAG, "limeretund deviceFoodRefundParam: " + JSON.toJSONString(deviceFoodRefundParam));

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(StatService.class)
                .foodRefund(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<FoodConsumeDetailResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodConsumeDetailResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        if (responseBaseResponse.isSuccess()) {
                            AppToast.toastMsg("退款成功");
                            EventBus.getDefault().post(new TTSSpeakEvent("退款成功"));
                            EventBus.getDefault().post(new RefreshConsumerRefundModeEvent(0));
                        }else {
                            AppToast.toastMsg(responseBaseResponse.getMessage());
                            EventBus.getDefault().post(new TTSSpeakEvent(responseBaseResponse.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        CommonDialogUtils.showTipsDialog(mActivity, "退款失败!" + e.getMessage());
                    }
                });
    }


    private void foodRefundList(String consumeId) {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodRefundList");
        paramsMap.put("consumeId", consumeId);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(StatService.class)
                .foodRefundList(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<FoodConsumeRefundResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodConsumeRefundResponse> responseBaseResponse) {
                        hideLoadingDialog();
                        if (responseBaseResponse.isSuccess() && responseBaseResponse.getData() != null && responseBaseResponse.getData().getRefundFoodList() != null && responseBaseResponse.getData().getRefundFoodList().size() > 0) {
                            OrderRefundAlertDialogFragment.build(responseBaseResponse.getData())
                                    .setAlertTitleTxt("退款记录")
                                    .show(mActivity);
                        }else {
                            AppToast.toastMsg("无退款记录");
                            EventBus.getDefault().post(new TTSSpeakEvent("无退款记录"));
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


    public void refreshTotalPrice() {

        double totalPrice = 0;
        int totalCount = 0;

        for (int i = 0; i < foodRefundListAdapter.getData().size(); i++) {
            ConsumeFoodBean consumeFoodBean = ((ConsumeFoodBean)(foodRefundListAdapter.getData().get(i)));
            if (consumeFoodBean.isSelected()){
                if (consumeFoodBean.isWeightGoods()) {
                    totalCount += 1;
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getFoodWeight() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
                } else {
                    totalCount += consumeFoodBean.getStandardGoodsCountWithInt();
                    totalPrice += Double.parseDouble(PriceUtils.formatPrice(consumeFoodBean.getStandardGoodsCountWithInt() * consumeFoodBean.getFoodUnitPriceYuan().getAmount()));
                    Log.d(TAG, "limerefreshTotalPrice count: " + consumeFoodBean.getStandardGoodsCountWithInt() + " price: " + consumeFoodBean.getFoodUnitPriceYuan().getAmount());
                }
            }

        }

        tv_total_count.setText("" + totalCount);
        tv_total_money.setText("￥ " + PriceUtils.formatPrice(totalPrice));

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_add_goods_back || v.getId() == R.id.stv_cancel_add){
            EventBus.getDefault().post(new RefreshConsumerRefundModeEvent(0));

        }else if (v.getId() == R.id.stv_save_storage){
            int selectCount = 0;

            for (int i = 0; i < foodRefundListAdapter.getData().size(); i++) {
                if (foodRefundListAdapter.getData().get(i).isSelected()){
                    selectCount += 1;
                }
            }

            if (selectCount > 0){
                CommonRefundAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt((MainApplication.foodConsumeDetailResponse.getSettleType() == PayConstants.PAY_TYPE_CASH)?"该订单为现金支付，请确认已线下完成现金退款？" : "退款完成后，您在本单实际支付的金额将原路返还，若返还失败，请前往钱包余额中查看。 是否确认退款？")
                        .setLeftNavTxt("确认")
                        .setLeftNavClickListener(new CommonRefundAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonRefundAlertDialogFragment alertDialogFragment) {
                                refund();
                            }
                        })
                        .setRightNavTxt("取消").show(getActivity());


            }else {
                AppToast.toastMsg("请选择退款商品");
                EventBus.getDefault().post(new TTSSpeakEvent("请选择退款商品"));
            }



        } else if (v.getId() == R.id.ll_select_all){

            iv_select_all.setSelected(!iv_select_all.isSelected());
            for (int i = 0; i < foodRefundListAdapter.getData().size(); i++) {
                if (foodRefundListAdapter.getData().get(i).getStandardGoodsCountWithInt() > 0) {
                    foodRefundListAdapter.getData().get(i).setSelected(iv_select_all.isSelected());
                }

            }

            if (iv_select_all.isSelected()){
                iv_select_all.setImageResource(R.mipmap.icon_goods_selected);
            }else {
                iv_select_all.setImageResource(R.mipmap.icon_goods_unselect);
            }

            foodRefundListAdapter.notifyDataSetChanged();
            refreshTotalPrice();

        } else if (v.getId() == R.id.stv_refund_record){

            foodRefundList(MainApplication.foodConsumeDetailResponse.getOrder().getId());

        }
    }
}
