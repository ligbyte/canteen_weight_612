package com.stkj.cashier.base.ui.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.cashier.R;
import com.stkj.cashier.base.model.FoodItemEntity;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.pay.model.FoodBillRecords;

public class FoodListStatAdapter extends BaseQuickAdapter<FoodBillRecords, BaseViewHolder> {

    private Context context;

    public FoodListStatAdapter(Context context)
    {
        super(R.layout.item_list_stat);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, FoodBillRecords item) {
        holder.setText(R.id.tv_order_time, item.getBillDate().replace(" 00:00:00",""));
        holder.setText(R.id.tv_order_count, (item.getOrderCount() - item.getRefundOrderCount()) + "");
        holder.setText(R.id.tv_order_food_count, (item.getFoodCount() - item.getRefundFoodCount()) + "");
        holder.setText(R.id.tv_order_ys, PriceUtils.formatPrice(item.getConsumeFee()));
        holder.setText(R.id.tv_order_ss, PriceUtils.formatPrice(item.getActualFee()));

    }
}
