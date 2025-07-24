package com.stkj.plate.weight.base.ui.adapter;

import android.content.Context;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.pay.model.ConsumeFoodBean;

public class FoodOrderRefundAdapter extends BaseQuickAdapter<ConsumeFoodBean, BaseViewHolder> {

    public final static String TAG = "FoodOrderDetailAdapter";
    private Context context;

    public FoodOrderRefundAdapter(Context context)
    {
        super(R.layout.item_order_food_detail);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, ConsumeFoodBean item) {
        try {
//            holder.setText(R.id.tv_name, item.getFoodName());
//            holder.setText(R.id.tv_spdj, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount()));
//            if (item.getFoodMethod() == 2) {
//                holder.setText(R.id.tv_spjs, String.valueOf(item.getFoodWeight()));
//                holder.setText(R.id.tv_ys, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodWeight()));
//                holder.setText(R.id.tv_ss, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodWeight()));
//                holder.setText(R.id.tv_tkjs, String.valueOf(item.getRefundFoodCount()));
//                holder.setText(R.id.tv_tkje, PriceUtils.formatPrice(item.getRefundFoodPriceYuan().getAmount()));
//            } else {
//                holder.setText(R.id.tv_spjs, String.valueOf(item.getFoodCount()));
//                holder.setText(R.id.tv_ys, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodCount()));
//                holder.setText(R.id.tv_ss, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodCount()));
//                holder.setText(R.id.tv_tkjs, String.valueOf(item.getRefundFoodCount()));
//                holder.setText(R.id.tv_tkje, PriceUtils.formatPrice(item.getRefundFoodPriceYuan() == null ? 0 : item.getRefundFoodPriceYuan().getAmount()));
//            }

        }catch (Exception e){
            Log.e(TAG, "limeFoodOrderDetailAdapter: " + e.getMessage());
        }

    }
}
