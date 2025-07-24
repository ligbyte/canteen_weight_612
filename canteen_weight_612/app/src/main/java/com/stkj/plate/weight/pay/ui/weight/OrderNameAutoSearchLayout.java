package com.stkj.plate.weight.pay.ui.weight;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.device.DeviceManager;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.widget.CommonActionDoneEditText;
import com.stkj.plate.weight.pay.goods.callback.OrderNameAutoSearchListener;
import com.stkj.plate.weight.pay.model.DeviceFoodConsumePageParam;
import com.stkj.plate.weight.pay.model.FoodConsumeBean;
import com.stkj.plate.weight.pay.model.FoodConsumePageResponse;
import com.stkj.plate.weight.pay.service.PayService;
import com.stkj.plate.weight.pay.ui.adapter.OrderNameAutoSearchInfoViewHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceModel;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceRecyclerViewHolder;
import com.stkj.common.utils.KeyBoardUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class OrderNameAutoSearchLayout extends FrameLayout {
    
    public final static String TAG = "OrderNameAutoSearchLayout";
    private RecyclerView rvSearchContent;
    private CommonActionDoneEditText etGoodsSearch;
    private CommonRecyclerAdapter commonRecyclerAdapter;
    private LifecycleOwner lifecycleOwner;
    private OrderNameAutoSearchListener goodsAutoSearchListener;
    private String mLastSearchKey;
    private ImageView ivClearSearch;
    private int billType = 0;

    public OrderNameAutoSearchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public OrderNameAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderNameAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_name_auto_search, this);
        ivClearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        ivClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoodsSearch.setText("");
            }
        });
        etGoodsSearch = (CommonActionDoneEditText) findViewById(R.id.et_goods_search);
        etGoodsSearch.setOnEditWatchListener(new CommonActionDoneEditText.OnEditWatchListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    ivClearSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAfterTextChanged(String input) {
                if (TextUtils.isEmpty(input)) {
                    mLastSearchKey = "";
                    rvSearchContent.setVisibility(GONE);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (billType != 1) {
                                autoSearch(input, false);
                            }
                        }
                    }, 100);
                }
            }

            @Override
            public void onActionDone() {
                KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
            }

            @Override
            public void onFocusChange(boolean hasFocus) {
                if (hasFocus) {
                    String currentTxt = etGoodsSearch.getText().toString();
                    if (!TextUtils.isEmpty(currentTxt)) {
                        rvSearchContent.setVisibility(VISIBLE);
                    } else {
                        rvSearchContent.setVisibility(GONE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
//                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent = (RecyclerView) findViewById(R.id.rv_search_content);
        commonRecyclerAdapter = new CommonRecyclerAdapter(false);
        commonRecyclerAdapter.addViewHolderFactory(new OrderNameAutoSearchInfoViewHolder.Factory());
        commonRecyclerAdapter.addViewHolderFactory(new PlaceRecyclerViewHolder.Factory(R.layout.item_goods_auto_search_empty));
        commonRecyclerAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                if (obj instanceof FoodConsumeBean) {
                    if (goodsAutoSearchListener != null) {
                        goodsAutoSearchListener.onStartGetGoodsItemDetail((FoodConsumeBean) obj);
                    }
                    KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
                    hideSearchGoodsList();
                } else if (obj instanceof PlaceModel) {
                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent.setAdapter(commonRecyclerAdapter);
    }

    public void setGoodsAutoSearchListener(LifecycleOwner lifecycleOwner, OrderNameAutoSearchListener autoSearchItemListener) {
        this.lifecycleOwner = lifecycleOwner;
        this.goodsAutoSearchListener = autoSearchItemListener;
    }

    /**
     * @param key                搜索 key
     * @param autoGetGoodsDetail 自动调用获取商品详细信息
     */
    public void autoSearch(String key, boolean autoGetGoodsDetail) {
        if (lifecycleOwner == null) {
            return;
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        //已经在搜索了
        if (!autoGetGoodsDetail && TextUtils.equals(key, mLastSearchKey)) {
            return;
        }
        //注释掉扫码枪扫商品条码不填充数据
//        if (autoGetGoodsDetail) {
//            etGoodsSearch.setText(key);
//        }
        mLastSearchKey = key;

        getOrderList(key);

    }


    private void getOrderList(String queryName) {


        if (TextUtils.isEmpty(queryName)){
            queryName = null;
        }

        Log.d(TAG, "limefoodConsumePage: " + 293);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodConsumePage");
        DeviceFoodConsumePageParam deviceFoodConsumePageParam = new DeviceFoodConsumePageParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                queryName,null,null,null,billType,0,50);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsMap.put("foodConsumePageParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodConsumePageParam).getBytes()));
        }
        Log.d(TAG, "limefoodConsumePage 313: "  + queryName);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodConsumePage(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<FoodConsumePageResponse>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodConsumePageResponse> responseBaseResponse) {
                        try {
                            Log.d(TAG, "limeFoodConsumePageResponse 202: " + JSON.toJSONString(responseBaseResponse));

                            FoodConsumePageResponse responseData = responseBaseResponse.getData();
                            if (responseBaseResponse.isSuccess() && responseData != null) {

                                Set<String> seenNames = new HashSet<>();
                                List<FoodConsumeBean> dataRecords = responseData.getRecords();

                                if (dataRecords != null && !dataRecords.isEmpty()) {
                                    List<FoodConsumeBean> dataRecordsTemp = new ArrayList<>();
                                    for  (FoodConsumeBean foodConsumeBean : dataRecords) {
                                        if (seenNames.add(foodConsumeBean.getCustomerName())) {
                                            dataRecordsTemp.add(foodConsumeBean);
                                        }
                                    }
                                    showSearchGoodsList(dataRecordsTemp);

                                } else {
                                    showSearchGoodsList(null);

                                }
                            } else {


                            }

                        }catch (Exception e){
                            Log.e(TAG, "limeFoodConsumePageResponse: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void showSearchGoodsList(List<FoodConsumeBean> dataList) {
        rvSearchContent.setVisibility(VISIBLE);
        commonRecyclerAdapter.removeAllData();
        if (dataList == null || dataList.isEmpty()) {
            commonRecyclerAdapter.addData(new PlaceModel(R.layout.item_goods_auto_search_empty, true));
        } else {
            commonRecyclerAdapter.addDataList(dataList);
        }
    }

    public CommonActionDoneEditText getEtGoodsSearch() {
        return etGoodsSearch;
    }

    public void hideSearchGoodsList() {
        rvSearchContent.setVisibility(GONE);
        etGoodsSearch.clearFocus();
    }

    public void clearSearchKey() {
        etGoodsSearch.setText("");
        etGoodsSearch.clearFocus();
        rvSearchContent.setVisibility(GONE);
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }
}
