package com.stkj.plate.weight.base.ui.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.common.utils.BigDecimalUtils;

public class FoodListShowAdapter extends BaseQuickAdapter<FoodInfoTable, BaseViewHolder> {

    private Context context;

    public FoodListShowAdapter(Context context)
    {
        super(R.layout.item_list_food_show);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, FoodInfoTable item) {
        holder.setText(R.id.tv_show_item_name, item.getName());
        holder.setText(R.id.tv_show_item_price, PriceUtils.formatPrice(item.getUnitPriceMoney_amount()));
        if (item.getPricingMethod() == 1){
            holder.setText(R.id.tv_show_item_count, item.getStandardGoodsCount());
        }else {
            holder.setText(R.id.tv_show_item_count, item.getWeightGoodsCount() + "kg");
        }

        double inputGoodsCount = item.isWeightGoods() ? item.getWeightGoodsCountWithDouble() : item.getStandardGoodsCountWithInt();
        double inputGoodsInitPrice = item.getInputGoodsInitPriceWithDouble();
        double totalPrice = BigDecimalUtils.mul(inputGoodsInitPrice, inputGoodsCount);
        holder.setText(R.id.tv_show_item_total_price, PriceUtils.formatPrice(totalPrice));


    }
}
