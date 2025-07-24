package com.stkj.cashier.stat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.cashier.R;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.base.utils.StarUtils;
import com.stkj.cashier.pay.model.FoodConsumeBean;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.common.utils.BigDecimalUtils;

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
