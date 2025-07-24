package com.stkj.plate.weight.stat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.base.utils.StarUtils;
import com.stkj.plate.weight.pay.model.FoodConsumeBean;

public class FoodOrderListAdapter extends BaseQuickAdapter<FoodConsumeBean, BaseViewHolder> {

    private Context context;

    public FoodOrderListAdapter(Context context)
    {
        super(R.layout.item_stat_consumer_record_list_goods);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, FoodConsumeBean item) {
            holder.setText(R.id.tv_name, !TextUtils.isEmpty(item.getCustomerName()) ? item.getCustomerName() : "--");
            holder.setText(R.id.tv_account, TextUtils.isEmpty(item.getBillNo()) ? StarUtils.phoneStar(item.getOrderNo()) :StarUtils.phoneStar(item.getBillNo()));
            if (item.getCustomerFeeYuan() == null){
                holder.setText(R.id.tv_amount, PriceUtils.formatPrice(item.getCustomerFee()));
            } else {
                holder.setText(R.id.tv_amount, PriceUtils.formatPrice(item.getCustomerFeeYuan().getAmount()));
            }

            holder.setText(R.id.tv_fee_type, String.valueOf(item.getNumber()));
            holder.setText(R.id.tv_time, item.getCreateTime());
            holder.setText(R.id.tv_pay_type, "查看详情");
    }
}
