package com.stkj.cashier.pay.ui.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.stkj.cashier.R;
import com.stkj.cashier.pay.model.FoodConsumeBean;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 自动搜索列表
 */
public class OrderNoAutoSearchInfoViewHolder extends CommonRecyclerViewHolder<FoodConsumeBean> {

    public final static String TAG = "OrderNoAutoSearchInfoViewHolder";
    private TextView tvGoodsName;
    private View divider;

    public OrderNoAutoSearchInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        divider = findViewById(R.id.divider);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(FoodConsumeBean data) {


        Log.d(TAG, "limeFoodConsumeBean: " + JSON.toJSONString(data));

        tvGoodsName.setText(TextUtils.isEmpty(data.getBillNo()) ? data.getOrderNo() : data.getBillNo());
        if (getDataPosition() == (getmDataAdapter().getDataList().size() - 1)){
            divider.setVisibility(View.GONE);
        }else {
            divider.setVisibility(View.VISIBLE);
        }
//        tvGoodsQrcode.setText("条码: " + data.getGoodsCode());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodConsumeBean> {
        @Override
        public CommonRecyclerViewHolder<FoodConsumeBean> createViewHolder(View itemView) {
            return new OrderNoAutoSearchInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_order_auto_search_info;
        }

        @Override
        public Class<FoodConsumeBean> getItemDataClass() {
            return FoodConsumeBean.class;
        }
    }
}
