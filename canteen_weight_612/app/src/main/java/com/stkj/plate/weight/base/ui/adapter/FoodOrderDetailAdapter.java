package com.stkj.plate.weight.base.ui.adapter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.pay.model.ConsumeFoodBean;

public class FoodOrderDetailAdapter extends BaseQuickAdapter<ConsumeFoodBean, BaseViewHolder> {

    public final static String TAG = "FoodOrderDetailAdapter";
    private Context context;

    public FoodOrderDetailAdapter(Context context)
    {
        super(R.layout.item_order_food_detail);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, ConsumeFoodBean item) {
        try {
            Log.d(TAG, "limeFoodOrderDetailAdapter item: " + JSON.toJSONString(item));
            holder.setText(R.id.tv_name, item.getFoodName());
            Log.d(TAG, "limeconvert: " + item.getFoodName());
            holder.setText(R.id.tv_spdj, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount()));

            if (item.getFoodMethod() == 2) {
                holder.setText(R.id.tv_spjs, String.valueOf(item.getFoodCount()));
                holder.setText(R.id.tv_ys, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodWeight()));
                holder.setText(R.id.tv_ss, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodWeight()));
                holder.setText(R.id.tv_tkjs, String.valueOf(item.getRefundFoodCount()));
                holder.setText(R.id.tv_tkje,PriceUtils.formatPrice(item.getRefundFoodCount() * item.getFoodUnitPriceYuan().getAmount() * item.getFoodWeight()));
            } else {
                holder.setText(R.id.tv_spjs, String.valueOf(item.getFoodCount()));
                holder.setText(R.id.tv_ys, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodCount()));
                holder.setText(R.id.tv_ss, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount() * item.getFoodCount()));
                holder.setText(R.id.tv_tkjs, String.valueOf(item.getRefundFoodCount()));
                holder.setText(R.id.tv_tkje, PriceUtils.formatPrice(item.getRefundFoodCount() * item.getFoodUnitPriceYuan().getAmount()));
            }

        }catch (Exception e){
            Log.e(TAG, "limeFoodOrderDetailAdapter: " + e.getMessage());
        }

    }
}
